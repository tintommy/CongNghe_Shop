package com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh


import com.google.gson.annotations.SerializedName

data class District(

    @SerializedName("DistrictID")
    val districtID: Int,
    @SerializedName("DistrictName")
    val districtName: String,

    @SerializedName("ProvinceID")
    val provinceID: Int,

    )