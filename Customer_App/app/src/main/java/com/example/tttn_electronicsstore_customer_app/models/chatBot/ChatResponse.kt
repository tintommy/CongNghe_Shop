package com.example.tttn_electronicsstore_customer_app.models.chatBot


import com.google.gson.annotations.SerializedName

data class ChatResponse(
    @SerializedName("candidates")
    val candidates: MutableList<Candidate>,

)