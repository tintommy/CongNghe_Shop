package com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.DetailApiService
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.OrderApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {
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

    private val _orderByStatus = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderByStatus = _orderByStatus.asStateFlow()

    private val _detailOrder = MutableStateFlow<Resource<List<OrderDetail>>>(Resource.Unspecified())
    val detailOrder = _detailOrder.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()

    private val _orderSearch = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderSearch = _orderSearch.asStateFlow()
    fun getOrderByStatus(status: Int) {
        viewModelScope.launch {
            _orderByStatus.emit(Resource.Loading())

            val response = orderApiService.getAllByStatus(status)
            if (response.isSuccessful) {
                _orderByStatus.emit(Resource.Success(response.body()!!.data))
            } else {
                _orderByStatus.emit(Resource.Error("Xảy ra lỗi"))
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

    fun changeOrderStatus(orderId: Int, status: Int) {
        viewModelScope.launch {
            _updateOrder.emit(Resource.Loading())

            val response = orderApiService.changeStatusOrder(orderId, status)
            if (response.isSuccessful) {
                _updateOrder.emit(Resource.Success(response.body()!!.data))
            } else {
                _updateOrder.emit(Resource.Error("Xảy ra lỗi"))
            }


        }
    }

    fun changeOrderStatusToShipping(orderId:Int, shipperUsername:String){
        viewModelScope.launch {
            _updateOrder.emit(Resource.Loading())

            val response = orderApiService.shippingOrder(orderId, shipperUsername)
            if (response.isSuccessful) {
                _updateOrder.emit(Resource.Success(response.body()!!.data))
            } else {
                _updateOrder.emit(Resource.Error("Xảy ra lỗi"))
            }


        }
    }

    fun searchOrder(search: String) {
        viewModelScope.launch {
            _orderSearch.emit(Resource.Loading())

            val response = orderApiService.searchOrder(search)
            if (response.isSuccessful) {
                _orderSearch.emit(Resource.Success(response.body()!!.data))
            } else {
                _orderSearch.emit(Resource.Error("Không tìm thấy"))
            }


        }
    }
}