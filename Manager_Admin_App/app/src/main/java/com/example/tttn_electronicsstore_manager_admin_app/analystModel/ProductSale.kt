package com.example.tttn_electronicsstore_manager_admin_app.analystModel


import com.example.tttn_electronicsstore_manager_admin_app.models.Image
import com.google.gson.annotations.SerializedName

data class ProductSale(
    @SerializedName("imageList")
    val imageList: List<Image>,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("sum")
    val sum: Int
)