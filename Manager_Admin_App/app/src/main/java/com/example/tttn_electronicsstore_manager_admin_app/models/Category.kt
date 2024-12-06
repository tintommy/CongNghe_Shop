package com.example.tttn_electronicsstore_manager_admin_app.models


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Category(
    @SerializedName("details")
    val details: List<Detail>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("status")
    val status: Boolean
) : Serializable {

    override fun toString(): String {
        return name
    }
}