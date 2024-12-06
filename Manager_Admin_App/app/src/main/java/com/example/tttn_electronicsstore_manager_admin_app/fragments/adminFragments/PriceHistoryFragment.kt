package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.adapters.PriceHistoryAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentPriceHistoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvPriceHistoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.PriceHistory


class PriceHistoryFragment : Fragment() {

private lateinit var binding: FragmentPriceHistoryBinding
private lateinit var priceHistoryAdapter: PriceHistoryAdapter
private  var priceHistoryList: List<PriceHistory> = listOf()
    private var name=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentPriceHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            priceHistoryList = b.getSerializable("priceHistory") as List<PriceHistory>
            name= b.getString("name").toString()
            setUp()
        }


    }

    private fun setUp() {
        binding.tvName.text=name
        priceHistoryAdapter= PriceHistoryAdapter()
        priceHistoryAdapter.differ.submitList(priceHistoryList.sortedByDescending { it.id })
        binding.rvPriceHistory.adapter=priceHistoryAdapter
        binding.rvPriceHistory.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        setMaxAndMinPrice()
    }

    private fun setMaxAndMinPrice() {
        var min =priceHistoryList[0].price;
        var max=priceHistoryList[0].price;
        for (price in priceHistoryList){
            if(min>price.price)
                min=price.price
            if(max<price.price)
                max=price.price
        }

        binding.apply {
            tvHighPrice.text= Convert.formatNumberWithDotSeparator(max)+"đ"
            tvLowPrice.text= Convert.formatNumberWithDotSeparator(min)+"đ"
        }
    }

}