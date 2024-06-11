package com.example.weatherapp.main.fragments

// Import necessary Android and third-party libraries
import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.DatabaseHelper
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.models.FinalForecastModel
import com.example.weatherapp.models.WeatherResponse
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var progressDialog: ProgressDialog
    private lateinit var homeViewModel: HomeViewModel
    private var isFirstTime = false
    private var fromSearch:Boolean=false
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize progress dialog, view model, and location client
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle(getString(R.string.app_name))
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        databaseHelper= DatabaseHelper(requireContext())

        // Observe weather response from ViewModel and update UI accordingly
        homeViewModel.weatherResponse.observe(viewLifecycleOwner, Observer { weatherResponse ->
            weatherResponse?.let {
                Log.d("WeatherAppCheckResults", "getWeatherDetailsByLocation called with Success")
                progressDialog.dismiss()
                updateUIWithWeatherDetails(it)
                if (fromSearch){
                    saveToDb(it)
                    fromSearch=false
                }
            }
        })

        // Observe GeoCoder response from ViewModel and update UI with location details
        homeViewModel.geoCoderResponse.observe(viewLifecycleOwner, Observer { geoCoderResponse ->
            geoCoderResponse?.let {
                val location = it[0]?.name ?: ""
                binding.textViewAddress.text = location
                Log.e("aa", "WeatherAppCheckResults: ", )
                // Get weather details based on the obtained location
                homeViewModel.getWeatherDetailsByLocation(it[0]?.lat!!, it[0]?.lon!!,{progressDialog.show()},{progressDialog.dismiss()})
            }
        })

        // Request location permissions using Dexter library
        Dexter.withActivity(requireActivity())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
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
                // Handle query submission if needed
                if (query?.isNotEmpty() == true) {
                    // Call the API using the city name
                    fromSearch=true
                    homeViewModel.getWeatherDetailsByCityName(query.toString(),
                        { progressDialog.show() }, { progressDialog.hide() })

                } else {
                    // Call the API using the device's location
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

    // Request location updates using FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()
        locationRequest.numUpdates=1
        mFusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (!isFirstTime){
                    isFirstTime = true
                    Log.d("WeatherAppCheckResults", "getWeatherDetailsByLocation called with lat=${locationResult.lastLocation?.latitude!!}, lon=${locationResult.lastLocation?.longitude!!}")
                    // Get weather details based on the current location
                    homeViewModel.getWeatherDetailsByLocation(
                        locationResult.lastLocation?.latitude!!,
                        locationResult.lastLocation?.longitude!!,{progressDialog.show()},{progressDialog.dismiss()})

                }
            }
        }, Looper.getMainLooper())
    }

    // Update UI with weather details from the response
    @SuppressLint("SetTextI18n")
    private fun updateUIWithWeatherDetails(weatherResponse: WeatherResponse) {
        if (progressDialog.isShowing){
            progressDialog.dismiss()
        }
        binding.textViewSunset.text = convertTime(weatherResponse.sys.sunset.toLong())
        binding.textViewSunrise.text = convertTime(weatherResponse.sys.sunrise.toLong())
        binding.textViewStatus.text = weatherResponse.weather[0].description
        binding.textViewAddress.text = weatherResponse.name
        binding.textViewTempMax.text = weatherResponse.main.temp_max.toString() +" max"
        binding.textViewTempMin.text = weatherResponse.main.temp_min.toString() + " min"
        binding.textViewTemp.text = weatherResponse.main.temp.toString() +"°C"
        binding.textViewHumidity.text = weatherResponse.main.humidity.toString()
        binding.textViewPressure.text = weatherResponse.main.pressure.toString()
        binding.textViewWind.text = weatherResponse.wind.speed.toString()
        binding.textViewUpdatedAt.text = getCurrentDateTime()
        binding.textViewClouds.text = weatherResponse.clouds.all.toString() + "%"
    }

    // Convert Unix timestamp to formatted time string
    private fun convertTime(time: Long): String {
        val date = Date(time * 1000L)
        val timeFormatted = SimpleDateFormat("HH:mm", Locale.UK)
        timeFormatted.timeZone = TimeZone.getDefault()
        return timeFormatted.format(date)
    }

    // Get the current date and time in a formatted string
    private fun getCurrentDateTime(): String? {
        val sdf = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss aa", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun saveToDb(weatherResponse: WeatherResponse){
        val forecast=  FinalForecastModel(
            binding.textViewAddress.text.toString(),
            getCurrentDateTime()!!,
            weatherResponse.weather[0].description,
            weatherResponse.main.temp.toString(),
            weatherResponse.main.temp_min.toString(),
            weatherResponse.main.temp_max.toString(),
            convertTime(weatherResponse.sys.sunrise.toLong()),
            convertTime(weatherResponse.sys.sunset.toLong()),
            weatherResponse.wind.speed.toString(),
            weatherResponse.main.pressure.toString(),
            weatherResponse.main.humidity.toString(),
            weatherResponse.clouds.all.toString(),
            time = getCurrentDateTime()!!
        )
        databaseHelper?.addForecast(forecast)

    }
}