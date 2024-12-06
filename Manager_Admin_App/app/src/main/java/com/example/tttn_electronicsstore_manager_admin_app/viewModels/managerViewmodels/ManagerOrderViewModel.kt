package com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.DetailApiService
import com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService.OrderApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerOrderViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var orderApiService: OrderApiService

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        orderApiService = retrofit.create(OrderApiService::class.java)

    }

    private val _orderByDate = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderByDate = _orderByDate.asStateFlow()

    private val _detailOrder = MutableStateFlow<Resource<List<OrderDetail>>>(Resource.Unspecified())
    val detailOrder = _detailOrder.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()



    fun getByDate(fromDate:String, toDate:String){
        viewModelScope.launch {
            val response= orderApiService.getAllByDate(fromDate, toDate)
            _orderByDate.emit(Resource.Loading())
            if(response.isSuccessful)
            {
                _orderByDate.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _orderByDate.emit(Resource.Error("Lỗi khi tải đơn hàng"))
            }
        }
    }

    fun getDetailByOrderId(orderId: Int) {
        viewModelScope.launch {
            _detailOrder.emit(Resource.Loading())

            val response = orderApiService.getDetailById(orderId)
            if (response.isSuccessful) {
                _detailOrder.emit(Resource.Success(response.body()!!.data))
            } else {
                _detailOrder.emit(Resource.Error("Xảy ra lỗi"))
            }


        }

    }
}