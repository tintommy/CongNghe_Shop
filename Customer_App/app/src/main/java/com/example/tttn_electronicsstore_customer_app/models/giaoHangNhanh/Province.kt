package com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh


import com.google.gson.annotations.SerializedName

data class Province(

    @SerializedName("ProvinceID")
    val provinceID: Int,
    @SerializedName("ProvinceName")
    val provinceName: String,

)