package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("content")
    val content: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("userAvatar")
    val userAvatar: String,
    @SerializedName("userFullName")
    val userFullName: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("value")
    val value: Int
)