package com.example.tttn_electronicsstore_manager_admin_app.util


import com.google.gson.annotations.SerializedName

data class ResponseData<T>(
    @SerializedName("data")
    val data: T,
    @SerializedName("desc")
    val desc: String?,
    @SerializedName("status")
    val status: Int?,
    @SerializedName("success")
    val success: Boolean?
)