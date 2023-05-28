package com.example.gonggu.ui.location

data class Location (
    val latitude: Double,
    val longitude: Double)
{
    constructor() : this (0.0, 0.0)
}
