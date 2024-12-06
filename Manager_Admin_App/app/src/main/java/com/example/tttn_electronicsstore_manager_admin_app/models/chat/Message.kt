package com.example.tttn_electronicsstore_manager_admin_app.models.chat

data class Message(
    val text:String,
    val customer:Int
){
    constructor() : this("",1 )
}
