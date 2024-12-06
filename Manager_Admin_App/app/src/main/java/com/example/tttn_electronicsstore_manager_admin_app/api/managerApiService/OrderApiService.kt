package com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService

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
    @GET("api/manager/order/ordersByDate")
    suspend fun getAllByDate(@Query("fromDate") fromDate: String,@Query("toDate") toDate: String): Response<ResponseData<List<Order>>>
    @GET("api/manager/order/detail")
    suspend fun getDetailById(@Query("orderId") id: Int): Response<ResponseData<List<OrderDetail>>>


}