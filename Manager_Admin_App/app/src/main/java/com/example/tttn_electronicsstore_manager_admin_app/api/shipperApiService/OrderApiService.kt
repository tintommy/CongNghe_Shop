package com.example.tttn_electronicsstore_manager_admin_app.api.shipperApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

interface OrderApiService {
    @GET("api/shipper/order/status")
    suspend fun getAllByStatus(
        @Query("username") username: String,
        @Query("status") status: Int
    ): Response<ResponseData<List<Order>>>

    @GET("api/shipper/order/detail")
    suspend fun getDetailById(@Query("orderId") id: Int): Response<ResponseData<List<OrderDetail>>>

    @PUT("api/shipper/order/change-status")
    suspend fun changeStatusOrder(
        @Query("orderId") id: Int,
        @Query("status") status: Int
    ): Response<ResponseData<Order>>

    @PUT("api/shipper/order/cancel-order")
    suspend fun cancelOrder(
        @Query("orderId") id: Int,
        @Query("reason") reason: String
    ): Response<ResponseData<Order>>

    @PUT("api/shipper/order/shipped-order")
    suspend fun shippedOrder(
        @Query("orderId") id: Int,
        @Query("imageUrl") imageUrl: String
    ): Response<ResponseData<Order>>
}