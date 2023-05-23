package com.example.gonggu.ui.post

data class PostData (
    val content: String,
    val latitude: Double,
    val location: String,
    val longitude: Double,
    val numOfPeople: Int,
    val price : Int,
    val title: String,
    val time: String,
    val writeruid: String,
    val imageUrl: String,
    val like: MutableList<String> = mutableListOf(),
    val postId: String,
    val pricePerPerson: Int
)
{
    constructor(): this("", 0.0, "", 0.0,
        0,0,"","","","", mutableListOf(),"", 0)
}