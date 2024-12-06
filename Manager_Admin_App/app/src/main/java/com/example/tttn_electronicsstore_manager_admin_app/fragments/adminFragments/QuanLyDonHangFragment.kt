package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderMangerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderViewpagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLyDonHangBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.CancelledOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.CompletedOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.OrderDetailFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.PendingOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.PreparingOrderFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.ShipFailedOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.ShippedOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments.ShippingOrdersFragment
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.OrderViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuanLyDonHangFragment : Fragment() {

    private val orderViewModel by viewModels<OrderViewModel>()
    private val orderAdapter = OrderMangerAdapter()

    var querySearch: String = ""
    val delayTime: Long = 700

    val handler = Handler()

    val search = Runnable {
        if (querySearch != "") {
            orderViewModel.searchOrder(querySearch)
        }
    }

    private lateinit var binding: FragmentQuanLyDonHangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLyDonHangBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        val ordersFragments = arrayListOf<Fragment>(
            PendingOrdersFragment(),
            PreparingOrderFragment(),
            ShippingOrdersFragment(),
            ShippedOrdersFragment(),
            CompletedOrdersFragment(),
            ShipFailedOrdersFragment(),
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
                3 -> tab.text = "Đã giao hàng"
                4 -> tab.text = "Hoàn thành"
                5 -> tab.text = "Giao hàng thất bại"
                6 -> tab.text = "Đã huỷ"


            }
        }.attach()





        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0?.toString().equals("") || p0 == null) {
                    orderAdapter.differ.submitList(ArrayList<Order>())
                    orderAdapter.notifyDataSetChanged()
                    binding.rvOrder.visibility = View.GONE
                    querySearch = ""
                }

                if (p0 != null && !p0.equals("")) {
                    querySearch = p0.toString()
                    handler.removeCallbacks(search)
                    handler.postDelayed(search, delayTime)

                }
            }
        })



        lifecycleScope.launch {
            orderViewModel.orderSearch.collectLatest {
                when(it){
                    is Resource.Loading->{}
                    is Resource.Success->{
                        binding.rvOrder.visibility=View.VISIBLE
                        orderAdapter.differ.submitList(it.data?.sortedByDescending { it.id })
                    }
                    is Resource.Error->{
                        binding.rvOrder.visibility=View.GONE
                    }
                    else->{}
                }
            }
        }

    }

    private fun initAdapter() {
        binding.rvOrder.adapter = orderAdapter
        binding.rvOrder.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        orderAdapter.setOnItemClickListener(object : OrderMangerAdapter.OnItemClickListener {
            override fun onItemClick(order: Order) {
                val b = Bundle()
                b.putSerializable("order", order)
                val fragment = OrderDetailFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }

        })
    }

}