package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName

data class Detail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)