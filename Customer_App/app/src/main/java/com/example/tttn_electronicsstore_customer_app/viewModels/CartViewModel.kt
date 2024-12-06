package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.api.CartApiService
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var cartApiService: CartApiService


    private val _addCart = MutableSharedFlow<Resource<Cart>>()
    val addCart = _addCart.asSharedFlow()

    private val _deleteCart = MutableSharedFlow<Resource<Boolean>>()
    val deleteCart = _deleteCart.asSharedFlow()

    private val _countCart = MutableSharedFlow<Resource<Int>>()
    val countCart = _countCart.asSharedFlow()

    private val _getCart = MutableSharedFlow<Resource<List<Cart>>>()
    val getCart = _getCart.asSharedFlow()

    private val _getCartOrder = MutableSharedFlow<Resource<List<Cart>>>()
    val getCartOrder = _getCartOrder.asSharedFlow()

    private val _checkCartQuantity = MutableSharedFlow<Resource<List<Product>>>()
    val checkCartQuantity = _checkCartQuantity.asSharedFlow()

    private val _changeStatus = MutableSharedFlow<Resource<Boolean>>()
    val changeStatus = _changeStatus.asSharedFlow()


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        cartApiService = retrofit.create(CartApiService::class.java)

    }


    fun addCart(productId:Int, number:Int) {
        viewModelScope.launch {
            _addCart.emit(Resource.Loading())

            val response = cartApiService.addCart(username,productId, number)
            if (response.isSuccessful) {
                _addCart.emit(Resource.Success(response.body()!!.data))

            } else {
                _addCart.emit(Resource.Error("Lỗi khi thêm vào giỏ"))

            }
        }

    }

    fun countCart() {
        viewModelScope.launch {
            _countCart.emit(Resource.Loading())

            val response = cartApiService.countCart(username)
            if (response.isSuccessful) {
                _countCart.emit(Resource.Success(response.body()!!.data))

            }
        }

    }
    fun getCartUser() {
        viewModelScope.launch {
            _getCart.emit(Resource.Loading())

            val response = cartApiService.getCart(username)
            if (response.isSuccessful) {
                _getCart.emit(Resource.Success(response.body()!!.data))

            } else {
                _getCart.emit(Resource.Error("Lỗi khi lấy giỏ hàng"))

            }
        }

    }
    fun getCartOrder() {
        viewModelScope.launch {
            _getCartOrder.emit(Resource.Loading())

            val response = cartApiService.getCartOrder(username,true)
            if (response.isSuccessful) {
                _getCartOrder.emit(Resource.Success(response.body()!!.data))

            } else {
                _getCartOrder.emit(Resource.Error("Lỗi khi lấy giỏ hàng"))

            }
        }

    }
    fun changeStatus(cartId:Int, status:Boolean){
        viewModelScope.launch {
            val response= cartApiService.changeStatus(cartId, status)
            if(response.isSuccessful){
                _changeStatus.emit(Resource.Success(response.body()!!.data))
            }
        }
    }

    fun deleteCart(cartId:Int){
        viewModelScope.launch {
            val response= cartApiService.delete(cartId)
            if(response.isSuccessful)
                _deleteCart.emit(Resource.Success(response.body()!!.data))
        }
    }



//    fun checkCart() {
//        viewModelScope.launch {
//            _checkCartQuantity.emit(Resource.Loading())
//
//            val response = cartApiService.checkCart(username)
//            if (response.isSuccessful) {
//                _checkCartQuantity.emit(Resource.Success(response.body()!!.data))
//
//            }
//            else{
//                _checkCartQuantity.emit(Resource.Error("Lỗi"))
//            }
//        }
//
//    }
}