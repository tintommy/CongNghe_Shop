package com.example.tttn_electronicsstore_customer_app.fragments.orderFragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.adapters.OrderDetailAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.SuccessOrderDetailAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentOrderDetailBinding
import com.example.tttn_electronicsstore_customer_app.helper.hideBottomNavigation
import com.example.tttn_electronicsstore_customer_app.viewModels.OrderViewModel
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Order
import com.example.tttn_electronicsstore_customer_app.models.OrderDetail
import com.example.tttn_electronicsstore_customer_app.models.Review
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private lateinit var order: Order
    private val orderViewModel by viewModels<OrderViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val orderDetailAdapter: OrderDetailAdapter = OrderDetailAdapter()

    private val successOrderDetailAdapter = SuccessOrderDetailAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        val b = arguments
        if (b != null) {
            order = b.getSerializable("order") as Order
            setUpInfo()
            orderViewModel.getDetailByOrderId(order.id)
            eventManager()
            // initAdapter()
        }

    }

    private fun initAdapter() {
        if (order.status != 5) {
            binding.rvListOrderDetail.adapter = orderDetailAdapter
            binding.rvListOrderDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        } else {
            binding.rvListOrderDetail.adapter = successOrderDetailAdapter
            binding.rvListOrderDetail.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            successOrderDetailAdapter.setOnItemClickListener(object :
                SuccessOrderDetailAdapter.OnItemClickListener {
                override fun onBtnClick(orderDetail: OrderDetail, star: Int, content: String) {
                    userViewModel.addCmt(orderDetail.productId, star, content, orderDetail.id)
                }
            })
        }
    }

    private fun eventManager() {
        lifecycleScope.launch {
            orderViewModel.detailOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        orderDetailAdapter.differ.submitList(it.data)
                        successOrderDetailAdapter.differ.submitList(it.data)
                        setUpInfo()
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }

        }

        lifecycleScope.launch {
            orderViewModel.updateOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        order = it.data!!
                        setUpInfo()

                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }

        }

//        lifecycleScope.launch {
//            userViewModel.addReview.collectLatest {
//                when (it) {
//                    is Resource.Loading -> {}
//                    is Resource.Success -> {
//                        orderViewModel.getDetailByOrderId(order.id)
//
//                    }
//
//                    is Resource.Error -> {}
//                    else -> {}
//                }
//            }
//
//        }
    }

    private fun setUpInfo() {
        initAdapter()

        binding.apply {

            when (order.status) {
                0 -> {
                    tvOrderStatus.text = "Đã huỷ"
                }

                1 -> {
                    tvOrderStatus.text = "Đang chờ"
                }

                2 -> {
                    tvOrderStatus.text = "Đang chuẩn bị"
                }

                3 -> {
                    tvOrderStatus.text = "Đang giao"
                }

                4 -> {
                    tvOrderStatus.text = "Đã giao hàng"
                }

                5 -> {
                    tvOrderStatus.text = "Thành công"
                }

                6 -> {
                    tvOrderStatus.text = "Giao hàng thất bại"

                }

                else -> {}
            }



            tvReceiverName.text = order.receiverName
            tvReceiverPhone.text = order.receiverPhone
            tvReceiverAddress.text = order.receiverAddress
            tvOrderPrice.text = Convert.formatNumberWithDotSeparator(order.total) + "đ"
            tvOrderShip.text = Convert.formatNumberWithDotSeparator(order.ship) + "đ"
            tvOrderTotal.text = Convert.formatNumberWithDotSeparator(order.total + order.ship) + "đ"





            if (order.status == 1 || order.status == 2 || order.status == 4) {
                layoutBtn.visibility = View.VISIBLE
                if (order.status == 4) {
                    btnCancelOrder.visibility = View.GONE
                } else {
                    btnAcceptOrder.visibility = View.GONE
                }
            } else {
                layoutBtn.visibility = View.GONE
            }




            if (order.status == 4 || order.status == 5) {
                binding.layoutShippedImage.visibility = View.VISIBLE
                Glide.with(requireContext()).load(order.shippedImage).into(binding.ivShippedOrder)
            } else {
                binding.layoutShippedImage.visibility = View.GONE
            }


            if (order.status == 6) {
                layoutReason.visibility = View.VISIBLE
                binding.tvReason.text = order.cancelReason
            } else {
                layoutReason.visibility = View.GONE
            }


            if (order.onlinePay) {
                binding.tvOrderPay.text = "Đã thanh toán online"
            }

            binding.btnCancelOrder.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Chắc chắn huỷ đơn hàng  ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton("Huỷ") { dialog, which ->
                    orderViewModel.changeOrderStatus(order.id, 0)
                }

                builder.setNegativeButton("Không") { dialog, which ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()

            }
            binding.btnAcceptOrder.setOnClickListener {

                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Chắc chắn chuyển trạng thái đơn hàng  ?")
                builder.setTitle("Xác nhận !")
                builder.setCancelable(false)
                builder.setPositiveButton("Có") { dialog, which ->
                    orderViewModel.changeOrderStatus(order.id, order.status + 1)
                }

                builder.setNegativeButton("Không") { dialog, which ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()


            }
        }
    }
}