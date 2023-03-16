package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityResultBinding
import com.google.android.material.snackbar.Snackbar

class ResultActivity : AppCompatActivity() {
    private val binding: ActivityResultBinding by lazy {
        ActivityResultBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        val height = (intent.getStringExtra("height")?.toDouble() ?: 0.0) / 100
        val weight = intent.getStringExtra("weight")?.toDouble() ?: 0.0

        val bmiResult = weight / (height * height)

        Toast.makeText(this,"height:$height/weight:$weight => $bmiResult",Toast.LENGTH_LONG).show()

        val sharedPref = getSharedPreferences("com.example.myapplication.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        with(sharedPref.edit()){
            putString("height",(height * 100).toString())
            putString("weight",weight.toString())
            apply()
        }




        when (bmiResult) {
            !in 0.0..24.9 -> {
                binding.apply {
                    resTextView.text = BmiResult.FAT.res
                    bgColorView.setBackgroundColor(Color.parseColor(BmiResult.FAT.bgColor))
                    emojiTextView.text = BmiResult.FAT.emoji
                }
            }

            in 23.0..24.9 -> {
                binding.apply {
                    resTextView.text = BmiResult.OVERWEIGHT.res
                    bgColorView.setBackgroundColor(Color.parseColor(BmiResult.OVERWEIGHT.bgColor))
                    emojiTextView.text = BmiResult.OVERWEIGHT.emoji
                }

            }

            in 18.6..22.9 -> {
                binding.apply {
                    resTextView.text = BmiResult.NORMAL.res
                    bgColorView.setBackgroundColor(Color.parseColor(BmiResult.NORMAL.bgColor))
                    emojiTextView.text = BmiResult.NORMAL.emoji
                }
            }

            in 0.0..18.5 -> {

                binding.apply {
                    resTextView.text = BmiResult.UNDERWEIGHT.res
                    bgColorView.setBackgroundColor(Color.parseColor(BmiResult.UNDERWEIGHT.bgColor))
                    emojiTextView.text = BmiResult.UNDERWEIGHT.emoji
                }
            }

        }



    }

}























