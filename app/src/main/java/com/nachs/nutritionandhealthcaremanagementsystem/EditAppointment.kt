package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import TimePickerFragment
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class EditAppointment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_booking)

        val arraySpinner: Array<String> =
            arrayOf("Mandy", "John", "Jane", "Mary", "Peter", "Paul", "Ringo", "George")
        val s: Spinner = findViewById(R.id.spnNutritionist)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, arraySpinner)
        s.adapter = adapter
    }

    fun onClickSelectDateButton(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
        supportFragmentManager.setFragmentResultListener("datePicker", this) { key, bundle ->
            val date = bundle.getString("date")
            val textView: TextView = findViewById(R.id.btnDate)
            textView.text = date
        }
    }

    fun onClickSelectTimeButton(view: View) {
        val timePickerFragment = TimePickerFragment()
        timePickerFragment.show(supportFragmentManager, "timePicker")
        supportFragmentManager.setFragmentResultListener("timePicker", this) { key, bundle ->
            val time = bundle.getString("time")
            val textView: TextView = findViewById(R.id.btnTime)
            textView.text = time
        }
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }
}