package com.example.weatherapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.DatabaseHelper
import com.example.weatherapp.models.FinalForecastModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DbViewModel(application: Application) :AndroidViewModel(application){
    private val databaseHelper=DatabaseHelper(application)
    val dbLiveData=MutableLiveData<List<FinalForecastModel>>()

    fun getAllData(){
        viewModelScope.launch {
            val list=databaseHelper.getAllForecasts()
            withContext(Dispatchers.Main){
                dbLiveData.postValue(list)
            }
        }
    }
}