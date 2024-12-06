package com.example.tttn_electronicsstore_customer_app.request

data class ChangePassOTPRequest(
    val username:String,
    val password: String,
    val email: String,
    val otp:String
)
