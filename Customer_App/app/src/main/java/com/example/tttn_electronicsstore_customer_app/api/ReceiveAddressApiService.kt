package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ReceiveAddressApiService {
    @GET("/api/receive-address/get")
    suspend fun getAddress(@Query("username") username: String): Response<ResponseData<List<ReceiveAddress>>>

    @POST("/api/receive-address/add")
    suspend fun addAddress(@Body receiveAddress: ReceiveAddress): Response<ResponseData<ReceiveAddress>>

    @DELETE("/api/receive-address/delete")
    suspend fun deleteAddress(@Query("id") id:Int): Response<ResponseData<Boolean>>
}