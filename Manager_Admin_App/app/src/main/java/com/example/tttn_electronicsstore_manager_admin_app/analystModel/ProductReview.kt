package com.example.tttn_electronicsstore_manager_admin_app.analystModel


import com.example.tttn_electronicsstore_manager_admin_app.models.Image
import com.google.gson.annotations.SerializedName

data class ProductReview(
    @SerializedName("imageList")
    val imageList: List<Image>,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("review4")
    val review4: Int,
    @SerializedName("review5")
    val review5: Int,
    @SerializedName("sumReview")
    val sumReview: Int
)