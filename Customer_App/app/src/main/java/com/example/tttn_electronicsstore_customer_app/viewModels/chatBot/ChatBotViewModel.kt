package com.example.tttn_electronicsstore_customer_app.viewModels.chatBot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.chatBotAPI.ChatBotAPI_Instance
import com.example.tttn_electronicsstore_customer_app.api.chatBotAPI.ChatBotApiService
import com.example.tttn_electronicsstore_customer_app.helper.Key
import com.example.tttn_electronicsstore_customer_app.models.chatBot.ChatRequest
import com.example.tttn_electronicsstore_customer_app.models.chatBot.ChatResponse
import com.example.tttn_electronicsstore_customer_app.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ConnectException

class ChatBotViewModel : ViewModel() {
    private lateinit var chatBotApiService: ChatBotApiService

    init {
        initApiService()
    }

    fun initApiService() {

        var retrofit = ChatBotAPI_Instance.getClient()
        chatBotApiService = retrofit.create(ChatBotApiService::class.java)
    }

    private val _getResponse = MutableSharedFlow<Resource<ChatResponse>>()
    val getResponse = _getResponse.asSharedFlow()
    fun getResponse(chatRequest: ChatRequest) {
        viewModelScope.launch {
            _getResponse.emit(Resource.Loading())
            try {
                val response = chatBotApiService.generateContent(Key.chatApiKey, chatRequest)
                if (response.isSuccessful) {
                    Log.e("chat","Success")
                    _getResponse.emit(Resource.Success(response.body()!!))
                } else {
                    Log.e("chat","Error1")
                    _getResponse.emit(Resource.Error("Đã xảy ra lỗi"))
                }
            } catch (e: ConnectException) {
                Log.e("chat","Error2")
                _getResponse.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }


}