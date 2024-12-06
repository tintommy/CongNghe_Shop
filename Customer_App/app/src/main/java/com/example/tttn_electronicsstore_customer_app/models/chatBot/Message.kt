package com.example.tttn_electronicsstore_customer_app.models.chatBot

data class Message(
    val text:String,
    val customer:Int
){
    constructor() : this("",1 )
}
