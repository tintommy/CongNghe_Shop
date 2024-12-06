package com.example.tttn_electronicsstore_customer_app.request


import java.io.Serializable

data class SignUpRequest(
    val username:String,
    val fullName: String,
    val password: String,
    val email: String
):Serializable