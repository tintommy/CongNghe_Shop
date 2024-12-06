package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderMangerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentListOrderBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListOrderFragment : Fragment() {

    private lateinit var binding: FragmentListOrderBinding
    private var orderList: MutableList<Order> = mutableListOf()
    private lateinit var orderAdapter: OrderMangerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = arguments
        if (b != null) {
            orderList = b.getSerializable("orderList") as MutableList<Order>
            setUp()
        }


    }

    private fun setUp() {
        orderAdapter = OrderMangerAdapter()
        orderAdapter.differ.submitList(orderList)
        binding.rvListOrder.adapter = orderAdapter
        binding.rvListOrder.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        orderAdapter.setOnItemClickListener(object : OrderMangerAdapter.OnItemClickListener{
            override fun onItemClick(order: Order) {
                val b= Bundle()
                b.putSerializable("order",order)
                val fragment= OrderDetailFragment()
                fragment.arguments=b
                (activity as ManagerActivity).replaceFragment(fragment)
            }
        })
    }
}