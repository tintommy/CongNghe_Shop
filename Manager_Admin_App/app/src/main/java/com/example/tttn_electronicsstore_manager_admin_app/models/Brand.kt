package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Brand(
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("status")
    var status: Boolean
):Serializable