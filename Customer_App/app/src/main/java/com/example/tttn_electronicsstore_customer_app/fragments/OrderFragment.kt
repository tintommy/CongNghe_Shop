package com.example.tttn_electronicsstore_customer_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentOrderBinding
import com.example.tttn_electronicsstore_customer_app.adapters.OrderMangerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.OrderViewpagerAdapter
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.CancelledOrdersFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.CompletedOrdersFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.PendingOrdersFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.PreparingOrderFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.ShipFailedFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.ShippedOrdersFragment
import com.example.tttn_electronicsstore_customer_app.fragments.orderFragments.ShippingOrdersFragment
import com.example.tttn_electronicsstore_customer_app.helper.showBottomNavigation
import com.example.tttn_electronicsstore_customer_app.viewModels.OrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class OrderFragment : Fragment() {

    private val orderViewModel by viewModels<OrderViewModel>()
    private val orderAdapter = OrderMangerAdapter()



    private lateinit var binding: FragmentOrderBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showBottomNavigation()
        val ordersFragments = arrayListOf<Fragment>(
            PendingOrdersFragment(),
            PreparingOrderFragment(),
            ShippingOrdersFragment(),
            ShippedOrdersFragment(),
            CompletedOrdersFragment(),
            ShipFailedFragment(),
            CancelledOrdersFragment(),
        )
        val viewPager2Adapter =
            OrderViewpagerAdapter(ordersFragments, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Chờ xác nhận"
                1 -> tab.text = "Đang chuẩn bị hàng"
                2 -> tab.text = "Đang giao"
                3 -> tab.text = "Đã giao"
                4 -> tab.text = "Hoàn thành"
                5 -> tab.text = "Giao hàng thất bại"
                6 -> tab.text = "Đã huỷ"


            }
        }.attach()



    }


}