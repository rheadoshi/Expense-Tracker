package com.example.expensetracker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import com.example.expensetracker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var pins: Array<String?> = arrayOf("", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPreference =  getSharedPreferences("USER", Context.MODE_PRIVATE)
        val ph = sharedPreference.getString("phone_number","-")
        if (ph=="-"){
            setCurrView(sign_up())
        }
        else{
            //Login
             pins = arrayOf<String?>(sharedPreference.getString("Pin1","0"),
                sharedPreference.getString("Pin2","0"),
                sharedPreference.getString("Pin3","0"),
                sharedPreference.getString("Pin4","0"))

            setCurrView(LoginPin())
        }

    }

    public fun setCurrView(fragment : Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction=fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container1,fragment)
        fragmentTransaction.commit()
    }

    public fun checkPin(p1:Array<String?>, p2:Array<String?> = pins):Boolean{
        for(i in p1.indices){
            Log.d("PINS", "p1 value"+p1[i]+" p2value"+p2[i])
            if(p1[i]!=p2[i])
                return false
        }
        return true
    }

    public fun addData(key:String, value:String?){
        val sharedPreference =  getSharedPreferences("USER", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putString(key,value)
        editor.commit()
    }

}