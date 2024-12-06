package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Review(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("userAvatar")
    val userAvatar: String,
    @SerializedName("userFullName")
    val userFullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("value")
    val value: Int
):Serializable