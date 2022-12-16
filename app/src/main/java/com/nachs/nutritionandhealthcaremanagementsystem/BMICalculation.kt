package com.nachs.nutritionandhealthcaremanagementsystem

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
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
        val advice = findViewById<TextView>(R.id.tvAdvice)
        result.setText(bmiRounded)
        when {
            bmi < 18.5 -> advice.setText(
                "You are underweight. You should talk to your doctor to determine" +
                        " the cause of your low body weight. Your doctor can help you create a plan to gain weight in a healthy way." +
                        " This may include making changes to your diet and exercise routine, as well as addressing any underlying" +
                        " medical conditions that may be contributing to your weight loss. It's important to gain weight in a healthy way," +
                        " so avoid crash diets and excessive caloric intake without proper nutrition. It's also important to talk to your" +
                        " doctor before starting any new exercise program."
            )
            bmi < 25 -> advice.setText(
                "You are normal. You should continue to eat a healthy diet and exercise regularly. You should" +
                        " also continue to see your doctor regularly to monitor your health and make sure you are maintaining a healthy weight."
            )
            bmi < 30 -> advice.setText(
                "You are overweight. You should talk to your doctor about your weight and discuss ways to lose weight" +
                        " in a healthy way. This may include making changes to your diet and exercise routine, as well as addressing any underlying" +
                        " medical conditions that may be contributing to your weight gain. It's important to lose weight in a healthy way, so" +
                        " avoid crash diets and excessive caloric intake without proper nutrition. It's also important to talk to your doctor before" +
                        " starting any new exercise program."
            )
            bmi < 35 -> advice.setText(
                "You are obese. You should talk to your doctor about your weight and discuss ways to lose weight in a" +
                        " healthy way. This may include incorporating more fruits, vegetables, and whole grains into your diet, and increasing your" +
                        " physical activity, as well as addressing any underlying medical conditions that may be contributing to your weight gain. It's" +
                        " important to lose weight in a healthy way, so avoid crash diets and excessive caloric intake without proper nutrition. It's also" +
                        " important to talk to your doctor before starting any new exercise program."
            )
            else -> advice.setText(
                "You are obese. it's important to talk to your doctor to determine the cause of your excess weight and to" +
                        " develop a plan to lose weight in a healthy way. Your doctor may recommend making changes to your diet and exercise routine," +
                        " as well as addressing any underlying medical conditions that may be contributing to your obesity. It's important to avoid crash diets" +
                        " and excessive caloric restriction, as these can be harmful to your health. Instead, try to make gradual, sustainable changes to" +
                        " your lifestyle that will help you lose weight in a healthy way. This may include incorporating more fruits, vegetables, and whole" +
                        " grains into your diet, and increasing your physical activity. Your doctor may also recommend weight loss medication or weight loss surgery," +
                        " if appropriate. Talk to your doctor before starting any new exercise program or weight loss treatment."
            )

        }
    }

    fun onClickResetButton(view: View) {
        val height = findViewById<EditText>(R.id.etHeight)
        val weight = findViewById<EditText>(R.id.etWeight)
        val bmi = findViewById<EditText>(R.id.etBMI)
        val advice = findViewById<TextView>(R.id.tvAdvice)
        height.setText("")
        weight.setText("")
        bmi.setText("")
        advice.setText("")

    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

}