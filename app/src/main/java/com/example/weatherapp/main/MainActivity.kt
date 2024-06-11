package com.example.weatherapp.main


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.weatherapp.R
import com.example.weatherapp.adapter.PagerAdapter
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var navHostFragment: NavHostFragment
    lateinit var navController: NavController
    lateinit var adapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.main)

        // Get the NavHostFragment and NavController
//        navHostFragment =
//            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)!!
//        navController = navHostFragment.navController

        // Set up MeowBottomNavigation
      //  setBottomNavigation()
        setAdapter()

    }

    private fun setAdapter(){
        adapter= PagerAdapter(this)
        binding.vpTabs.adapter=adapter
        TabLayoutMediator(binding.tabLayout,binding.vpTabs
        ) { tab, position ->
            when(position){
                0->{
                    tab.text="Weather"
                }
                1->{
                    tab.text="Forecast"
                }
                2->{
                    tab.text="History"
                }
            }
        }.attach()
    }

    // Set up MeowBottomNavigation
//    private fun setBottomNavigation() {
//
//        // Add navigation items to the bottom navigation bar
//        binding.bottomNav.bottomNavigation.add(
//            MeowBottomNavigation.Model(
//                1,
//                R.drawable.ic_baseline_home_24
//            )
//        )
//        binding.bottomNav.bottomNavigation.add(
//            MeowBottomNavigation.Model(
//                2,
//                R.drawable.baseline_widgets_24
//            )
//        )
//        binding.bottomNav.bottomNavigation.add(
//            MeowBottomNavigation.Model(
//                3,
//                R.drawable.info
//            )
//        )
//
//        // Navigate to HomeFragment initially
//        navController.navigate(R.id.homeFragment)
//        binding.bottomNav.bottomNavigation.show(1, true)
//
//        // Set click listener for bottom navigation items
//        binding.bottomNav.bottomNavigation.setOnClickMenuListener { model: MeowBottomNavigation.Model ->
//            when (model.id) {
//                1 -> navController.navigate(R.id.homeFragment)
//                2 -> navController.navigate(R.id.forecastFragment)
//                3 -> navController.navigate(R.id.infosFragment)
//            }
//        }
//
//        // Set click listeners for custom bottom navigation buttons
//        binding.bottomNav.home.setOnClickListener {
//            navController.navigate(R.id.homeFragment)
//            binding.bottomNav.bottomNavigation.show(1, true)
//        }
//        binding.bottomNav.forecast.setOnClickListener(View.OnClickListener {
//            binding.bottomNav.bottomNavigation.show(2, true)
//            navController.navigate(R.id.forecastFragment)
//        })
//        binding.bottomNav.infos.setOnClickListener {
//            binding.bottomNav.bottomNavigation.show(3, true)
//            navController.navigate(R.id.infosFragment)
//        }
//    }

}