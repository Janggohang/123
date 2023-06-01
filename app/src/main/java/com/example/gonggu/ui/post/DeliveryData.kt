package com.example.gonggu.ui.post

data class DeliveryData (
    var category: String,
    var content: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    var numOfPeople: Int,
    var price : Int,
    val pricePerPerson: Int,
    var title: String,
    var time: String,
    val writeruid: String,
    var imageUrl: String,
    val like: MutableList<String> = mutableListOf(),
    val postId: String,
    val joiner: MutableList<String> = mutableListOf()
)
{
    constructor(): this("","", "", 0.0, 0.0,0,0, 0,"","","","",
        mutableListOf(),"", mutableListOf()
    )
}
