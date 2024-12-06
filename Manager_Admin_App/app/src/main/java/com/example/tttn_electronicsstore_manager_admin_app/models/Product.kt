package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("bought")
    val bought: Int,
    @SerializedName("brandId")
    val brandId: Int,
    @SerializedName("brandName")
    val brandName: String,
    @SerializedName("categoryId")
    val categoryId: Int,
    @SerializedName("categoryName")
    val categoryName: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("imageList")
    val imageList: List<Image>,
    @SerializedName("priceHistoryList")
    val priceHistoryList: List<PriceHistory>,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("productDetailList")
    val productDetailList: List<ProductDetail>,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("reviewList")
    val reviewList: List<Review>,
    @SerializedName("reviewValue")
    val reviewValue: Double,
    @SerializedName("status")
    var status: Boolean,

):Serializable