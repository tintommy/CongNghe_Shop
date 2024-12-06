package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.CompareProductDetailAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.AddToCartLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentCompareBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.CompareValue
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.CartViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompareFragment : Fragment() {
    private lateinit var binding: FragmentCompareBinding
    private val compareProductDetailAdapter = CompareProductDetailAdapter()
    private lateinit var product1: Product
    private lateinit var product2: Product
    private var productId1: Int = 0
    private var productId2: Int = 0
    private var num = 1
    private val productViewModel by viewModels<ProductViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompareBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        num = 1
        val b = arguments
        if (b != null) {
            productId1 = b.getInt("productId1", 0)
            productId2 = b.getInt("productId2", 0)
        }

        productViewModel.getProduct(productId1)
        lifecycleScope.launch {
            productViewModel.getProduct.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        if (num == 1) {
                            product1 = it.data!!
                            num = 2
                            productViewModel.getProduct(productId2)
                        }

                        if (num == 2) {
                            product2 = it.data!!
                            setUp()


                        }


                    }

                    else -> {}
                }
            }
        }


    }

    private fun setUp() {
        val detailList1 = product1.productDetailList.sortedBy { it.detailId }

        val detailList2 = product2.productDetailList.sortedBy { it.detailId }

        Log.e("compare", detailList1.toString())
        Log.e("compare", detailList2.toString())
        binding.rvCompare.adapter = compareProductDetailAdapter
        binding.rvCompare.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        Glide.with(requireContext()).load(product1.imageList.get(0).link).into(binding.ivProduct1)
        binding.tvName1.text = product1.name
        binding.tvPrice1.text = Convert.formatNumberWithDotSeparator(product1.price) + "đ"
        Glide.with(requireContext()).load(product2.imageList.get(0).link).into(binding.ivProduct2)
        binding.tvName2.text = product2.name
        binding.tvPrice2.text = Convert.formatNumberWithDotSeparator(product2.price) + "đ"

        val compareValueList: MutableList<CompareValue> = mutableListOf()
        for (i in 0 until detailList1.size) {
            val compareValue = CompareValue(
                detailList1.get(i).detailName,
                detailList1.get(i).value,
                detailList2.get(i).value
            )
            compareValueList.add(compareValue)
        }
        compareProductDetailAdapter.differ.submitList(compareValueList)




        binding.btnAddToCart1.setOnClickListener {
            openBottomDialog(product1)
        }
        binding.btnAddToCart2.setOnClickListener {
            openBottomDialog(product2)
        }
    }




    private fun openBottomDialog(product:Product) {
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


}