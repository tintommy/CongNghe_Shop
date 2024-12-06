package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.models.Review
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApiService {

    @GET("/api/product/get-product")
    suspend fun getProduct(
        @Query("id") id: Int
    ): Response<ResponseData<Product>>

    @GET("/api/product/get-page-product")
    suspend fun getPageProduct(
        @Query("categoryId") categoryId: Int,
        @Query("type") type: String,
        @Query("orderType") orderType: String,
        @Query("offSet") offSet: Int,
        @Query("pageSize") pageSize: Int,
    ): Response<ResponseData<List<Product>>>

    @GET("/api/product/top-new")
    suspend fun getTopNewProduct(
        @Query("num") num: Int
    ): Response<ResponseData<List<Product>>>

    @GET("/api/product/top-bought")
    suspend fun getTopBoughtProduct(
        @Query("num") num: Int
    ): Response<ResponseData<List<Product>>>



    @GET("/api/product/search-product")
    suspend fun searchProduct(
        @Query("searchContent") searchContent: String
    ): Response<ResponseData<List<Product>>>


    @GET("/api/product/filter-product")
    suspend fun filterProduct(
        @Query("categoryId") categoryId: Int,
        @Query("brandId") brandId: Int,
        @Query("productDetail") productDetail: String,
        @Query("fromPrice") fromPrice: Long,
        @Query("toPrice") toPrice: Long,
    ): Response<ResponseData<List<Product>>>

    @GET("/api/product/filter-product-priority")
    suspend fun filterProductPriority(
        @Query("productDetail") productDetail: String,
        @Query("productPriority") productPriority: String,
        @Query("categoryId") categoryId: Int,
        @Query("brandId") brandId: Int,
        @Query("fromPrice") fromPrice: Long,
        @Query("toPrice") toPrice: Long,
    ): Response<ResponseData<List<Product>>>

    @GET("/api/review/get-review")
    suspend fun getReview(
        @Query("productId") productId: Int,
        @Query("offset") offset: Int,
        @Query("pageSize") pageSize: Int,

    ): Response<ResponseData<List<Review>>>
}