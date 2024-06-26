package com.example.expensetracker

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.expensetracker.databinding.ActivityHomePageBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private val name = "USER"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home_page)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_income, R.id.navigation_dashboard, R.id.navigation_expenses
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    public fun listView(key:String):String?{
        val sharedPreferences = getSharedPreferences(name, MODE_PRIVATE)
        val json = sharedPreferences.getString(key, null)
        var editor = sharedPreferences.edit()
        editor.remove(key)
        editor.commit()
        return json
    }

    public fun addLstView(key:String,json:String?){
        val sharedPreferences = getSharedPreferences(name, MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.putString(key,json)
        editor.commit()
    }
}