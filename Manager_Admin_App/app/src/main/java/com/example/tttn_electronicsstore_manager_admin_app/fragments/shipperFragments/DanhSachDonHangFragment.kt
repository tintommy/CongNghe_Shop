package com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderViewpagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentDanhSachDonHangBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments.CancelledOrdersShipperFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments.CompletedOrdersShipperFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments.ShippedOrdersShipperFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments.ShippingOrdersShipperFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DanhSachDonHangFragment : Fragment() {

    private lateinit var binding: FragmentDanhSachDonHangBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDanhSachDonHangBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        super.onViewCreated(view, savedInstanceState)
        //  initAdapter()
        val ordersFragments = arrayListOf<Fragment>(
            ShippingOrdersShipperFragment(),
            ShippedOrdersShipperFragment(),
            CompletedOrdersShipperFragment(),
            CancelledOrdersShipperFragment(),
        )
        val viewPager2Adapter =
            OrderViewpagerAdapter(ordersFragments, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Đang giao"
                1 -> tab.text = "Đã giao hàng"
                2 -> tab.text = "Hoàn thành"
                3 -> tab.text = "Giao thất bại"
            }
        }.attach()

    }

}