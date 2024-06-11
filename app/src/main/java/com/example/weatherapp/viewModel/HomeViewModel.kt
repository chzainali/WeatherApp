package com.example.weatherapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.api.WeatherRepository
import com.example.weatherapp.geocoder.GeoCoderResponse
import com.example.weatherapp.models.ForecastWeatherResponse
import com.example.weatherapp.models.WeatherResponse

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository = WeatherRepository(application)

    private val _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse

    private val _geoCoderResponse = MutableLiveData<GeoCoderResponse?>()
    val geoCoderResponse: LiveData<GeoCoderResponse?>
        get() = _geoCoderResponse

    private val _forecastWeatherResponse = MutableLiveData<ForecastWeatherResponse>()
    val forecastWeatherResponse: LiveData<ForecastWeatherResponse>
        get() = _forecastWeatherResponse

    fun getWeatherDetailsByLocation(latitude: Double, longitude: Double,showProgress: () -> Unit,
                                    hideProgress: () -> Unit ){
        repository.getWeatherDetailsByLocation(latitude, longitude,showProgress,hideProgress) { response ->
            _weatherResponse.postValue(response)
        }
    }

    fun getWeatherDetailsByCityName(cityName: String, showProgress: () -> Unit,
                                    hideProgress: () -> Unit) {
        repository.getGeoCoderLocation(cityName, showProgress, hideProgress) { response ->
            _geoCoderResponse.postValue(response)
        }
    }

    fun getForecastWeatherDetailsByLocation(latitude: Double, longitude: Double, showProgress: () -> Unit,
                                            hideProgress: () -> Unit) {
        repository.getForecastWeatherDetailsByLocation(
            latitude, longitude, showProgress, hideProgress
        ) { response ->
            _forecastWeatherResponse.postValue(response)
        }
    }
}
