package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName

data class ProductDetail(
    @SerializedName("detailId")
    val detailId: Int,
    @SerializedName("detailName")
    val detailName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("value")
    val value: String
)