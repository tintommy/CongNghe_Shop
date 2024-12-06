package com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService

import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductReview
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductSale
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.models.Review
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ProductApiService {


    @GET("/api/manager/product/most-bought")
    suspend fun getMostBoughtProduct(
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("limit") limit: Int
    ): Response<ResponseData<List<ProductSale>>>

    @GET("/api/manager/product/most-review")
    suspend fun getMostReviewProduct(
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String,
        @Query("limit") limit: Int
    ): Response<ResponseData<List<ProductReview>>>

    @GET("/api/manager/product/review-product-statistic")
    suspend fun getReviewProduct(
        @Query("productId") productId: Int,
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String
    ): Response<ResponseData<List<Review>>>
}