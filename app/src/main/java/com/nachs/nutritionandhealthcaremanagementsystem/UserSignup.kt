package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UserSignup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_signup)

        val arraySpinner: Array<String> = arrayOf("Male", "Female")
        val s: Spinner = findViewById(R.id.spnGender)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
        s.adapter = adapter
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickSelectDateButton(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
        supportFragmentManager.setFragmentResultListener("datePicker", this) { key, bundle ->
            val date = bundle.getString("date")
            val textView: TextView = findViewById(R.id.btnBirthDate)
            textView.text = date
        }
    }

    fun onClickBackToLoginButton(view: View) {
        onBackPressed()
    }

    fun onClickSignupButton(view: View) {
        applicationContext.getSharedPreferences("prefs", 0).edit()
            .putBoolean("loggedIn", true).apply()
        val intent: Intent = Intent(this, Home::class.java)
        startActivity(intent)
    }
}