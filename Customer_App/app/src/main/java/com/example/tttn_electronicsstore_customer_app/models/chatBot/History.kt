package com.example.tttn_electronicsstore_customer_app.models.chatBot

data class History(
    val username: String,
    val seen: Int,
    val time: String,
    val date:String
) {
    constructor() : this("", 0, "","")
}
