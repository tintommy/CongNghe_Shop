package com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProductApiService {
    @POST("/api/admin/product/add")
    suspend fun addProduct(
        @Body productDTO: Product,
        @Query("productAvatar") avatar: String,
        @Query("otherImages") otherImages: List<String>,
        @Query("productDetail") productDetailJson: String
    ): Response<ResponseData<Product>>

    @PUT("/api/admin/product/update")
    suspend fun updateProduct(
        @Body productDTO: Product,
        @Query("productDetail") productDetailJson: String
    ): Response<ResponseData<Product>>

    @PUT("/api/admin/product/update-status")
    suspend fun updateStatusProduct(
        @Query("productId") id: Int
    ): Response<ResponseData<Product>>


    @DELETE("/api/admin/product/delete")
    suspend fun deleteProduct(
        @Query("productId") id: Int
    ): Response<ResponseData<Boolean>>

    @GET("/api/admin/product/get-page-product")
    suspend fun getPageProduct(
        @Query("offset") offset: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<ResponseData<List<Product>>>

    @GET("/api/admin/product/search-product")
    suspend fun searchProduct(
        @Query("searchContent") searchContent: String
    ): Response<ResponseData<List<Product>>>
}