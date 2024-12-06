package com.example.tttn_electronicsstore_customer_app.util

import com.google.gson.annotations.SerializedName

data class GiaoHangNhanhResponseData<T>(

    @SerializedName("data")
    val data: T,
    @SerializedName("message")
    val desc: String?,
    @SerializedName("code")
    val status: Int?,


    )