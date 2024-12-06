package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Image(
    @SerializedName("avatar")
    val avatar: Boolean,
    @SerializedName("id")
    val id: Int,
    @SerializedName("link")
    val link: String,
    @SerializedName("productId")
    val productId: Int
): Serializable