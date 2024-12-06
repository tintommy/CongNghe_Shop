package com.example.tttn_electronicsstore_manager_admin_app.util


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("desc")
    val desc: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("token")
    val token: String,
    @SerializedName("username")
    val username: String
)