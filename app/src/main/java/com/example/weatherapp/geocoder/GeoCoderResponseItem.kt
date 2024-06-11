package com.example.weatherapp.geocoder

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GeoCoderResponseItem : Serializable {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("local_names")
    @Expose
    var localNames: LocalNames? = null

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

    @SerializedName("lon")
    @Expose
    var lon: Double? = null

    @SerializedName("country")
    @Expose
    var country: String? = null

    @SerializedName("state")
    @Expose
    var state: String? = null

    companion object {
        private const val serialVersionUID = 3669756251720800470L
    }
}