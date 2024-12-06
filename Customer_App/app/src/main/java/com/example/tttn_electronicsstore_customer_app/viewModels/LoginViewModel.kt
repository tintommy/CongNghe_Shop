package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.request.SignInRequest
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.request.SignUpRequest
import com.example.tttn_electronicsstore_customer_app.api.LoginApiService
import com.example.tttn_electronicsstore_customer_app.request.ChangePassOTPRequest
import com.example.tttn_electronicsstore_customer_app.util.LoginResponse
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _signup = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val signup = _signup.asStateFlow()

    private val _verify = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val verify = _verify.asStateFlow()
    private val _sendOTP = MutableSharedFlow<Resource<Boolean>>()
    val sendOTP = _sendOTP.asSharedFlow()

    private val _changePassOTP = MutableSharedFlow<Resource<Boolean>>()
    val changePassOTP = _changePassOTP.asSharedFlow()

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
                    }
                } else {

                    _login.emit(Resource.Error("Error"))

                }
            }catch (e:ConnectException){
                _login.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }

    }


    fun userSignUp(username: String, email: String, password: String, fullName:String) {
        viewModelScope.launch {
            _signup.emit(Resource.Loading())
            val signUpRequest= SignUpRequest(username,fullName,password,email)
            try {


                val response = loginApiService.userSignup(signUpRequest)
                if (response.isSuccessful) {
                    _signup.emit(Resource.Success(response.body()!!.desc!!))

                } else {

                    _signup.emit(Resource.Error("Error"))

                }
            }catch (e:ConnectException){
                _signup.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }

    }


    fun userVerify(signUpRequest: SignUpRequest, otp:String) {
        viewModelScope.launch {
            _verify.emit(Resource.Loading())
            try {

                val response = loginApiService.userVerify(signUpRequest,otp)
                if (response.isSuccessful) {
                    _verify.emit(Resource.Success(response.body()!!.data))

                } else {

                    _verify.emit(Resource.Error("Error"))

                }
            }catch (e:ConnectException){
                _signup.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }

    }

    fun sendOTP(username:String, email:String) {

        viewModelScope.launch {
            _sendOTP.emit(Resource.Loading())

            val response = loginApiService.sendOTP(username, email)
            if (response.isSuccessful) {
                _sendOTP.emit(Resource.Success(response.body()!!.data))

            } else {
                _sendOTP.emit(Resource.Error("Lỗi khi gửi OTP"))

            }
        }

    }

    fun changePassOTP(changePassOTPRequest: ChangePassOTPRequest) {

        viewModelScope.launch {
            _changePassOTP.emit(Resource.Loading())

            val response = loginApiService.changePassOTP(changePassOTPRequest)
            if (response.isSuccessful) {
                _changePassOTP.emit(Resource.Success(response.body()!!.data))

            } else {
                _changePassOTP.emit(Resource.Error("Lỗi truy cập sever"))

            }
        }

    }

}