package com.example.weatherapp.api

import com.example.weatherapp.geocoder.GeoCoderResponse
import com.example.weatherapp.models.ForecastWeatherResponse
import com.example.weatherapp.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherServiceApi {

    //Weather Call
    @GET("data/2.5/weather")
    fun getWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") metric: String
    ): Call<WeatherResponse>

    //Forecast Call
    @GET("data/2.5/forecast")
    fun getForecastWeatherDetails(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("units") metric: String
    ): Call<ForecastWeatherResponse>

    //GeoCoder Call
    @GET("geo/1.0/direct")
    fun geoCodeLocation(
        @Query("q") lat: String?,
        @Query("limit") limit: String?,
        @Query("appid") apiKey: String?
    ): Call<GeoCoderResponse?>?

}