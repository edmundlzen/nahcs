package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import TimePickerFragment
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ActiveAppointmentsReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_appointments_report)

        // Generate a lot of fake rows
        val table = findViewById<View>(R.id.tlActiveAppointments) as TableLayout
        val days =
            arrayOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val times = arrayOf(
            "9:00 AM",
            "10:00 AM",
            "11:00 AM",
            "12:00 PM",
            "1:00 PM",
            "2:00 PM",
            "3:00 PM",
            "4:00 PM",
            "5:00 PM",
            "6:00 PM",
            "7:00 PM",
            "8:00 PM",
            "9:00 PM"
        )
        for (i in 0..20) {
            val view =
                layoutInflater.inflate(R.layout.activity_active_appointments_report, null, false)
            val day = view.findViewById<TextView>(R.id.tvDay1)
            val time = view.findViewById<TextView>(R.id.tvTime1)
            val patient = view.findViewById<TextView>(R.id.tvPatient1)
            day.text = days[i % 7]
            time.text = times[i % 13]
            patient.text = "Patient $i"

            val row = view.findViewById<TableRow>(R.id.tr1)
            (row.parent as TableLayout).removeView(row)
            table.addView(row)
        }
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