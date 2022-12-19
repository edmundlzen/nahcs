package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import TimePickerFragment
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdding : AppCompatActivity() {
    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activity_adding)
    }

    fun onClickSelectTimeButton(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
        supportFragmentManager.setFragmentResultListener("datePicker", this) { key, bundle ->
            val date = bundle.getString("date")
            // Show timePickerFragment
            val timePickerFragment = TimePickerFragment()
            timePickerFragment.show(supportFragmentManager, "timePicker")
            supportFragmentManager.setFragmentResultListener("timePicker", this) { key, bundle ->
                val time = bundle.getString("time")
                val calendar = Calendar.getInstance()
                calendar.time =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(date!!) as Date
                if (time != null) {
                    calendar.set(
                        Calendar.HOUR,
                        if (time.split(":")[0].toInt() == 12) 0 else time.split(":")[0].toInt()
                    )

                    calendar.set(Calendar.MINUTE, time.split(":")[1].split(" ")[0].toInt())

                    calendar.set(
                        Calendar.AM_PM,
                        if (time.split(":")[1].split(" ")[1] == "AM") {
                            Calendar.AM
                        } else {
                            Calendar.PM
                        }
                    )
                }
                selectedDate = Date(calendar.timeInMillis)
                val btnDate: Button = findViewById(R.id.btnTime)
                btnDate.text =
                    SimpleDateFormat("EEEE, d MMMM hh:mm a", Locale.getDefault()).format(
                        selectedDate
                    )
            }
        }
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickConfirmButton(view: View) {
        val activityName = findViewById<TextView>(R.id.etActivityName).text.toString()
        if (selectedDate == null) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please select a date")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        if (activityName.isBlank()) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please enter an activity name")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        // Check if the selected date and time is in the future
        if (Date().before(selectedDate)) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please select a date and time in the past")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        val db = Firebase.firestore
        val auth = Firebase.auth
        val appointmentsRef = db.collection("activities")

        val activity = hashMapOf(
            "activityName" to activityName,
            "activityTime" to Timestamp(selectedDate!!),
            "user" to auth.currentUser!!.uid
        )

        appointmentsRef.add(activity).addOnSuccessListener {
            val customDialog = CustomDialog(this)
            customDialog.setText("Activity added successfully")
            customDialog.setCallback {
                val intent =
                    Intent(view.context, ActivityTracker::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
            customDialog.setCancellable(false)
            customDialog.show()
        }.addOnFailureListener {
            val customDialog = CustomDialog(this)
            customDialog.setText("Failed to add activity")
            customDialog.setCancellable(false)
            customDialog.show()
        }
    }
}