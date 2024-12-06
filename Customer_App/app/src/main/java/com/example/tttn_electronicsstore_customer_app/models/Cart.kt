package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName

data class Cart(
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageList")
    val imageList: List<Image>,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("productPrice")
    val productPrice: Int,
    @SerializedName("quantity")
    var quantity: Int,
    @SerializedName("productStatus")
    var productStatus:Boolean,
    @SerializedName("status")
    var status: Boolean,
    @SerializedName("username")
    val username: String
)