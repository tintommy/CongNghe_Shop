package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.BrandSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.CategorySpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.ProductSearchAdpater
import com.example.tttn_electronicsstore_customer_app.adapters.SearchDetailProductAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.DialogFilterProductLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.DialogSearchProductLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentSearchBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.helper.showBottomNavigation
import com.example.tttn_electronicsstore_customer_app.models.Brand
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.models.Detail
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.BrandViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.CategoryViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.ProductViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var brandSpinnerAdapter: BrandSpinnerAdapter
    private val brandViewModel by viewModels<BrandViewModel>()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val productViewModel by viewModels<ProductViewModel>()
    private var selectedBrandId: Int = 0;
    private var selectedCategoryId: Int = 1;

    private lateinit var searchDetailProductAdapter: SearchDetailProductAdapter

    private var currentFrom = ""
    private var currentTo = ""

    private var moneyFrom = 0
    private var moneyTo = 999999999
    private val brandList: MutableList<Brand> = mutableListOf()
    private var detailsReset: MutableList<Detail> = mutableListOf()
    private val productSearchAdpater = ProductSearchAdpater()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()

        val brandTemp = Brand(0, "", "Tất cả", true)
        brandList.add(brandTemp)
        brandViewModel.getAllBrand()
        categoryViewModel.getAll()
        eventManager()

        binding.etFromPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != currentFrom) {
                    binding.etFromPrice.removeTextChangedListener(this)

                    val formatted = formatNumber(p0.toString())
                    currentFrom = formatted

                    binding.etFromPrice.setText(formatted)
                    binding.etFromPrice.setSelection(formatted.length)

                    binding.etFromPrice.addTextChangedListener(this)
                }

            }
        })

        binding.etToPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != currentTo) {
                    binding.etToPrice.removeTextChangedListener(this)

                    val formatted = formatNumber(p0.toString())
                    currentTo = formatted

                    binding.etToPrice.setText(formatted)
                    binding.etToPrice.setSelection(formatted.length)

                    binding.etToPrice.addTextChangedListener(this)
                }


            }
        })


        binding.btnSubmit.setOnClickListener {
            var fromPrice = 0
            var toPrice = 999999999
            if (!binding.etFromPrice.toString().equals(""))
                fromPrice = formatPriceToInt(binding.etFromPrice.text.toString())
            if (!binding.etToPrice.toString().equals(""))
                toPrice = formatPriceToInt(binding.etToPrice.text.toString())
            productViewModel.filterProductPriority(
                Convert.convertMapToJson(searchDetailProductAdapter.getDetailValueMap()),
                Convert.convertMapToJson(searchDetailProductAdapter.getPriorityMap()),
                selectedCategoryId,
                selectedBrandId,
                fromPrice.toLong(), toPrice.toLong()
            )
            Log.e("Search", searchDetailProductAdapter.getPriorityMap().toString())

        }

        binding.btnOption.setOnClickListener {
            if (binding.rvDetailSearch.visibility == View.VISIBLE) {
                binding.rvDetailSearch.visibility = View.GONE
                binding.btnOption.text="Mở lọc thông số"
            }
            else if (binding.rvDetailSearch.visibility == View.GONE) {
                binding.rvDetailSearch.visibility = View.VISIBLE
                binding.btnOption.text="Ẩn lọc thông số"
            }
        }



        binding.rangePrice.addOnChangeListener { slider, value, fromUser ->
            // Get the min and max values from the slider
            val values = slider.values
            val minValue = values[0].toInt()
            val maxValue = values[1].toInt()

            // Use the min and max values as needed

            binding.etFromPrice.setText((minValue * 100000).toString())
            binding.etToPrice.setText((maxValue * 100000).toString())

        }
    }


    private fun eventManager() {
        lifecycleScope.launch {
            brandViewModel.getAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {


                        brandList.addAll(it.data!!)
                        initBrandSPAdapter(brandList)
                    }

                    is Resource.Error -> {


                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            categoryViewModel.allCategoty.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        initCategorySPAdapter(it.data!!)
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            productViewModel.getFilterProductPriority.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSubmit.startAnimation()
                    }

                    is Resource.Success -> {

                        productSearchAdpater.differ.submitList(it.data)
                        initListProductSearch()
                        binding.btnSubmit.revertAnimation()

                    }

                    is Resource.Error -> {
                        binding.btnSubmit.revertAnimation()
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Không có sản phẩm thích hợp")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.dismiss()
                        }

                        val alertDialog = builder.create()
                        alertDialog.show()

                        productSearchAdpater.differ.submitList(mutableListOf())


                    }

                    else -> {}
                }
            }
        }

    }

    private fun initListProductSearch() {
        binding.rvProduct.adapter = productSearchAdpater
        binding.rvProduct.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        productSearchAdpater.setOnItemClickListener(object :
            ProductSearchAdpater.OnItemClickListener {
            override fun onItemClick(productSearch: Product) {
                val b = Bundle()
                b.putInt("productId", productSearch.id)
                requireView().findNavController()
                    .navigate(R.id.action_searchFragment_to_productFragment, b)

            }

        })

    }

    private fun initBrandSPAdapter(brandList: List<Brand>) {
        brandSpinnerAdapter = BrandSpinnerAdapter(requireContext(), brandList)
        binding.spBrand.adapter = brandSpinnerAdapter

        binding.spBrand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedBrandId = p3.toInt()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun initCategorySPAdapter(categoryList: List<Category>) {
        val adapter = CategorySpinnerAdapter(
            requireContext(),
            categoryList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCategoryId = p3.toInt()
                loadRvDetail(categoryList[p2].details)
                detailsReset.clear()
                detailsReset.addAll(categoryList[p2].details)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun loadRvDetail(details: List<Detail>) {
        searchDetailProductAdapter = SearchDetailProductAdapter(requireContext())
        searchDetailProductAdapter.clearDetailValueMap()
        searchDetailProductAdapter.differ.submitList(mutableListOf())
        searchDetailProductAdapter.differ.submitList(details)
        searchDetailProductAdapter.notifyDataSetChanged()
        binding.rvDetailSearch.adapter = searchDetailProductAdapter
        binding.rvDetailSearch.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun formatNumber(input: String): String {
        val cleanString = input.replace(".", "")
        val formattedString = cleanString.toLongOrNull()?.let {
            String.format("%,d", it).replace(",", ".")
        }
        return formattedString ?: input
    }

    fun formatPriceToInt(input: String): Int {
        val cleanString = input.replace(".", "")
        return cleanString.toInt()
    }


}