package com.example.tttn_electronicsstore_customer_app.fragments.orderFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentBaseOrderBinding

import com.example.tttn_electronicsstore_customer_app.adapters.OrderMangerAdapter
import com.example.tttn_electronicsstore_customer_app.models.Order


open class BaseOrderFragment : Fragment() {
protected lateinit var binding: FragmentBaseOrderBinding
protected  var orderAdapter: OrderMangerAdapter = OrderMangerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBaseOrderBinding.inflate(layoutInflater)
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
               findNavController().navigate(R.id.action_orderFragment_to_orderDetailFragment,b)
            }
        })
    }

}