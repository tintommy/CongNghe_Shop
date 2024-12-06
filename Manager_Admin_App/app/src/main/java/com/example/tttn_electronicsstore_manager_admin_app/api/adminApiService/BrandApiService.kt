package com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface BrandApiService {
    @POST("/api/admin/brand/add")
    suspend fun addBrand(@Body brand: Brand): Response<ResponseData<Brand>>

    @GET("/api/admin/brand/get-all")
    suspend fun getAll(): Response<ResponseData<List<Brand>>>

    @PUT("/api/admin/brand/update-status")
    suspend fun updateBrand(@Body brand: Brand): Response<ResponseData<Brand>>

    @DELETE("/api/admin/brand/delete")
    suspend fun deleteBrand(@Query("brandId") brandId:Int): Response<ResponseData<Boolean>>
}