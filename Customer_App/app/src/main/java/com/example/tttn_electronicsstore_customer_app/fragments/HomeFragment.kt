package com.example.tttn_electronicsstore_customer_app.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.CategoryAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.ProductAdpater
import com.example.tttn_electronicsstore_customer_app.adapters.ProductSearchAdpater
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentHomeBinding
import com.example.tttn_electronicsstore_customer_app.helper.showBottomNavigation
import com.example.tttn_electronicsstore_customer_app.viewModels.CategoryViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.ProductViewModel
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val categoryAdapter = CategoryAdapter()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val productViewModel by viewModels<ProductViewModel>()
    private val topNewProductAdapter = ProductAdpater()
    private val topBoughtProductAdapter = ProductAdpater()
    private val searchProductAdapter = ProductSearchAdpater()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }


    var querySearch: String = ""
    val delayTime: Long = 700

    val handler = Handler()

    val search = Runnable {
        if (querySearch != "") {
            productViewModel.searchProduct(querySearch)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()
        initAdapter()
        categoryViewModel.getAll()
        productViewModel.getTopNewProduct(7)
        productViewModel.getTopBoughtProduct(7)

        Glide.with(requireContext()).load(R.drawable.slide1).apply(
            RequestOptions().transform(RoundedCorners(50))
        ).into(binding.slide1)
        Glide.with(requireContext()).load(R.drawable.slide2).apply(
            RequestOptions().transform(RoundedCorners(50))
        ).into(binding.slide2)
        Glide.with(requireContext()).load(R.drawable.slide3).apply(
            RequestOptions().transform(RoundedCorners(50))
        ).into(binding.slide3)
        eventManager()


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
            categoryViewModel.allCategoty.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        categoryAdapter.differ.submitList(it.data)
                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            productViewModel.topNewProduct.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        topNewProductAdapter.differ.submitList(it.data)

                    }

                    is Resource.Error -> {

                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            productViewModel.topBoughtProduct.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        topBoughtProductAdapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {

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

    private fun initAdapter() {
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryAdapter.setOnItemClickListener(object : CategoryAdapter.OnItemClickListener {
            override fun onItemClick(category: Category) {
                val b = Bundle()
                b.putSerializable("category", category)
                findNavController().navigate(R.id.action_homeFragment_to_categoryFragment, b)
            }
        })

        binding.rvNew.apply {
            adapter = topNewProductAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            topNewProductAdapter.setOnItemClickListener(object :
                ProductAdpater.OnItemClickListener {
                override fun onItemClick(product: Product) {
                    val b = Bundle()
                    b.putInt("productId", product.id)
                    findNavController().navigate(R.id.action_homeFragment_to_productFragment, b)
                }
            })
        }
        binding.rvMostBought.apply {
            adapter = topBoughtProductAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            topBoughtProductAdapter.setOnItemClickListener(object :
                ProductAdpater.OnItemClickListener {
                override fun onItemClick(product: Product) {
                    val b = Bundle()
                    b.putInt("productId", product.id)
                    requireView().findNavController().navigate(R.id.action_homeFragment_to_productFragment, b)
                }
            })
        }


        binding.rvProductSearch.adapter = searchProductAdapter
        binding.rvProductSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchProductAdapter.setOnItemClickListener(object : ProductSearchAdpater.OnItemClickListener{
            override fun onItemClick(productSearch: Product) {
                val b = Bundle()
                b.putInt("productId", productSearch.id)
                requireView().findNavController().navigate(R.id.action_homeFragment_to_productFragment, b)
            }
        })
    }


}