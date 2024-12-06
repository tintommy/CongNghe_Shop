package com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh


import com.google.gson.annotations.SerializedName

data class TotalFee(

    @SerializedName("service_fee")
    val serviceFee: Int,
    @SerializedName("total")
    val total: Int
)