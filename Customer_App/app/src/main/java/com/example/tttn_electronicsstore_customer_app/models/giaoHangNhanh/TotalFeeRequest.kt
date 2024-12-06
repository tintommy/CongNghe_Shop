package com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh


import com.google.gson.annotations.SerializedName

data class TotalFeeRequest(
    @SerializedName("coupon")
    val coupon: Any,
    @SerializedName("from_district_id")
    val fromDistrictId: Int,
    @SerializedName("from_ward_code")
    val fromWardCode: String,
    @SerializedName("height")
    val height: Int,
    @SerializedName("insurance_value")
    val insuranceValue: Int,
    @SerializedName("length")
    val length: Int,
    @SerializedName("service_type_id")
    val serviceTypeId: Int,
    @SerializedName("to_district_id")
    val toDistrictId: Int,
    @SerializedName("to_ward_code")
    val toWardCode: String,
    @SerializedName("weight")
    val weight: Int,
    @SerializedName("width")
    val width: Int
)