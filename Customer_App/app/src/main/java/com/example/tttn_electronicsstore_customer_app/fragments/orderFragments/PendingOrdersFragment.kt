package com.example.tttn_electronicsstore_customer_app.fragments.orderFragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingOrdersFragment : BaseOrderFragment(){
    private val orderViewModel by viewModels<OrderViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.getOrderByStatus(1)
        lifecycleScope.launch {
            orderViewModel.orderByStatus.collectLatest {
                when(it){
                    is Resource.Loading->{}
                    is Resource.Success->{
                        if(it.data==null){
                            binding.layoutEmpty.visibility=View.VISIBLE
                            orderAdapter.differ.submitList(it.data)
                        }else{
                            binding.layoutEmpty.visibility=View.GONE
                            orderAdapter.differ.submitList(it.data.sortedByDescending { it.id })
                        }


                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(), "Xảy ra lỗi khi tải danh sách đơn", Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }



    }
}