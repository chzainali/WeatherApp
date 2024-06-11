package com.example.weatherapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ItemForecastBinding
import com.example.weatherapp.models.FinalForecastModel
import com.example.weatherapp.models.ForecastWeatherResponse

class ForecastWeatherRecyclerAdapter(
    private val context: Context, private val weatherDataList: List<FinalForecastModel>
) : RecyclerView.Adapter<ForecastWeatherRecyclerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemForecastBinding>(
            LayoutInflater.from(context), R.layout.item_forecast, parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(weatherDataList[position])
    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    inner class ViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(weatherItem: FinalForecastModel) {
            binding.apply {
                // Set values to the views using data binding
                textViewDay.text = weatherItem.day
                textViewStatus.text = weatherItem.status
                textViewTemp.text = weatherItem.temperature+"°C"
                textViewTempMin.text = weatherItem.minTemperature+ " min"
                textViewTempMax.text = weatherItem.maxTemperature+" max"
                textViewSunrise.text = weatherItem.sunrise
                textViewSunset.text = weatherItem.sunset
                textViewWind.text = weatherItem.wind
                textViewPressure.text = weatherItem.pressure
                textViewHumidity.text = weatherItem.humidity
                textViewClouds.text = weatherItem.cloud + "%"
            }
        }
    }

}
