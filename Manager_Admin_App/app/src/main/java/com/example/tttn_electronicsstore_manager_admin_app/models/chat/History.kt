package com.example.tttn_electronicsstore_manager_admin_app.models.chat

data class History(
    val username: String,
    val seen: Int,
    val time: String,
    val date: String
) {
    constructor() : this("", 0, "", "")
}
