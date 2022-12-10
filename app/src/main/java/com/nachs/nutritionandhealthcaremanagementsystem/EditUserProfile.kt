package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class EditUserProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val arraySpinner: Array<String> = arrayOf("Male", "Female")
        val s: Spinner = findViewById(R.id.spnGender)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
        s.adapter = adapter

        val bundle = intent.extras
        val name = bundle?.getString("name")
        val email = bundle?.getString("email")
        val phone = bundle?.getString("phone")
        val birthDate = bundle?.getString("birthDate")
        val address = bundle?.getString("address")
        val gender = bundle?.getString("gender")

        findViewById<EditText>(R.id.etName).setText(name)
        findViewById<EditText>(R.id.etEmail).setText(email)
        findViewById<EditText>(R.id.etNumber).setText(phone)
        findViewById<EditText>(R.id.etAddress).setText(address)
        findViewById<Button>(R.id.btnBirthDate).text = birthDate
        findViewById<Spinner>(R.id.spnGender).setSelection(if (gender == "Male") 0 else 1)
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
        val builder = CustomDialog(this)
        builder.setText("Congratulations on successfully creating an account.")
        builder.setCancellable(false)
        builder.setCallback {
            applicationContext.getSharedPreferences("prefs", 0).edit()
                .putBoolean("loggedIn", true).apply()
            val intent: Intent = Intent(this, Home::class.java)
            startActivity(intent)
        }
        builder.show()
    }

    fun onClickSaveButton(view: View) {
        val builder = CustomDialog(this)
        builder.setText("Your profile has been successfully updated.")
        builder.setCancellable(false)
        builder.setCallback {
            onBackPressed()
        }
        builder.show()
    }

    fun onClickCancelButton(view: View) {
        onBackPressed()
    }
}