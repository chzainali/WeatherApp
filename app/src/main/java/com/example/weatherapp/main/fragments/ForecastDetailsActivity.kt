package com.example.weatherapp.main.fragments

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DatabaseHelper
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ForecastWeatherRecyclerAdapter
import com.example.weatherapp.databinding.FragmentForecastDetailsActivityBinding
import com.example.weatherapp.models.FinalForecastModel
import com.example.weatherapp.viewModel.DbViewModel
import com.example.weatherapp.viewModel.HomeViewModel


class ForecastDetailsActivity : AppCompatActivity() {
    lateinit var binding:FragmentForecastDetailsActivityBinding
    private lateinit var finalForecastModel: FinalForecastModel
    private var databaseHelper:DatabaseHelper?=null

    private var adapter:ForecastWeatherRecyclerAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentForecastDetailsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root )
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)
        databaseHelper= DatabaseHelper(this)

        finalForecastModel = intent.getSerializableExtra("data") as FinalForecastModel
        val list=databaseHelper?.getAllEntriesWithDate(finalForecastModel.time.toString())
        adapter= ForecastWeatherRecyclerAdapter(this,list!!)
        binding.run {
            textViewAddress.text=finalForecastModel.city.toString()
            rvForeCast.layoutManager=LinearLayoutManager(this@ForecastDetailsActivity)
            rvForeCast.adapter=adapter
        }
    }

}