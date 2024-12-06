package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    var fullName: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("username")
    val username: String
)