package com.example.gonggu.ui.login

data class UserData (
    var name: String,
    var email: String,
    var phonenumber: String,
    var uId: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
    ) {
    constructor(): this("","","","","",0.0,0.0)
}