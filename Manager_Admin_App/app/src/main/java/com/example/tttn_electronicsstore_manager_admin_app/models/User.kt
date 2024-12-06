package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("active")
    var active: Boolean,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("username")
    val username: String
):Serializable