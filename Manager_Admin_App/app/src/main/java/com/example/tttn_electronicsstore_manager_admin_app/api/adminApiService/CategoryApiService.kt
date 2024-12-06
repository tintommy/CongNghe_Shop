package com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface CategoryApiService {
    @POST("/api/admin/category/add")
    suspend fun addCategory(
        @Query("name") name: String,
        @Body idDetailList: List<Int>
    ): Response<ResponseData<Category>>

    @GET("/api/admin/category/get-all")
    suspend fun getAllCategory(): Response<ResponseData<List<Category>>>


    @PUT("/api/admin/category/update")
    suspend fun updateCategory(
        @Body category: Category,
        @Query("addIdDetailList") addList: List<Int>,
        @Query("deleteIdDetailList") deleteList: List<Int>,
    ): Response<ResponseData<Category>>


    @DELETE("/api/admin/category/delete")
    suspend fun deleteCategory(
        @Query("categoryId") categoryId:Int,
    ): Response<ResponseData<Boolean>>
}