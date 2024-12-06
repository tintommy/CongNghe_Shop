package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReceiveAddress(
    @SerializedName("addressName")
    val addressName: String,
    @SerializedName("default")
    val default: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("receiverAddress")
    val receiverAddress: String,
    @SerializedName("receiverName")
    val receiverName: String,
    @SerializedName("receiverPhone")
    val receiverPhone: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("addressCode")
    val addressCode: String
):Serializable