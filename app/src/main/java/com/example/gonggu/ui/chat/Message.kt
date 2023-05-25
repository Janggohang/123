package com.example.gonggu.ui.chat

data class Message(
    var message: String?,
    var sendId: String?,
    val time: String,
    val timestamp: Long
) {
    constructor():this("","","", 0)
}
