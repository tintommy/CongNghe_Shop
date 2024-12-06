package com.example.tttn_electronicsstore_manager_admin_app.models

import com.google.gson.annotations.SerializedName

data class ImportReceiptDetail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("importReceiptId")
    val importReceiptId: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("productImageList")
    val imageList: List<Image>,
    @SerializedName("quantity")
    var quantity: Int,
    @SerializedName("price")
    var price: Int


)
