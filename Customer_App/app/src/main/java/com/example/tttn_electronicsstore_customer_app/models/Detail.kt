package com.example.tttn_electronicsstore_customer_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Detail(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
): Serializable