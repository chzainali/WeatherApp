package com.example.weatherapp.main.fragments

// Import necessary Android and third-party libraries
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.DatabaseHelper
import com.example.weatherapp.R
import com.example.weatherapp.adapter.ForecastWeatherRecyclerAdapter
import com.example.weatherapp.api.WeatherServiceApi
import com.example.weatherapp.databinding.FragmentForecastBinding
import com.example.weatherapp.geocoder.GeoCoderResponse
import com.example.weatherapp.models.FinalForecastModel
import com.example.weatherapp.models.ForecastWeatherResponse
import com.example.weatherapp.utils.Constants
import com.example.weatherapp.viewModel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ForecastFragment : Fragment() {
    lateinit var binding: FragmentForecastBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var progressDialog: ProgressDialog
    private lateinit var forecastWeatherRecyclerAdapter: ForecastWeatherRecyclerAdapter
    private var isFirstTime = false
    private var fromSearch=false
    private lateinit var homeViewModel: HomeViewModel
    var addedDays: ArrayList<String> = ArrayList()
    var forecastList: ArrayList<FinalForecastModel> = ArrayList()

    private var databaseHelper:DatabaseHelper?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize progress dialog, view model, and set up the recycler view adapter
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        setAdapter()
        databaseHelper=DatabaseHelper(requireContext())
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Observe Forecast response from ViewModel and update UI accordingly
        homeViewModel.forecastWeatherResponse.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {
                progressDialog.dismiss()
                updateUIWithWeatherDetails(it)
                if (fromSearch){
                    val date=getCurrentDateTime()
                    forecastList.onEach {
                        val forecastModel=it.copy()
                        databaseHelper?.addForecast(forecastModel)
                    }
                    fromSearch=false
                }
            }
        }

        // Observe GeoCoder response from ViewModel
        homeViewModel.geoCoderResponse.observe(viewLifecycleOwner) { geoCoderResponse ->
            geoCoderResponse?.let {
                // Update UI with GeoCoder details
                val location = it[0]?.name ?: ""
                binding.textViewAddress.text = location
                // Get forecast weather details based on the obtained location
                homeViewModel.getForecastWeatherDetailsByLocation(it[0]?.lat!!, it[0]?.lon!!,
                    { progressDialog.show() }, { progressDialog.hide() })
            }
        }

        // Request location permissions using Dexter library
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // Get user location if permission is granted
                    requestLocationData()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    // Show a rationale message to the user if needed
                    token.continuePermissionRequest()
                }
            })
            .check()

        // Set up a listener for the search view
        binding.etSearch.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Check if query is not empty
                if (query?.isNotEmpty() == true) {
                    // Call the API using the city name
                    fromSearch=true
                    homeViewModel.getWeatherDetailsByCityName(query.toString(),
                        { progressDialog.show() }, { progressDialog.hide() })
                } else {
                    // Call the API using the device's location
                    fromSearch=false
                    requestLocationData()
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Return true to indicate that the query has been handled
                return true
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun updateUIWithWeatherDetails(weather: ForecastWeatherResponse) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        addedDays.clear()
        forecastList.clear()
        binding.textViewAddress.text = weather.city.name

        for (weatherData in weather.list) {
            val day: String = getDate(weatherData.dt_txt)!!
            if (!addedDays.contains(day)) {
                addedDays.add(day)
                forecastList.add(
                    FinalForecastModel(
                        weather.city.name.toString(),
                        getDate(weatherData.dt_txt)!!,
                        weatherData.weather[0].description,
                        weatherData.main.temp.toString(),
                        weatherData.main.temp_min.toString(),
                        weatherData.main.temp_max.toString(),
                        convertTime(weather.city.sunrise),
                        convertTime(weather.city.sunset),
                        weatherData.wind.speed.toString(),
                        weatherData.main.pressure.toString(),
                        weatherData.main.humidity.toString(),
                        weatherData.clouds.all.toString(),
                        time = getCurrentDateTime()
                    )
                )
            }
        }
        forecastWeatherRecyclerAdapter.notifyDataSetChanged()
    }
    private fun getCurrentDateTime(): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss aa", Locale.getDefault())
        return sdf.format(Date())
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        progressDialog.show()
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()
        mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isFirstTime){
                    isFirstTime = true
                    // Get forecast weather details based on the current location
                    homeViewModel.getForecastWeatherDetailsByLocation(
                        locationResult.lastLocation?.latitude!!,
                        locationResult.lastLocation?.longitude!!,
                        { progressDialog.show() }, { progressDialog.hide() })
                }
            }
        }, Looper.myLooper())
    }

    private fun convertTime(time: Long): String {
        val date = Date(time * 1000L)
        val timeFormatted = SimpleDateFormat("HH:mm", Locale.UK)
        timeFormatted.timeZone = TimeZone.getDefault()
        return timeFormatted.format(date)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun getDate(dateString: String): String? {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // Parse the input string
        val dateTime = LocalDateTime.parse(dateString, formatter)
        // Extract date and   hours separately
        return dateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    private fun setAdapter() {
        binding.rvForeCast.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        forecastWeatherRecyclerAdapter = ForecastWeatherRecyclerAdapter(requireActivity(), forecastList)
        binding.rvForeCast.adapter = forecastWeatherRecyclerAdapter
    }
}
