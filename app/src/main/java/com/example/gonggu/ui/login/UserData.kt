package com.example.gonggu.ui.login

data class UserData (
    var name: String,
    var email: String,
    var phonenumber: String,
    var uId: String
) {
    constructor(): this("","","","")
}