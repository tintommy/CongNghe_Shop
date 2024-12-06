package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductManagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductSearchAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLySanPhamBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuanLySanPhamFragment : Fragment() {
    private lateinit var binding: FragmentQuanLySanPhamBinding
    private val productViewModel by viewModels<ProductViewModel>()
    private lateinit var productAdapter: ProductManagerAdapter
    private lateinit var searchProductAdapter: ProductSearchAdapter
    private var page = 0

    var querySearch: String = ""
    val delayTime: Long = 700

    val handler = Handler()

    val search = Runnable {
        if (querySearch != "") {
            productViewModel.searchProduct(querySearch)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLySanPhamBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initApdapter()
        eventManager()

        productViewModel.getPage(page)

        binding.btnAdd.setOnClickListener {

            (activity as AdminActivity).replaceFragment(AddProductFragment())
        }

        binding.btnNext.setOnClickListener {
            page += 1
            productViewModel.getPage(page)
        }
        binding.btnPrevious.setOnClickListener {
            page -= 1
            productViewModel.getPage(page)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0?.toString().equals("") || p0 == null) {
                    searchProductAdapter.differ.submitList(ArrayList<Product>())
                    searchProductAdapter.notifyDataSetChanged()
                    binding.rvProductSearch.visibility = View.GONE
                    querySearch = ""
                }

                if (p0 != null && !p0.equals("")) {
                    querySearch = p0.toString()
                    handler.removeCallbacks(search)
                    handler.postDelayed(search, delayTime)

                }
            }
        })
    }

    private fun eventManager() {
        lifecycleScope.launch {
            productViewModel.getPageProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        binding.btnPrevious.visibility = View.VISIBLE
                        binding.btnNext.visibility = View.VISIBLE

                        productAdapter.differ.submitList(it.data)
                        productAdapter.notifyDataSetChanged()

                        if (page == 0) {
                            binding.btnPrevious.visibility = View.GONE
                        }

                        if (it.data!!.size < 7) {
                            binding.btnNext.visibility = View.GONE
                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            productViewModel.deleteProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        Toast.makeText(requireContext(), "Đã xoá sản phẩm", Toast.LENGTH_LONG)
                            .show()
                        productViewModel.getPage(page)
                    }

                    is Resource.Error -> {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Không thể xoá sản phẩm do có đơn hàng liên kết")
                        builder.setTitle("Thông báo !")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.cancel()
                        }
                        val alertDialog = builder.create()
                        alertDialog.show()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            productViewModel.getSearchProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        binding.rvProductSearch.visibility = View.VISIBLE
                        searchProductAdapter.differ.submitList(it.data)
                        searchProductAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        binding.rvProductSearch.visibility = View.GONE
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initApdapter() {
        productAdapter = ProductManagerAdapter()
        binding.rvListProduct.adapter = productAdapter
        binding.rvListProduct.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        productAdapter.setOnItemClickListener(object : ProductManagerAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                val b = Bundle()
                b.putSerializable("product", product)
                val fragment = EditProductFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }

            override fun onLongClick(product: Product) {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Bạn có chắc xoá sản phẩm ${product.name}  ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton("Xoá") { dialog, which ->
                    productViewModel.deleteProduct(product)
                }

                builder.setNegativeButton("Huỷ") { dialog, which ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }

            override fun onSwitchClick(product: Product, isChecked: Boolean) {
                productViewModel.updateStatusProduct(product.id)
                product.status=isChecked
            }

            override fun onPriceHistoryClick(product: Product) {
                val b = Bundle()
                b.putSerializable("priceHistory", ArrayList(product.priceHistoryList))
                b.putString("name", product.name)
                val fragment = PriceHistoryFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }
        })


        searchProductAdapter = ProductSearchAdapter()
        binding.rvProductSearch.adapter = searchProductAdapter
        binding.rvProductSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchProductAdapter.setOnItemClickListener(object :
            ProductSearchAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                val b = Bundle()
                b.putSerializable("product", product)
                val fragment = EditProductFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }

        })

    }

}