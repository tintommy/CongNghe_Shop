package com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.ShipperActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderMangerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentBaseOrderShipperBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order


open class BaseOrderShipperFragment : Fragment() {
protected lateinit var binding: FragmentBaseOrderShipperBinding
protected  var orderAdapter:OrderMangerAdapter= OrderMangerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBaseOrderShipperBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

    }

    private fun initAdapter() {
       binding.rvOrder.adapter=orderAdapter
        binding.rvOrder.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        orderAdapter.setOnItemClickListener(object: OrderMangerAdapter.OnItemClickListener{
            override fun onItemClick(order: Order) {
                val b= Bundle()
                b.putSerializable("order",order)
                val fragment= OrderDetailShipperFragment()
                fragment.arguments=b
                (activity as ShipperActivity).replaceFragment(fragment)
            }
        })
    }

}