package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.BrandSpinnerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.CategorySpinnerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.DetailAddProductAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.DetailEditProductAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OtherImagesAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OtherImagesEditAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentEditProductBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.Image
import com.example.tttn_electronicsstore_manager_admin_app.models.PriceHistory
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.models.ProductDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Review
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.BrandViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.CategoryViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@AndroidEntryPoint
class EditProductFragment : Fragment() {
    private lateinit var binding: FragmentEditProductBinding
    private lateinit var product: Product

    private lateinit var brandSpinnerAdapter: BrandSpinnerAdapter
    private val detailAddProductAdapter = DetailAddProductAdapter()
    private val detailEditProductAdapter = DetailEditProductAdapter()
    private val otherImagesAdapter = OtherImagesAdapter()
    private val otherImagesEditAdapter = OtherImagesEditAdapter()
    private var selectedBrandId: Int = 0;
    private var selectedCategoryId: Int = 0;
    private lateinit var selectedAvatar: Uri
    private var selectedOtherImagesList: MutableList<Uri> = ArrayList()
    private val brandViewModel by viewModels<BrandViewModel>()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val productViewModel by viewModels<ProductViewModel>()
    private var current = ""
    private var imageList: MutableList<Image> = mutableListOf()

    //    private lateinit var categoriesList: List<Category>
//    private lateinit var brandsList: List<Brand>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventManager()
      //  setAvatarProduct()
        val b = arguments
        product = b!!.getSerializable("product") as Product
        setUp(product)

    }

    private fun setUp(product: Product) {
        brandViewModel.getAllBrand()
        categoryViewModel.getAll()
        binding.apply {
            etProductName.setText(product.name)
            etProductPrice.setText(formatNumberWithDotSeparator(product.price))
            etProductQuantity.setText(product.quantity.toString())
            etProductDesc.setText(product.description)
            loadRvOtherImage(product.imageList)
            switchStatus.isChecked = product.status
        }



        if (product.status) {
            binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.GREEN)
            binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.GREEN)
        } else {
            binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.RED)
            binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.RED)
        }

        binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.switchStatus.thumbTintList =
                        ColorStateList.valueOf(Color.GREEN)
                    binding.switchStatus.trackTintList =
                        ColorStateList.valueOf(Color.GREEN)
                } else {
                    binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.RED)
                    binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.RED)
                }

            }
        }






        binding.etProductPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != current) {
                    binding.etProductPrice.removeTextChangedListener(this)

                    val formatted = formatNumber(p0.toString())
                    current = formatted

                    binding.etProductPrice.setText(formatted)
                    binding.etProductPrice.setSelection(formatted.length)

                    binding.etProductPrice.addTextChangedListener(this)
                }

            }
        })
        for (image in product.imageList) {
            if (image.avatar) {
            Glide.with(this).load(image.link).into(binding.ivProductAvatar)

            }
        }



        binding.btnSave.setOnClickListener {
            val productName = binding.etProductName.text.toString().trim()
            val productPrice = formatPriceToInt(binding.etProductPrice.text.toString().trim())
            val productQuantity = binding.etProductQuantity.text.toString().trim().toInt()
            val productDesc = binding.etProductDesc.text.toString().trim()
            val productStatus = binding.switchStatus.isChecked
            val imageTemp = mutableListOf<Image>()
            if (selectedCategoryId == product.categoryId) {
                val productTemp = Product(
                    0,
                    selectedBrandId,
                    "",
                    selectedCategoryId,
                    "",
                    productDesc,
                    product.id,
                    imageTemp,
                    mutableListOf<PriceHistory>(),
                    productName,
                    productPrice!!,
                    mutableListOf<ProductDetail>(),
                    productQuantity,
                    mutableListOf<Review>(),
                    product.reviewValue,
                    productStatus

                )

                productViewModel.updateProduct(
                    productTemp,
                    detailEditProductAdapter.getDetailValueMap(),
                )
            } else {
                val productTemp = Product(
                    0,
                    selectedBrandId,
                    "",
                    selectedCategoryId,
                    "",
                    productDesc,
                    product.id,
                    imageTemp, mutableListOf<PriceHistory>(),
                    productName,
                    productPrice!!,
                    mutableListOf<ProductDetail>(),
                    productQuantity,
                    mutableListOf<Review>(),
                    0.0,
                    true

                )
                productViewModel.updateProduct(
                    productTemp,
                    detailAddProductAdapter.getDetailValueMap(),
                )
            }
        }
    }

    private fun editOtherImage() {
        val selectOtherImagesResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data

                    //Mutiple images selected
                    if (intent?.clipData != null) {
                        val count = intent.clipData?.itemCount ?: 0
                        (0 until count).forEach {
                            val imageUri = intent.clipData?.getItemAt(it)?.uri
                            imageUri?.let {
                                selectedOtherImagesList.add(it)
                            }
                        }
                    } else {
                        val imageUri = intent?.data
                        selectedOtherImagesList.add(imageUri!!)
                    }

                    otherImagesAdapter.differ.submitList(selectedOtherImagesList)
                    otherImagesAdapter.notifyDataSetChanged()

                }

            }

        //chọn ảnh trong máy
        binding.btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectOtherImagesResult.launch(intent)
        }
    }

    private fun editAvatar() {
        val selectAvatarResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    selectedAvatar = intent?.data!!

                    Glide.with(this).load(selectedAvatar).into(binding.ivProductAvatar)


                }

            }

        //chọn ảnh trong máy
        binding.ivProductAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectAvatarResult.launch(intent)
        }
    }


    private fun eventManager() {
        lifecycleScope.launch {
            brandViewModel.getAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        initBrandSPAdapter(it.data!!)

                    }

                    is Resource.Error -> {}
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
            productViewModel.updateProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        Toast.makeText(
                            requireContext(),
                            "Sửa sản phẩm thành công",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnSave.revertAnimation()
                    }

                    is Resource.Error -> {
                        binding.btnSave.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun initBrandSPAdapter(brandList: List<Brand>) {
        brandSpinnerAdapter = BrandSpinnerAdapter(requireContext(), brandList)
        binding.spBrand.adapter = brandSpinnerAdapter

        val defaultPosition =
            brandList.indexOfFirst { it.id == product.brandId } // Change 2 to the desired default category ID
        if (defaultPosition != -1) {
            binding.spBrand.setSelection(defaultPosition)
        }
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
        val defaultPosition =
            categoryList.indexOfFirst { it.id == product.categoryId }
        if (defaultPosition != -1) {
            binding.spCategory.setSelection(defaultPosition)
        }
        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                selectedCategoryId = p3.toInt()
                if (p2 == defaultPosition) {
                    loadRvDetailDefault(product.productDetailList)

                } else {

                    loadRvDetail(categoryList[p2].details)

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun loadRvDetailDefault(details: List<ProductDetail>) {

        detailEditProductAdapter.clearDetailValueMap()
        detailEditProductAdapter.differ.submitList(details)
        detailEditProductAdapter.notifyDataSetChanged()
        binding.rvListDetail.adapter = detailEditProductAdapter
        binding.rvListDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }


    private fun loadRvDetail(details: List<Detail>) {

        detailAddProductAdapter.clearDetailValueMap()
        detailAddProductAdapter.differ.submitList(details)
        detailAddProductAdapter.notifyDataSetChanged()
        binding.rvListDetail.adapter = detailAddProductAdapter
        binding.rvListDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun loadRvOtherImage(images: List<Image>) {
        otherImagesEditAdapter.differ.submitList(images)
        binding.rvProductImages.adapter = otherImagesEditAdapter
        binding.rvProductImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    fun formatNumberWithDotSeparator(number: Int): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = '.'
        }
        val decimalFormat = DecimalFormat("#,###", symbols)
        return decimalFormat.format(number)
    }

    private fun formatNumber(input: String): String {
        val cleanString = input.replace(".", "")
        val formattedString = cleanString.toLongOrNull()?.let {
            String.format("%,d", it).replace(",", ".")
        }
        return formattedString ?: input
    }

    fun formatPriceToInt(input: String): Int? {
        val cleanString = input.replace(".", "")
        return cleanString.toIntOrNull()
    }

    private fun setAvatarProduct() {
        val selectAvatarResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    selectedAvatar = intent?.data!!
                //    Glide.with(requireContext()).load(selectedAvatar).into(binding.ivProductAvatar)
                    binding.btnUpdateAvatar.visibility = View.VISIBLE

                }

            }

        //chọn ảnh trong máy
        binding.ivProductAvatar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectAvatarResult.launch(intent)
        }

    }
}