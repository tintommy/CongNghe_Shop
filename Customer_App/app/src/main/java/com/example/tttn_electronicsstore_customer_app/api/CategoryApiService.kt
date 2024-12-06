package com.example.tttn_electronicsstore_customer_app.api

import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApiService {
    @GET("/api/category/get-all")
    suspend fun getAllCategory(): Response<ResponseData<List<Category>>>
}