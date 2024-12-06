package com.example.tttn_electronicsstore_manager_admin_app.api

import com.example.appxemphim.request.SignInRequest
import com.example.tttn_electronicsstore_manager_admin_app.util.LoginResponse
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("/api/login/signin")
    suspend fun userLogin(@Body signInRequest: SignInRequest): Response<LoginResponse>
}