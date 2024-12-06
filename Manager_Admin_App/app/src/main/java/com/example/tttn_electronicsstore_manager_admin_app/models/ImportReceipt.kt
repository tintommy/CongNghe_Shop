package com.example.tttn_electronicsstore_manager_admin_app.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ImportReceipt (
    @SerializedName("id")
    val id:Int,
    @SerializedName("staffUsername")
    val staffUserName:String,
    @SerializedName("staffName")
    val staffName:String,
    @SerializedName("createdDate")
    val createdDate:String,
    @SerializedName("total")
    val total:Long
):Serializable