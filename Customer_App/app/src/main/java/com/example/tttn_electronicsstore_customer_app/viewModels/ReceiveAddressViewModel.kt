package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.ReceiveAddressApiService
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiveAddressViewModel @Inject constructor(private var sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var receiveAddressApiService: ReceiveAddressApiService


    private val _address = MutableStateFlow<Resource<List<ReceiveAddress>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    private val _addAddress = MutableStateFlow<Resource<ReceiveAddress>>(Resource.Unspecified())
    val addAddress = _addAddress.asStateFlow()
    private val _deleteAddress = MutableSharedFlow<Resource<Boolean>>()
    val deleteAddress = _deleteAddress.asSharedFlow()

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        receiveAddressApiService = retrofit.create(ReceiveAddressApiService::class.java)

    }


    fun getAddress() {
        viewModelScope.launch {
            _address.emit(Resource.Loading())

            val response = receiveAddressApiService.getAddress(username)
            if (response.isSuccessful) {
                _address.emit(Resource.Success(response.body()!!.data))

            } else {
                _address.emit(Resource.Error("Lỗi khi lấy address"))

            }
        }

    }

    fun addAddress(
        id: Int,
        addressName: String,
        receiverName: String,
        receiverPhone: String,
        receiverAddress: String,
        addressCode:String
    ) {
        viewModelScope.launch {
            _addAddress.emit(Resource.Loading())
            val receiveAddress = ReceiveAddress(
                addressName,
                false,
                id,
                receiverAddress,
                receiverName,
                receiverPhone,
                username,
                addressCode
            )
            val response = receiveAddressApiService.addAddress(receiveAddress)
            if (response.isSuccessful) {
                _addAddress.emit(Resource.Success(response.body()!!.data))

            } else {
                _addAddress.emit(Resource.Error("Lỗi khi thêm address"))

            }
        }

    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _deleteAddress.emit(Resource.Loading())

            val response = receiveAddressApiService.deleteAddress(id)
            if (response.isSuccessful) {
                _deleteAddress.emit(Resource.Success(response.body()!!.data))

            } else {
                _deleteAddress.emit(Resource.Error("Lỗi khi xoá address"))

            }
        }

    }
}