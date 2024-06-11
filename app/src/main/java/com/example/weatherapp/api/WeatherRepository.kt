package com.example.weatherapp.api

import android.app.Application
import android.os.AsyncTask
import android.util.Log
import com.example.weatherapp.geocoder.GeoCoderResponse
import com.example.weatherapp.models.ForecastWeatherResponse
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(private val application: Application) {

    private val serviceApi: WeatherServiceApi = createWeatherServiceApi()

    private fun createWeatherServiceApi(): WeatherServiceApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherServiceApi::class.java)
    }

    fun getWeatherDetailsByLocation(
        latitude: Double, longitude: Double,showProgress: () -> Unit,
        hideProgress: () -> Unit ,callback: (WeatherResponse) -> Unit
   ) {
        // Show progress dialog
            showProgress()
        // Perform network operation in the background using AsyncTask
        GetWeatherDetailsAsyncTask(serviceApi) {
            hideProgress()
            callback.invoke(it)
        }.execute(latitude, longitude)
    }

    fun getForecastWeatherDetailsByLocation(
        latitude: Double, longitude: Double, showProgress: () -> Unit,
        hideProgress: () -> Unit, callback: (ForecastWeatherResponse) -> Unit
    ) {
        // Show progress dialog
        showProgress.invoke()
        // Perform network operation in the background using AsyncTask
        GetForeCastWeatherDetailsAsyncTask(serviceApi) {
            // Dismiss progress dialog
            hideProgress.invoke()

            // Pass the result to the callback
            callback.invoke(it)
        }.execute(latitude, longitude)
    }

    fun getGeoCoderLocation(cityName: String,
                            showProgress: () -> Unit,
                            hideProgress: () -> Unit,
                            callback: (GeoCoderResponse?) -> Unit) {
        // Perform network operation in the background using AsyncTask
        GetGeoCoderLocationAsyncTask(serviceApi) {
            // Dismiss progress dialog
            hideProgress.invoke()

            // Pass the result to the callback
            callback.invoke(it)
        }.execute(cityName)
    }

    // AsyncTask to get weather details by location
    class GetWeatherDetailsAsyncTask(
        private val serviceApi: WeatherServiceApi,
        private val callback: (WeatherResponse) -> Unit
    ) : AsyncTask<Double, Void, WeatherResponse>() {

        override fun doInBackground(vararg params: Double?): WeatherResponse? {
            val latitude = params[0]
            val longitude = params[1]
            val call = serviceApi.getWeatherDetails(
                latitude ?: 0.0, longitude ?: 0.0,
                Constants.APP_ID, Constants.METRIC_UNIT
            )

            return try {
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: WeatherResponse?) {
            super.onPostExecute(result)
            result?.let { callback.invoke(it) }
        }
    }

    // AsyncTask to get forecast weather details by location
    class GetForeCastWeatherDetailsAsyncTask(
        private val serviceApi: WeatherServiceApi,
        private val callback: (ForecastWeatherResponse) -> Unit
    ) : AsyncTask<Double, Void, ForecastWeatherResponse>() {

        override fun doInBackground(vararg params: Double?): ForecastWeatherResponse? {
            val latitude = params[0]
            val longitude = params[1]
            val call = serviceApi.getForecastWeatherDetails(
                latitude ?: 0.0, longitude ?: 0.0,
                Constants.APP_ID, Constants.METRIC_UNIT
            )

            return try {
                val response = call.execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    Log.w("aaa", "doInBackground: ${response.message()}" )
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun onPostExecute(result: ForecastWeatherResponse?) {
            super.onPostExecute(result)
            result?.let { callback.invoke(it) }
        }
    }

    // AsyncTask to get GeoCoder location details
    class GetGeoCoderLocationAsyncTask(
        private val serviceApi: WeatherServiceApi,
        private val callback: (GeoCoderResponse?) -> Unit
    ) : AsyncTask<String, Void, GeoCoderResponse?>() {

        override fun doInBackground(vararg params: String?): GeoCoderResponse? {
            val cityName = params[0]
            val call = serviceApi.geoCodeLocation(cityName, "1", Constants.APP_ID)

            return try {
                val response = call?.execute()
                if (response?.isSuccessful == true) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

        override fun onPostExecute(result: GeoCoderResponse?) {
            super.onPostExecute(result)
            callback.invoke(result)
        }
    }
}
