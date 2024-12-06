package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.BrandSpinnerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.CategorySpinnerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.DetailAddProductAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OtherImagesAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentAddProductBinding
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

@AndroidEntryPoint
class AddProductFragment : Fragment() {

    private lateinit var binding: FragmentAddProductBinding
    private lateinit var brandSpinnerAdapter: BrandSpinnerAdapter
    private val detailAddProductAdapter = DetailAddProductAdapter()
    private val otherImagesAdapter = OtherImagesAdapter()
    private var selectedBrandId: Int = 0;
    private var selectedCategoryId: Int = 0;

    // private lateinit var categoryList: List<Category>
    private var current = ""
    private lateinit var selectedAvatar: Uri
    private var selectedOtherImagesList: MutableList<Uri> = ArrayList()
    private val brandViewModel by viewModels<BrandViewModel>()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val productViewModel by viewModels<ProductViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        brandViewModel.getAllBrand()
        categoryViewModel.getAll()
        eventManager()
        initOtherImagesAdapter()
        setAvatarProduct()
        setOtherImagesProduct()
        selectedAvatar = "".toUri()

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
        binding.btnSave.setOnClickListener {


            if (!isFullyForm(
                    binding.etProductName.text.toString(),
                    binding.etProductPrice.text.toString(),
                    binding.etProductQuantity.text.toString(),
                    binding.etProductDesc.text.toString()
                ) || selectedAvatar.toString() == "" || selectedOtherImagesList.isEmpty()
            ) {
                Toast.makeText(
                    requireContext(),
                    "Hãy nhập đủ các thông tin và hình ảnh ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val productName = binding.etProductName.text.toString().trim()
                val productPrice = formatPriceToInt(binding.etProductPrice.text.toString().trim())
                val productQuantity = binding.etProductQuantity.text.toString().trim().toInt()
                val productDesc = binding.etProductDesc.text.toString().trim()
                val imageTemp = mutableListOf<Image>()

                val product = Product(
                    0,
                    selectedBrandId,
                    "",
                    selectedCategoryId,
                    "",
                    productDesc,
                    0,
                    imageTemp,
                    mutableListOf<PriceHistory>(),
                    productName,
                    productPrice!!,
                    mutableListOf<ProductDetail>(),
                    productQuantity,
                    mutableListOf<Review>(),
                    0.0,
                    true

                )

                productViewModel.addProduct(
                    product,
                    selectedAvatar,
                    selectedOtherImagesList,
                    detailAddProductAdapter.getDetailValueMap(),
                    requireContext()
                )
            }
        }
    }

    private fun initOtherImagesAdapter() {
        binding.rvProductImages.adapter = otherImagesAdapter
        binding.rvProductImages.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        otherImagesAdapter.setOnItemClickListener(object : OtherImagesAdapter.OnItemClickListener {
            override fun onDeleteClick(position: Int) {
                selectedOtherImagesList.removeAt(position)
                otherImagesAdapter.differ.submitList(selectedOtherImagesList)
                otherImagesAdapter.notifyDataSetChanged()
            }
        })
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
            productViewModel.addProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        val toast = Toast.makeText(
                            requireContext(),
                            "Thêm sản phẩm thành công",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        (activity as AdminActivity).supportFragmentManager.popBackStack()

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
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun loadRvDetail(details: List<Detail>) {

        detailAddProductAdapter.clearDetailValueMap()
        detailAddProductAdapter.differ.submitList(details)
        detailAddProductAdapter.notifyDataSetChanged()
        binding.rvListDetail.adapter = detailAddProductAdapter
        binding.rvListDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setOtherImagesProduct() {
        val selectAvatarResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    selectedAvatar = intent?.data!!
                    Glide.with(requireContext()).load(selectedAvatar).into(binding.ivProductAvatar)


                }

            }

        //chọn ảnh trong máy
        binding.ivProductAvatar.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectAvatarResult.launch(intent)
        }

    }

    private fun setAvatarProduct() {
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
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectOtherImagesResult.launch(intent)
        }
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
}

private fun isFullyForm(name: String, price: String, quantity: String, desc: String): Boolean {
    if (name.equals("") || price.equals("") || quantity.equals("") || desc.equals("")) {
        return false
    }
    return true
}
