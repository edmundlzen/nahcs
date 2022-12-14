package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity


class CaloriesCalculation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_caloriescalculation)

        val genderSpinner: Array<String> = arrayOf("Male", "Female")
        val spnGender: Spinner = findViewById(R.id.spnGender)
        spnGender.onItemSelectedListener = listener
        val spnGenderAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genderSpinner)
        spnGender.adapter = spnGenderAdapter

        val activityLevelSpinner: Array<String> = arrayOf(
            "Little or no exercise",
            "Light (1-3 times per week)",
            "Moderate (4-5 times per week)",
            "Active (daily, or intense exercise 3-4 times per week)",
            "Very active (intense exercise 6-7 times per week)",
            "Extra active (very intense exercise daily)"
        )
        val spnActivityLevel: Spinner = findViewById(R.id.spnActivityLevel)
        spnActivityLevel.onItemSelectedListener = listener
        val spnActivityLevelAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, activityLevelSpinner)
        spnActivityLevel.adapter = spnActivityLevelAdapter
    }

    fun onClickCalculateButton(view: View) {
        val age = findViewById<EditText>(R.id.etAge).text.toString()
        val height = findViewById<EditText>(R.id.etHeight).text.toString()
        val weight = findViewById<EditText>(R.id.etWeight).text.toString()
        val gender = findViewById<Spinner>(R.id.spnGender).selectedItem.toString()
        val activityLevel = findViewById<Spinner>(R.id.spnActivityLevel).selectedItem.toString()
        if (age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please fill in all the fields")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        val bmr =
            (10 * weight.toDouble()) + (6.25 * height.toDouble()) - (5 * age.toDouble()) + (if (gender == "Male") 5 else -161)
        val bmrWithActivityLevel = when (activityLevel) {
            "Little or no exercise" -> bmr * 1.2
            "Light (1-3 times per week)" -> bmr * 1.375
            "Moderate (4-5 times per week)" -> bmr * 1.55
            "Active (daily, or intense exercise 3-4 times per week)" -> bmr * 1.65
            "Very active (intense exercise 6-7 times per week)" -> bmr * 1.725
            "Extra active (very intense exercise daily)" -> bmr * 1.9
            else -> 0.0
        }

        val etMaintainWeight = findViewById<EditText>(R.id.etMaintainWeight)
        val etMildWeightLoss = findViewById<EditText>(R.id.etMildWeightLoss)
        val etModerateWeightLoss = findViewById<EditText>(R.id.etModerateWeightLoss)
        val etExtremeWeightLoss = findViewById<EditText>(R.id.etExtremeWeightLoss)

        etMaintainWeight.setText(bmrWithActivityLevel.toInt().toString())
        etMildWeightLoss.setText((bmrWithActivityLevel - 276).toInt().toString())
        etModerateWeightLoss.setText((bmrWithActivityLevel - 551).toInt().toString())
        etExtremeWeightLoss.setText((bmrWithActivityLevel - 1102).toInt().toString())

    }

    fun onClickResetButton(view: View) {
        val etAge = findViewById<EditText>(R.id.etAge)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val spnGender = findViewById<Spinner>(R.id.spnGender)
        val spnActivityLevel = findViewById<Spinner>(R.id.spnActivityLevel)
        val etMaintainWeight = findViewById<EditText>(R.id.etMaintainWeight)
        val etMildWeightLoss = findViewById<EditText>(R.id.etMildWeightLoss)
        val etModerateWeightLoss = findViewById<EditText>(R.id.etModerateWeightLoss)
        val etExtremeWeightLoss = findViewById<EditText>(R.id.etExtremeWeightLoss)

        etAge.setText("")
        etHeight.setText("")
        etWeight.setText("")
        spnGender.setSelection(0)
        spnActivityLevel.setSelection(0)
        etMaintainWeight.setText("")
        etMildWeightLoss.setText("")
        etModerateWeightLoss.setText("")
        etExtremeWeightLoss.setText("")
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    private val listener: AdapterView.OnItemSelectedListener =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                (parent.getChildAt(0) as TextView).setTextColor(resources.getColor(R.color.white))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

}