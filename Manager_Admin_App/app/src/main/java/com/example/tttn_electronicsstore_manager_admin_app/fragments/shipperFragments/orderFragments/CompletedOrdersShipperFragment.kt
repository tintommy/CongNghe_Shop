package com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.OrderViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.shipperViewModels.ShipperOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CompletedOrdersShipperFragment: BaseOrderShipperFragment() {
    private val orderViewModel by viewModels<ShipperOrderViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.getOrderByStatus(5)
        lifecycleScope.launch {
            orderViewModel.orderByStatus.collectLatest {
                when(it){
                    is Resource.Loading->{}
                    is Resource.Success->{

                        if (it.data==null) {
                            binding.tvEmpty.visibility = View.VISIBLE
                            binding.rvOrder.visibility = View.GONE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvOrder.visibility = View.VISIBLE
                            orderAdapter.differ.submitList(it.data?.sortedByDescending { it.id })
                        }

                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }
                    else->{}
                }
            }
        }

    }
}