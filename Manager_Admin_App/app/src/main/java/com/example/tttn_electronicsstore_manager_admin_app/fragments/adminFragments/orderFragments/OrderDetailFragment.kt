package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderDetailAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.UserSpinnerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentOrderDetailBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.OrderViewModel
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
    private var selectedShipper = ""
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
        val b = arguments
        if (b != null) {
            order = b.getSerializable("order") as Order
            setUpInfo()
            orderViewModel.getDetailByOrderId(order.id)
            eventManager()
            initAdapter()
        }

    }

    private fun initAdapter() {
        binding.rvListOrderDetail.adapter = orderDetailAdapter
        binding.rvListOrderDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun eventManager() {
        lifecycleScope.launch {
            orderViewModel.detailOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        orderDetailAdapter.differ.submitList(it.data)

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
    }

    private fun setUpInfo() {
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
                    tvOrderStatus.text = "Đơn hàng thành công"
                }
                6 -> {
                    tvOrderStatus.text = "Giao hàng thất bại"

                }

                else -> {}
            }



            tvOrderId.text = order.id.toString()
            tvReceiverName.text = order.receiverName
            tvReceiverPhone.text = order.receiverPhone
            tvReceiverAddress.text = order.receiverAddress
            tvOrderPrice.text = Convert.formatNumberWithDotSeparator(order.total) + "đ"
            tvOrderShip.text = Convert.formatNumberWithDotSeparator(order.ship) + "đ"
            tvOrderTotal.text = Convert.formatNumberWithDotSeparator(order.total + order.ship) + "đ"





            if (order.status == 1 || order.status == 2) {
                layoutBtn.visibility = View.VISIBLE
                if (order.status == 1) {
                    btnAcceptOrder.text = "Duyệt đơn"
                } else if (order.status == 2) {
                    btnAcceptOrder.text = "Giao hàng"
                }
            } else {
                layoutBtn.visibility = View.GONE
            }

            if (order.status == 2) {
                layoutShipper.visibility = View.VISIBLE
                initSpinnerShipper()
                userViewModel.getUserByRole("shipper")


            } else {
                layoutShipper.visibility = View.GONE
            }

            if(order.status==6){
                layoutReason.visibility=View.VISIBLE
                tvReason.text=order.cancelReason
                btnReShip.visibility=View.VISIBLE
            }
            else{
                layoutReason.visibility=View.GONE
                tvReason.text=order.cancelReason
                btnReShip.visibility=View.GONE
            }

        }



        if (order.status == 4 || order.status == 5) {
            binding.layoutShippedImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(order.shippedImage).into(binding.ivShippedOrder)
        } else {
            binding.layoutShippedImage.visibility = View.GONE
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

                if (order.status == 2) {
                    orderViewModel.changeOrderStatusToShipping(order.id, selectedShipper)
                } else {
                    orderViewModel.changeOrderStatus(order.id, order.status + 1)
                }
            }

            builder.setNegativeButton("Không") { dialog, which ->
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()


        }

        binding.btnReShip.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Chắc chắn giao lại đơn hàng này ?")
            builder.setTitle("Xác nhận !")
            builder.setCancelable(false)
            builder.setPositiveButton("Có") { dialog, which ->


                    orderViewModel.changeOrderStatus(order.id,3)

            }

            builder.setNegativeButton("Không") { dialog, which ->
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    private fun initSpinnerShipper() {
        var spAdapter: UserSpinnerAdapter
        lifecycleScope.launch {
            userViewModel.userByRole.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        spAdapter = UserSpinnerAdapter(requireContext(), it.data!!)
                        binding.spShipper.adapter = spAdapter
                        binding.spShipper.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    p2: Int,
                                    p3: Long
                                ) {
                                    selectedShipper = spAdapter.getUsername(p2).toString()
                                }

                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                }
                            }
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }


    }

}