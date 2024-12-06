package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.orderFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PreparingOrderFragment : BaseOrderFragment() {


    private val orderViewModel by viewModels<OrderViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.getOrderByStatus(2)
        lifecycleScope.launch {
            orderViewModel.orderByStatus.collectLatest {
                when(it){
                    is Resource.Loading->{}
                    is Resource.Success->{
                        if (it.data == null) {
                            binding.layoutEmpty.visibility = View.VISIBLE
                        } else {
                            binding.layoutEmpty.visibility = View.GONE
                            orderAdapter.differ.submitList(it.data)
                        }


                    }
                    is Resource.Error->{}
                    else->{}
                }
            }
        }

    }


}