package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName

data class Brand(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("status")
    var status: Boolean
)