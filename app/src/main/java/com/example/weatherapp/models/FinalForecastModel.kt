package com.example.weatherapp.models

import java.io.Serializable

data class FinalForecastModel(
    val city:String,
    var day: String,
    val status: String,
    val temperature: String,
    val minTemperature: String,
    val maxTemperature: String,
    val sunrise: String,
    val sunset: String,
    val wind: String,
    val pressure: String,
    val humidity: String,
    val cloud: String,
    var id:Int?=null,
    var time:String?=null
):Serializable
