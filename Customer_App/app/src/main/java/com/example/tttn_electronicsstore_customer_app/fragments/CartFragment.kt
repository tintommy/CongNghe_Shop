package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.activity.MainActivity
import com.example.tttn_electronicsstore_customer_app.adapters.CartProductAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentCartBinding
import com.example.tttn_electronicsstore_customer_app.helper.showBottomNavigation
import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val cartProductAdapter = CartProductAdapter()
    private val cartViewModel by viewModels<CartViewModel>()
    private var cartList: MutableList<Cart> = mutableListOf()
    private var total = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBottomNavigation()
        cartViewModel.getCartUser()
        initAdapter()

        lifecycleScope.launch {
            cartViewModel.getCart.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        if (it.data == null) {
                            emptyCart()
                        } else {
                            cartProductAdapter.differ.submitList(it.data)
                            cartList.clear()
                            cartList.addAll(it.data)
                            noEmptyCart()
                            totalCalculate(it.data)
                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "xảy ra lỗi khi load giỏ hàng",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                    else -> {}
                }
            }
        }
        lifecycleScope.launch {
            cartViewModel.deleteCart.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        (activity as MainActivity).updateBadge()

                    }

                    else -> {}
                }
            }
        }


        lifecycleScope.launch {
            cartViewModel.changeStatus.collectLatest {
                when(it){
                    is Resource.Success -> {
                      cartViewModel.getCartUser()

                    }

                    else -> {}
                }
            }
        }


//        lifecycleScope.launch {
//            cartViewModel.checkCartQuantity.collectLatest {
//
//                when (it) {
//                    is Resource.Loading -> {
//                        binding.btnBuy.startAnimation()
//                    }
//
//                    is Resource.Success -> {
//                        if (it.data?.isEmpty() == true) {
//
//                        } else {
//                            openDialog(it.data)
//                            binding.btnBuy.revertAnimation()
//                        }
//
//                    }
//
//                    is Resource.Loading -> {
//                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
//                        binding.btnBuy.revertAnimation()
//                    }
//
//                    else -> {
//                    }
//                }
//
//            }
//        }

        binding.btnBuy.setOnClickListener {
            if (total == 0) {
                Toast.makeText(requireContext(), "Hãy chọn sản phẩm", Toast.LENGTH_SHORT).show()
            } else {

                for( c in cartList){
                    if(c.productStatus==false && c.status==true){

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("Có sản phẩm ngừng kinh doanh nên không thể đặt")
                        builder.setTitle("Thông báo")
                        builder.setCancelable(false)
                        builder.setPositiveButton("OK") { dialog, which ->
                            dialog.cancel()
                        }

                        val alertDialog = builder.create()
                        alertDialog.show()
                        return@setOnClickListener
                    }
                }

                findNavController().navigate(R.id.action_cartFragment_to_createOrderFragment)

            }
        }
    }

    private fun openDialog(data: List<Product>?) {
        var text = ""
        if (data != null) {
            for (p in data) {
                text += p.name + "\n"
            }
        }
        text += "không đủ số lượng trong kho !! \n"
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(text)
        builder.setTitle("Thông báo")
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun initAdapter() {
        binding.rvCart.adapter = cartProductAdapter
        binding.rvCart.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        cartProductAdapter.setOnItemClickListener(object : CartProductAdapter.OnItemClickListener {
            override fun onItemClick(cart: Cart) {
                val b = Bundle()
                b.putInt("productId", cart.productId)
                findNavController().navigate(R.id.action_cartFragment_to_productFragment, b)
            }

            override fun onCbClick(cart: Cart, isChecked: Boolean) {
                cartViewModel.changeStatus(cart.id, isChecked)
                if (isChecked)
                    total += cart.productPrice * cart.quantity
                else
                    total -= cart.productPrice * cart.quantity
                binding.tvTotal.text = Convert.formatNumberWithDotSeparator(total) + "đ"

            }

            override fun onBtnDeleteClick(cart: Cart) {
                openDeleteDialog(cart)

            }

            override fun onBtnMinusClick(cart: Cart) {
                if (cart.status) {
                    total -= cart.productPrice
                    binding.tvTotal.text = Convert.formatNumberWithDotSeparator(total) + "đ"
                    cartViewModel.addCart(cart.productId, -1)
                }
            }

            override fun onBtnAddClick(cart: Cart) {
                if (cart.status) {
                    total += cart.productPrice
                    binding.tvTotal.text = Convert.formatNumberWithDotSeparator(total) + "đ"
                    cartViewModel.addCart(cart.productId, 1)
                }
            }
        })
    }

    private fun openDeleteDialog(cart: Cart) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Xoá ${cart.productName} khỏi giỏ hàng  ?")
        builder.setTitle("Xác nhận !")
        builder.setCancelable(false)
        builder.setPositiveButton("Xoá") { dialog, which ->
            cartViewModel.deleteCart(cart.id)
            cartProductAdapter.removeItem(cart)
            if (cart.status) {
                total -= cart.productPrice * cart.quantity
                binding.tvTotal.text = Convert.formatNumberWithDotSeparator(total) + "đ"

            }
            if (cartProductAdapter.differ.currentList.size == 1)
                emptyCart()


        }

        builder.setNegativeButton("Huỷ") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    fun emptyCart() {
        binding.apply {

            ivEmptyCart.visibility = View.VISIBLE
            rvCart.visibility = View.GONE
            layoutBuy.visibility = View.GONE
        }
    }

    fun noEmptyCart() {
        binding.apply {

            ivEmptyCart.visibility = View.GONE
            rvCart.visibility = View.VISIBLE
            layoutBuy.visibility = View.VISIBLE
        }
    }

    fun totalCalculate(cartList: List<Cart>) {
        total = 0
        for (cart in cartList) {
            if (cart.status && cart.productStatus)
                total += cart.productPrice * cart.quantity
        }
        binding.tvTotal.text = Convert.formatNumberWithDotSeparator(total) + "đ"
    }
}