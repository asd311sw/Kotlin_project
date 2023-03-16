package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.viewbinding.ViewBinding
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object{
        const val FROM_MAINACTIVITY = 1010

    }

    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("com.example.myapplication.PREFERENCE_FILE_KEY",
            Context.MODE_PRIVATE)

        binding.weightEditText.setText(sharedPref.getString("weight",""))
        binding.heightEditText.setText(sharedPref.getString("height",""))


        binding.clickButton.setOnClickListener {
            val height = binding.heightEditText.text.toString()
            val weight = binding.weightEditText.text.toString()

            val intent = Intent(applicationContext, ResultActivity::class.java).apply {
                putExtra("height", height)
                putExtra("weight", weight)
            }



            startActivityForResult(intent, FROM_MAINACTIVITY)
        }
    }
}

















































