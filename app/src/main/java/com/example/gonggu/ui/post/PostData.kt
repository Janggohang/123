package com.example.gonggu.ui.post

data class PostData (
    val content: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val numOfPeople: Int,
    val price : Int,
    val title: String,
    val time: String,
    val writeruid: String,
    val imageUrl: String,
    val like: MutableList<String> = mutableListOf(),
    val postId: String,
)
{
    constructor(): this("", "", 0.0, 0.0,0,0,"","","","", mutableListOf(),"")
}