package com.example.tttn_electronicsstore_manager_admin_app.api

import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    @GET("/api/user/{username}")
    suspend fun getUser(@Path(value = "username") username: String): Response<ResponseData<User>>

    @GET("/api/user/get")
    suspend fun getUserByRole(@Query("role") role:String): Response<ResponseData<List<User>>>
    @GET("/api/user/get-all-staff")
    suspend fun getAllStaff(): Response<ResponseData<List<User>>>

    @POST("/api/user/add")
    suspend fun addStaff(@Body user:User): Response<ResponseData<String>>
    @PUT("/api/user/update")
    suspend fun updateStaff(@Body user:User): Response<ResponseData<User>>

    @POST("/api/user/change-pass")
    suspend fun changePass(
        @Query("username") username: String,
        @Query("pass") pass: String,
        @Query("newPass") newPass: String
    ): Response<ResponseData<User>>


}