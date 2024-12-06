package com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.request.AddImportReceiptRequest
import com.example.tttn_electronicsstore_manager_admin_app.util.ResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ImportReceiptService {
    @GET("/api/manager/import-receipt/detail")
    suspend fun getDetailById(@Query("receiptId") id: Int): Response<ResponseData<List<ImportReceiptDetail>>>

    @GET("/api/manager/import-receipt/receiptByDate")
    suspend fun getAllByDate(
        @Query("fromDate") fromDate: String,
        @Query("toDate") toDate: String
    ): Response<ResponseData<List<ImportReceipt>>>


}