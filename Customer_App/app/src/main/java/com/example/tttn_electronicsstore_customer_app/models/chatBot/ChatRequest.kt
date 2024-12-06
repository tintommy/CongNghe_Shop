package com.example.tttn_electronicsstore_customer_app.models.chatBot


import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("contents")
    val contents: MutableList<Content>
)

data class Content(
    @SerializedName("parts")
    var parts: MutableList<Part>,

    )
data class Part(
    @SerializedName("text")
    val text: String
)