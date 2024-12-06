package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PriceHistory(
    @SerializedName("createAt")
    val createAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("price")
    val price: Int
):Serializable