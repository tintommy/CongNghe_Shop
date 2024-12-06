package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.UserApiService
import com.example.tttn_electronicsstore_customer_app.models.Review
import com.example.tttn_electronicsstore_customer_app.models.User
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var userService: UserApiService


    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _changePass = MutableSharedFlow<Resource<Boolean>>()
    val changePass = _changePass.asSharedFlow()

    private val _addReview = MutableSharedFlow<Resource<Review>>()
    val addReview = _addReview.asSharedFlow()
    private val _updateUser = MutableSharedFlow<Resource<User>>()
    val updateUser = _updateUser.asSharedFlow()


    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        userService = retrofit.create(UserApiService::class.java)

    }


    fun getUser() {
        viewModelScope.launch {
            _user.emit(Resource.Loading())

            val response = userService.getUser(username)
            if (response.isSuccessful) {
                _user.emit(Resource.Success(response.body()!!.data))

            } else {
                _user.emit(Resource.Error("Lỗi khi lấy user"))

            }
        }

    }

    fun changePass(pass:String, newPass:String) {
        viewModelScope.launch {
            _changePass.emit(Resource.Loading())
            val response = userService.changePass(username, pass, newPass)
            if (response.isSuccessful) {
                _changePass.emit(Resource.Success(response.body()!!.success!!))

            } else {
                _changePass.emit(Resource.Error("Lỗi khi đổi mật khẩu"))

            }
        }

    }

    fun addCmt(productId:Int, star:Int,content:String, orderDetailId:Int) {
        val review=Review(content,"",0,productId,"","",username,star)
        viewModelScope.launch {
            _addReview.emit(Resource.Loading())

            val response = userService.addReview(review,orderDetailId)
            if (response.isSuccessful) {
                _addReview.emit(Resource.Success(response.body()!!.data))

            } else {
                _addReview.emit(Resource.Error("Lỗi khi lấy user"))

            }
        }

    }

    fun updateUser(user:User){
        viewModelScope.launch {

            _updateUser.emit(Resource.Loading())
            val respone = userService.updateUser(user)
            if (respone.isSuccessful) {
                _updateUser.emit(Resource.Success(respone.body()!!.data))
            } else {
                _updateUser.emit(Resource.Error("Xảy ra lỗi"))
            }

        }
    }

}