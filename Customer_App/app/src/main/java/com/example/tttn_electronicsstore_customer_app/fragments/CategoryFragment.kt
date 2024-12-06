package com.example.tttn_electronicsstore_customer_app.fragments

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.GridLayoutManager
import com.example.tttn_electronicsstore_customer_app.adapters.ProductAdpater
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentCategoryBinding
import com.example.tttn_electronicsstore_customer_app.helper.hideBottomNavigation
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var binding: FragmentCategoryBinding
    private val productViewModel by viewModels<ProductViewModel>()
    private var productAdapter: ProductAdpater = ProductAdpater()
    private lateinit var category: Category
    private var type = "default"
    private var orderType = "default"
    private var offset = 0
    private var pageSize = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        val b = arguments
        if (b != null) {
            category = b.getSerializable("category") as Category
            setUp()
        }
    }

    private fun initSpinner() {
        val mapData: Map<String, String> = mapOf(
            "Key1" to "Mặc định",
            "Key2" to "Giá tăng dần",
            "Key3" to "Giá giảm dần",
            "Key4" to "Số sao tăng dần",
            "Key5" to "Số sao giảm dần",
        )

        val spinnerData = mapData.map { Pair(it.key, it.value) }

        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.simple_spinner_item,
            spinnerData.map { it.second }
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spOrder.adapter = arrayAdapter
        binding.spOrder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    type = "default"
                    orderType = "default"
                } else if (p2 == 1) {
                    type = "price"
                    orderType = "ASC"
                } else if (p2 == 2) {
                    type = "price"
                    orderType = "DESC"
                } else if (p2 == 3) {
                    type = "review"
                    orderType = "ASC"
                } else if (p2 == 4) {
                    type = "review"
                    orderType = "DESC"
                }
                offset = 0
                productViewModel.getPageProduct(category.id, type, orderType, offset, pageSize)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun setUp() {
        initSpinner()

        binding.apply {
            tvCategoryName.text = category.name
            rvProduct.layoutManager = GridLayoutManager(requireContext(), 2)
            rvProduct.adapter = productAdapter
            productAdapter.setOnItemClickListener(object : ProductAdpater.OnItemClickListener {
                override fun onItemClick(product: Product) {
                    val b = Bundle()
                    b.putInt("productId", product.id)
                   findNavController().navigate(com.example.tttn_electronicsstore_customer_app.R.id.action_categoryFragment_to_productFragment, b)
                }

            })
        }

        lifecycleScope.launch {
            productViewModel.getPageProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        loadingStatus()
                    }

                    is Resource.Success -> {
                        productAdapter.differ.submitList(it.data)
                        successStatus(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

    }

    private fun successStatus(productList: List<Product>) {
        binding.apply {
            progressBar.visibility = View.GONE
            rvProduct.visibility = View.VISIBLE
            layoutBtn.visibility = View.VISIBLE


            if (productList.size < 6) {
                btnNext.visibility = View.GONE
            } else {
                btnNext.visibility = View.VISIBLE
            }

            if (offset > 0) {
                btnPrevious.visibility = View.VISIBLE
            } else {
                btnPrevious.visibility = View.GONE
            }


            btnNext.setOnClickListener {
                offset += 1
                productViewModel.getPageProduct(category.id, type, orderType, offset, pageSize)
            }
            btnPrevious.setOnClickListener {
                offset -= 1
                productViewModel.getPageProduct(category.id, type, orderType, offset, pageSize)
            }
        }


    }

    private fun loadingStatus() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            rvProduct.visibility = View.GONE
            layoutBtn.visibility = View.GONE
        }
    }


}