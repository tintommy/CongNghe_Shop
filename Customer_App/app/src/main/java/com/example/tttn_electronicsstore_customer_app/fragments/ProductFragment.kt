package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.ImageAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.ProductDetailAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.ProductSearchAdpater
import com.example.tttn_electronicsstore_customer_app.adapters.ReviewAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.AddToCartLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.DialogSearchProductLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentProductBinding
import com.example.tttn_electronicsstore_customer_app.helper.hideBottomNavigation
import com.example.tttn_electronicsstore_customer_app.viewModels.ProductViewModel
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale


@AndroidEntryPoint
class ProductFragment : Fragment() {

    private lateinit var binding: FragmentProductBinding
    private lateinit var product: Product
    private lateinit var imageAdapter: ImageAdapter
    private val productViewModel by viewModels<ProductViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private val productDetailAdapter = ProductDetailAdapter()
    private val productSearchAdapter = ProductSearchAdpater()
    private val reviewAdapter = ReviewAdapter()
    private var pageReview = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()

        val b = arguments
        if (b != null) {
            val productId = b.getInt("productId")
            productViewModel.getProduct(productId)

        }
        eventManager()


    }

    private fun eventManager() {
        lifecycleScope.launch {
            productViewModel.getProduct.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        product = it.data!!
                        setUp()
                    }

                    is Resource.Error -> {

                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            productViewModel.getPageReview.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        if (it.data!!.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvReview.visibility=View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvReview.visibility=View.VISIBLE
                            reviewAdapter.differ.submitList(it.data)
                            reviewAdapter.notifyDataSetChanged()
                            binding.btnPrevious.visibility = View.VISIBLE
                            binding.btnNext.visibility = View.VISIBLE


                            if (pageReview == 0) {
                                binding.btnPrevious.visibility = View.GONE
                            }

                            if (it.data!!.size < 5) {
                                binding.btnNext.visibility = View.GONE
                            }
                        }
                    }

                    is Resource.Error -> {

                    }

                    else -> {}
                }
            }
        }

    }

    fun setUp() {

        productViewModel.getPageReview(product.id, pageReview, 5)

        imageAdapter = ImageAdapter(requireContext(), product.imageList)
        binding.viewPager.adapter = imageAdapter
        binding.indicator.setViewPager(binding.viewPager)
        imageAdapter.registerDataSetObserver(binding.indicator.dataSetObserver)

        binding.apply {
            tvProductName.text = product.name
            tvProductPrice.text = Convert.formatNumberWithDotSeparator(product.price) + "đ"
            tvStart.text = String.format(Locale.US, "%.1f", product.reviewValue)
            tvBought.text = product.bought.toString() + " đã mua"
            tvBrand.text = product.brandName

            tvProductDesc.text = product.description

            productDetailAdapter.differ.submitList(product.productDetailList)
            rvProductDetail.adapter = productDetailAdapter
            rvProductDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


            if(product.status==false){
                tvProductPrice.text = "Sản phẩm ngừng kinh doanh"
                btnAddToCart.visibility=View.GONE
            }


            binding.btnDesc.setOnClickListener {
                if (tvProductDesc.visibility == View.VISIBLE) {
                    tvProductDesc.visibility = View.GONE
                } else {
                    tvProductDesc.visibility = View.VISIBLE
                }
            }
            binding.btndetail.setOnClickListener {
                if (rvProductDetail.visibility == View.VISIBLE) {
                    rvProductDetail.visibility = View.GONE
                } else {
                    rvProductDetail.visibility = View.VISIBLE
                }
            }

            binding.btnReview.setOnClickListener {
                if (layoutReview.visibility == View.VISIBLE) {
                    layoutReview.visibility = View.GONE
                } else {
                    layoutReview.visibility = View.VISIBLE
                }
            }


            binding.btnBack.setOnClickListener {
                findNavController().navigateUp()
            }

            binding.btnAddToCart.setOnClickListener {
                openBottomDialog()
            }

            binding.btnCompare.setOnClickListener {
                openSearchDialog()
            }
        }


        binding.rvReview.adapter = reviewAdapter
        binding.rvReview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        binding.btnNext.setOnClickListener {
            pageReview += 1
            productViewModel.getPageReview(product.id, pageReview,5)
        }
        binding.btnPrevious.setOnClickListener {
            pageReview -= 1
            productViewModel.getPageReview(product.id, pageReview,5)
        }

    }


    private fun openBottomDialog() {
        val dialog: Dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = AddToCartLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        var number = 1

        dialogBinding.apply {

            tvProductName.text = product.name
            tvProductQuantity.text = "Còn lại ${product.quantity}"

            Glide.with(requireContext()).load(product.imageList[0].link).into(ivPhoto)

            btnMinus.isEnabled = true

            btnMinus.setOnClickListener {
                btnAdd.isEnabled = true
                if (etNumber.text.toString().toInt() <= 1) {
                    btnMinus.isEnabled = false

                } else {
                    number = etNumber.text.toString().toInt()
                    number -= 1
                    etNumber.setText(number.toString())
                }
            }

            btnPlus.setOnClickListener {
                btnMinus.isEnabled = true
                if (etNumber.text.toString().toInt() >= product.quantity) {
                    btnPlus.isEnabled = false
                } else {
                    number = etNumber.text.toString().toInt()
                    number += 1
                    etNumber.setText(number.toString())
                }
            }

            btnAdd.setOnClickListener {
                if (etNumber.text.toString().toInt() < 1 || etNumber.text.toString()
                        .toInt() > product.quantity
                ) {
                    Toast.makeText(requireContext(), "Số lượng không hợp lệ", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    cartViewModel.addCart(product.id, etNumber.text.toString().toInt())
                }
            }
            lifecycleScope.launch {
                cartViewModel.addCart.collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            dialogBinding.btnAdd.startAnimation()
                        }

                        is Resource.Success -> {
                            dialogBinding.btnAdd.revertAnimation()
                            dialog.dismiss()
                            Toast.makeText(
                                requireContext(),
                                "Đã thêm hàng vào giỏ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is Resource.Error -> {
                            dialogBinding.btnAdd.revertAnimation()
                            Toast.makeText(
                                requireContext(),
                                "Đã xảy ra lỗi khi thêm vào giỏ",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        else -> {}
                    }
                }
            }

        }




        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }


    private fun openSearchDialog() {
        val dialogBinding: DialogSearchProductLayoutBinding =
            DialogSearchProductLayoutBinding.inflate(layoutInflater)
        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialogBinding.apply {
            rvProduct.adapter = productSearchAdapter
            rvProduct.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            productSearchAdapter.setOnItemClickListener(object :
                ProductSearchAdpater.OnItemClickListener {
                override fun onItemClick(productSearch: Product) {

                    if (product.categoryId != productSearch.categoryId)
                        dialogBinding.tvWrong.visibility = View.VISIBLE
                    else {
                        val b = Bundle()
                        b.putInt("productId1", product.id)
                        b.putInt("productId2", productSearch.id)
                        findNavController().navigate(
                            R.id.action_productFragment_to_compareFragment,
                            b
                        )
                        mDialog.dismiss()
                    }
                }
            })
        }
        initSearch(dialogBinding)
        mDialog.show()


    }

    private fun initSearch(dialogBinding: DialogSearchProductLayoutBinding) {

        lifecycleScope.launch {
            productViewModel.getSearchProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        dialogBinding.tvWrong.visibility = View.GONE
                        dialogBinding.rvProduct.visibility = View.VISIBLE
                        productSearchAdapter.differ.submitList(it.data)
                        productSearchAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        dialogBinding.rvProduct.visibility = View.GONE
                    }

                    else -> {}
                }
            }
        }


        var querySearch: String = ""
        val delayTime: Long = 700

        val handler = Handler()

        val search = Runnable {
            if (querySearch != "") {
                productViewModel.searchProduct(querySearch)
            }
        }
        dialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0?.toString().equals("") || p0 == null) {
                    productSearchAdapter.differ.submitList(ArrayList<Product>())
                    productSearchAdapter.notifyDataSetChanged()
                    dialogBinding.rvProduct.visibility = View.GONE
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


}