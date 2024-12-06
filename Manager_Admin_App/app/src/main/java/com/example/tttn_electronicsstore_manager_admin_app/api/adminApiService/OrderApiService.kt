package com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService

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
    @GET("api/admin/order/status")
    suspend fun getAllByStatus(@Query("status") status: Int): Response<ResponseData<List<Order>>>

    @GET("api/admin/order/detail")
    suspend fun getDetailById(@Query("orderId") id: Int): Response<ResponseData<List<OrderDetail>>>

    @PUT("api/admin/order/change-status")
    suspend fun changeStatusOrder(
        @Query("orderId") id: Int,
        @Query("status") status: Int
    ): Response<ResponseData<Order>>


    @PUT("api/admin/order/shipping")
    suspend fun shippingOrder(
        @Query("orderId") id: Int,
        @Query("shipperUsername") shipperUsername: String
    ): Response<ResponseData<Order>>

    @GET("/api/admin/order/search-order")
    suspend fun searchOrder(
        @Query("searchContent") searchContent: String
    ): Response<ResponseData<List<Order>>>
}