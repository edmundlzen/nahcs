package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Generate a lot of fake rows
        val linearLayout = findViewById<LinearLayout>(R.id.llPosts)
        val nutritionists = arrayOf(
            "Sandy",
            "Tina",
            "Sally",
            "Sue",
            "Samantha",
            "Sara",
            "Sasha",
            "Sandra",
            "Quinn",
        )
        val titles = arrayOf(
            "How to eat healthy",
            "How to lose weight",
            "How to gain weight",
            "How to cure cancer",
            "How to cure diabetes",
            "How to cure heart disease",
            "How to cure obesity",
            "How to cure depression",
            "How to cure anxiety",
        )
        for (i in 0..8) {
            val view =
                layoutInflater.inflate(R.layout.activity_home, null, false)
            val nutritionist = view.findViewById<TextView>(R.id.tvNutritionistName1)
            val title = view.findViewById<TextView>(R.id.tvTitlePost1)
            val date = view.findViewById<TextView>(R.id.tvDate1)
            nutritionist.text = nutritionists[i]
            title.text = titles[i]
            date.text = "${i + 2} days ago"

            val post = view.findViewById<LinearLayout>(R.id.llContentPost1)
            (post.parent as LinearLayout).removeView(post)
            linearLayout.addView(post)
        }
    }

    fun onClickSettingsButton(view: View) {
        val userType =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE).getString("userType", "")
        val intent: Intent = if (userType == "nutritionist") {
            Intent(this, NutritionistSettings::class.java)
        } else {
            Intent(this, MemberSettings::class.java)
        }
        startActivity(intent)
    }

    fun onClickBMICalculatorButton(view: View) {
        val intent = Intent(this, BMICalculation::class.java)
        startActivity(intent)
    }
}