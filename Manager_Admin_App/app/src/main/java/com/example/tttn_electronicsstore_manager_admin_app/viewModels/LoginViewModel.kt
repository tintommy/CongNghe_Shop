package com.example.tttn_electronicsstore_manager_admin_app.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appxemphim.request.SignInRequest
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.LoginApiService
import com.example.tttn_electronicsstore_manager_admin_app.util.LoginResponse
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {
    private val loginApiService: LoginApiService =
        API_Instance.getClient("").create(LoginApiService::class.java)


    private val _login = MutableStateFlow<Resource<LoginResponse>>(Resource.Unspecified())
    val login = _login.asStateFlow()

    fun userLogin(username: String, email: String, password: String) {
        viewModelScope.launch {
            _login.emit(Resource.Loading())
            val signInRequest: SignInRequest = SignInRequest(username, email, password)
            try {


                val response = loginApiService.userLogin(signInRequest)
                if (response.isSuccessful) {
                    _login.emit(Resource.Success(response.body()!!))
                    if(response.body()!!.success){
                        Log.e("MyTag", response.body()!!.username)
                        val editor = sharedPref.edit()
                        editor.putString("token", response.body()!!.token )
                        editor.putString("username", response.body()!!.username)
                        editor.apply()

                        Log.e("MyTag",response.body()!!.token )
                        Log.e("MyTag",response.body()!!.username )
                    }
                } else {

                    _login.emit(Resource.Error("Error"))

                }
            }catch (e:ConnectException){
                _login.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }

    }


}