package com.example.tttn_electronicsstore_customer_app.api.giaoHangNhanh_api

import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Province
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.TotalFee
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.TotalFeeRequest
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Ward
import com.example.tttn_electronicsstore_customer_app.util.GiaoHangNhanhResponseData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressApiService {
    @GET("master-data/province")
    suspend fun getAllProvince(): Response<GiaoHangNhanhResponseData<List<Province>>>

    @GET("master-data/district")
    suspend fun getAllDistrict(@Query("province_id") province_id: Int): Response<GiaoHangNhanhResponseData<List<District>>>

    @GET("master-data/ward")
    suspend fun getAllWard(@Query("district_id") district_id: Int): Response<GiaoHangNhanhResponseData<List<Ward>>>

    @GET("v2/shipping-order/fee")
    suspend fun getFee(
        @Query("coupon") coupon: Any?,
        @Query("from_district_id") fromDistrictId: Int,
        @Query("from_ward_code") fromWardCode: String,
        @Query("height") height: Int,
        @Query("insurance_value") insuranceValue: Int,
        @Query("length") length: Int,
        @Query("service_type_id") serviceId: Int,
        @Query("to_district_id") toDistrictId: Int,
        @Query("to_ward_code") toWardCode: String,
        @Query("weight") weight: Int,
        @Query("width") width: Int
    ): Response<GiaoHangNhanhResponseData<TotalFee>>

}