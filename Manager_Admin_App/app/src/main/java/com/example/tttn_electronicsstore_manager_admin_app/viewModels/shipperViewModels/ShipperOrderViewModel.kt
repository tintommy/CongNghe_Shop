package com.example.tttn_electronicsstore_manager_admin_app.viewModels.shipperViewModels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.shipperApiService.OrderApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand

import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ShipperOrderViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {
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

    private val _cancelOrder = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val cancelOrder = _cancelOrder.asStateFlow()

    private val _confirmOrderShipped = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val confirmOrderShipped = _confirmOrderShipped.asStateFlow()

    private val _orderSearch = MutableStateFlow<Resource<List<Order>>>(Resource.Unspecified())
    val orderSearch = _orderSearch.asStateFlow()
    fun getOrderByStatus(status: Int) {
        viewModelScope.launch {
            _orderByStatus.emit(Resource.Loading())

            val response = orderApiService.getAllByStatus(username, status)
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


    fun cancelOrder(orderId: Int, reason: String) {
        viewModelScope.launch {
            _cancelOrder.emit(Resource.Loading())

            val response = orderApiService.cancelOrder(orderId, reason)
            if (response.isSuccessful) {
                _cancelOrder.emit(Resource.Success(response.body()!!.data))
            } else {
                _cancelOrder.emit(Resource.Error("Xảy ra lỗi"))
            }


        }
    }


    fun confirmOrderShipped(orderId: Int, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            _confirmOrderShipped.emit(Resource.Loading())

            val storageRef = FirebaseStorage.getInstance().getReference()
            val imageName =
                "order" + "_" + orderId.toString() // Tên duy nhất cho mỗi ảnh
            val imageRef = storageRef.child("images_orders/$imageName")
            val stream = context.contentResolver.openInputStream(imageUri)
            val uploadTask = imageRef.putStream(stream!!)
            uploadTask.addOnFailureListener {
                viewModelScope.launch {
                    _confirmOrderShipped.emit(Resource.Error("Lỗi khi up ảnh, hãy thử lại"))
                }
            }.addOnSuccessListener {
                imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    viewModelScope.launch {
                        try {
                            val response = orderApiService.shippedOrder(orderId, imageUrl)
                            if (response.isSuccessful) {
                                if (response.body()!!.success == true)
                                    _confirmOrderShipped.emit(Resource.Success(response.body()!!.data))
                                else {
                                    _confirmOrderShipped.emit(Resource.Error("Lỗi"))
                                }
                            }
                        } catch (e: ConnectException) {
                            _confirmOrderShipped.emit(Resource.Error("Không thể kết nối tới Server"))
                        }
                    }

                }


            }


        }

    }

}