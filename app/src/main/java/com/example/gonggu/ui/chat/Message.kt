package com.example.gonggu.ui.chat

data class Message(
    var message: String?,
    var sendId: String?,
) {
    constructor():this("","")
}
