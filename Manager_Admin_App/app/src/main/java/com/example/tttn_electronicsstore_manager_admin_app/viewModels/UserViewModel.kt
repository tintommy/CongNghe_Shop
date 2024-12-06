package com.example.tttn_electronicsstore_manager_admin_app.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.UserApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
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

    private val _userByRole = MutableSharedFlow<Resource<List<User>>>()
    val userByRole = _userByRole.asSharedFlow()

    private val _getAllStaff = MutableSharedFlow<Resource<List<User>>>()
    val getAllStaff = _getAllStaff.asSharedFlow()

    private val _addStaff = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val addStaff = _addStaff.asStateFlow()

    private val _updateStaff = MutableSharedFlow<Resource<User>>()
    val updateStaff = _updateStaff.asSharedFlow()
    private val _changePass = MutableSharedFlow<Resource<Boolean>>()
    val changePass = _changePass.asSharedFlow()

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
            Log.e("MyTag", username)
            val response = userService.getUser(username)
            if (response.isSuccessful) {
                _user.emit(Resource.Success(response.body()!!.data))
                _user.emit(Resource.Unspecified())

            } else {
                _user.emit(Resource.Error("Lỗi khi lấy user"))

            }
        }

    }


    fun getUserByRole(role: String) {
        viewModelScope.launch {

            _userByRole.emit(Resource.Loading())
            val respone = userService.getUserByRole(role)
            if (respone.isSuccessful) {
                _userByRole.emit(Resource.Success(respone.body()!!.data))
            } else {
                _userByRole.emit(Resource.Error("Lỗi khi load user"))
            }

        }
    }


    fun getAllStaff() {
        viewModelScope.launch {

            _getAllStaff.emit(Resource.Loading())
            val respone = userService.getAllStaff()
            if (respone.isSuccessful) {
                _getAllStaff.emit(Resource.Success(respone.body()!!.data.sortedBy { it.role }))
            } else {
                _getAllStaff.emit(Resource.Error("Lỗi khi load user"))
            }

        }
    }


    fun addStaff(user:User){
        viewModelScope.launch {

            _addStaff.emit(Resource.Loading())
            val respone = userService.addStaff(user)
            if (respone.isSuccessful) {
                _addStaff.emit(Resource.Success(respone.body()!!.data))
            } else {
                _addStaff.emit(Resource.Error("Lỗi khi thêm user"))
            }

        }
    }

    fun updateStaff(user:User){
        viewModelScope.launch {

            _updateStaff.emit(Resource.Loading())
            val respone = userService.updateStaff(user)
            if (respone.isSuccessful) {
                _updateStaff.emit(Resource.Success(respone.body()!!.data))
            } else {
                _updateStaff.emit(Resource.Error("Xảy ra lỗi"))
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

}