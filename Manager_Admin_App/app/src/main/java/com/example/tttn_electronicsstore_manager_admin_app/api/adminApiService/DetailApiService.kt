package com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DetailApiService {
    @GET("/api/admin/detail/get-all")
    suspend fun getAll(): Response<ResponseData<List<Detail>>>
    @POST("/api/admin/detail/add")
    suspend fun add(@Query("name") name:String): Response<ResponseData<Detail>>
}