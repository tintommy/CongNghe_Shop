package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.OrderApiService
import com.example.tttn_electronicsstore_customer_app.models.Order
import com.example.tttn_electronicsstore_customer_app.models.OrderDetail
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var orderApiService: OrderApiService


    private val _addOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val addOrder = _addOrder.asStateFlow()

    private val _orderByStatus = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderByStatus = _orderByStatus.asStateFlow()

    private val _detailOrder = MutableStateFlow<Resource<List<OrderDetail>>>(Resource.Unspecified())
    val detailOrder = _detailOrder.asStateFlow()

    private val _updateOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val updateOrder = _updateOrder.asStateFlow()

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        orderApiService = retrofit.create(OrderApiService::class.java)

    }


    fun addOrder(receiveAddress: ReceiveAddress, productPrice: Int, ship:Int, onlinePay: Boolean) {
        viewModelScope.launch {

            val order = Order(
                "",
                0,
                receiveAddress.receiverName,
                receiveAddress.receiverPhone,
                receiveAddress.receiverAddress,
                1,
                productPrice,
                ship,"",
                username,
                onlinePay,
                null
            )

            _addOrder.emit(Resource.Loading())

            val response = orderApiService.addOrder(order)
            if (response.isSuccessful) {
                _addOrder.emit(Resource.Success(response.body()!!.data))

            } else {
                _addOrder.emit(Resource.Error("Lỗi khi lấy user"))

            }
        }

    }

    fun getOrderByStatus(status: Int) {
        viewModelScope.launch {
            _orderByStatus.emit(Resource.Loading())

            val response = orderApiService.getByStatus(username, status)
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
}