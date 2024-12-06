package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CartApiService {
    @POST("/api/cart/add-cart")
    suspend fun addCart(
        @Query("username") username: String,
        @Query("productId") productId: Int,
        @Query("number") number: Int,
    ): Response<ResponseData<Cart>>

    @GET("/api/cart/count")
    suspend fun countCart(@Query("username") username: String): Response<ResponseData<Int>>

    @GET("/api/cart/get-cart")
    suspend fun getCart(@Query("username") username: String): Response<ResponseData<List<Cart>>>
    @GET("/api/cart/get-cart-order")
    suspend fun getCartOrder(@Query("username") username: String,@Query("status") status: Boolean): Response<ResponseData<List<Cart>>>

    @PUT("/api/cart/change-status")
    suspend fun changeStatus(
        @Query("id") id: Int,
        @Query("status") status: Boolean
    ): Response<ResponseData<Boolean>>

    @DELETE("/api/cart/delete")
    suspend fun delete(
        @Query("id") id: Int,
    ): Response<ResponseData<Boolean>>


}