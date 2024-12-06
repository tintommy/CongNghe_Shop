package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.Review
import com.example.tttn_electronicsstore_customer_app.models.User
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
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

    @POST("/api/user/change-pass")
    suspend fun changePass(
        @Query("username") username: String,
        @Query("pass") pass: String,
        @Query("newPass") newPass: String
    ): Response<ResponseData<User>>


    @POST("/api/review/add")
    suspend fun addReview(
        @Body review: Review,
        @Query("orderDetailId") orderDetailId:Int
    ): Response<ResponseData<Review>>

    @PUT("/api/user/update-customer")
    suspend fun updateUser(@Body user:User): Response<ResponseData<User>>
}