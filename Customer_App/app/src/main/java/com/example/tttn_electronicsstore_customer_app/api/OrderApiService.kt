package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.Order
import com.example.tttn_electronicsstore_customer_app.models.OrderDetail
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface OrderApiService {
    @POST("/api/order/add")
    suspend fun addOrder(@Body order: Order): Response<ResponseData<Order>>

    @GET("/api/order/status")
    suspend fun getByStatus(@Query("username") username:String,
                         @Query("status") status:Int): Response<ResponseData<List<Order>>>

    @GET("api/order/detail")
    suspend fun getDetailById(@Query("orderId") id: Int): Response<ResponseData<List<OrderDetail>>>

    @PUT("api/order/change-status")
    suspend fun changeStatusOrder(
        @Query("orderId") id: Int,
        @Query("status") status: Int
    ): Response<ResponseData<Order>>
}