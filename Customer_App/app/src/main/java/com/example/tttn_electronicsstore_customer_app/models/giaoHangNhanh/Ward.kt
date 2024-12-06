package com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh


import com.google.gson.annotations.SerializedName

data class Ward(

    @SerializedName("DistrictID")
    val districtID: Int,
    @SerializedName("WardCode")
    val wardCode: String,
    @SerializedName("WardName")
    val wardName: String,

    )