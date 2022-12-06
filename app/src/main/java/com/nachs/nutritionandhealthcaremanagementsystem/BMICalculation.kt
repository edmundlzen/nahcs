package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BMICalculation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmicalculation)
    }

    fun onClickCalculateButton(view: View) {
        val height = findViewById<EditText>(R.id.etHeight).text.toString()
        val weight = findViewById<EditText>(R.id.etWeight).text.toString()
        if (height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_LONG).show()
            return
        }
        val bmi = (weight.toDouble() / (height.toDouble() * height.toDouble())) * 10000
        val bmiRounded = String.format("%.1f", bmi)
        val result = findViewById<EditText>(R.id.etBMI)
        result.setText(bmiRounded)
    }

    fun onClickResetButton(view: View) {
        val height = findViewById<EditText>(R.id.etHeight)
        val weight = findViewById<EditText>(R.id.etWeight)
        val bmi = findViewById<EditText>(R.id.etBMI)
        height.setText("")
        weight.setText("")
        bmi.setText("")
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

}