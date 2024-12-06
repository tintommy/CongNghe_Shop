package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName

data class OrderDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("orderId")
    val orderId: Int,
    @SerializedName("price")
    val price: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productImageList")
    val productImageList: List<ProductImage>,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("review_status")
    var reviewStatus: Boolean
)