package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Order(
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("receiverName")
    val receiverName: String,
    @SerializedName("receiverPhone")
    val receiverPhone: String,
    @SerializedName("receiverAddress")
    val receiverAddress: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("ship")
    val ship: Int,
    @SerializedName("cancelReason")
    val cancelReason: String?,
    @SerializedName("username")
    val username: String,
    @SerializedName("onlinePay")
    val onlinePay: Boolean,
    @SerializedName("shippedImage")
    val shippedImage: String?,
) : Serializable