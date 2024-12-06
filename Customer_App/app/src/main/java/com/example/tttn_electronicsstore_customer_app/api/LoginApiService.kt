package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.request.ChangePassOTPRequest
import com.example.tttn_electronicsstore_customer_app.request.SignInRequest
import com.example.tttn_electronicsstore_customer_app.request.SignUpRequest
import com.example.tttn_electronicsstore_customer_app.util.LoginResponse
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface LoginApiService {
    @POST("/api/login/signin")
    suspend fun userLogin(@Body signInRequest: SignInRequest): Response<LoginResponse>

    @POST("/api/login/signup")
    suspend fun userSignup(@Body signUpRequest: SignUpRequest): Response<ResponseData<String>>

    @POST("/api/login/verify")
    suspend fun userVerify(
        @Body signUpRequest: SignUpRequest,
        @Query("otp") otp: String
    ): Response<ResponseData<Boolean>>

    @POST("/api/login/resendOTP")
    suspend fun resendOTP(@Query("username") username: String): Response<ResponseData<Boolean>>

    @POST("/api/login/sendOTP")
    suspend fun sendOTP(
        @Query("username") username: String,
        @Query("email") email: String
    ): Response<ResponseData<Boolean>>

    @POST("/api/login/change-pass-otp")
    suspend fun changePassOTP(
       @Body changePassOTPRequest: ChangePassOTPRequest
    ): Response<ResponseData<Boolean>>
}