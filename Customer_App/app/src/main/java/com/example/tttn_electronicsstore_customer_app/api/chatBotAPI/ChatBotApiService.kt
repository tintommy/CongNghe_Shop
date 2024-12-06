package com.example.tttn_electronicsstore_customer_app.api.chatBotAPI

import com.example.tttn_electronicsstore_customer_app.models.chatBot.ChatRequest
import com.example.tttn_electronicsstore_customer_app.models.chatBot.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatBotApiService {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun generateContent(
        @Query("key") key:String,
        @Body requestBody: ChatRequest
    ): Response<ChatResponse>
}