package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.Executors

class AppointmentsHistoryReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_appointments_report)

        val isNutritionist =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isNutritionist", false)

        findViewById<TextView>(R.id.tvActions).visibility = View.GONE
        findViewById<TextView>(R.id.tvTitle).text = "Appointments History"

        if (isNutritionist) {
            findViewById<TextView>(R.id.tvNutritionistName).text = "Patient"
        }

        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()

        val executor = Executors.newSingleThreadExecutor()
        executor.submit(Runnable {
            lifecycleScope.launch {
                val activeAppointments = getActiveAppointments()
                runOnUiThread {
                    val recyclerView: RecyclerView = findViewById(R.id.rvActiveAppointments)
                    recyclerView.adapter = ActiveAppointmentsAdapter(activeAppointments)
                    progressBarDialog.dismiss()
                }
            }
        })
    }

    private suspend fun getActiveAppointments(): ArrayList<ActiveAppointment> =
        withContext(Dispatchers.Default) {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val isNutritionist =
                applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                    .getBoolean("isNutritionist", false)
            val appointmentsRef = if (!isNutritionist) {
                db.collection("appointments")
                    .whereEqualTo("user", auth.currentUser!!.uid)
            } else {
                db.collection("appointments")
                    .whereEqualTo("nutritionist", auth.currentUser!!.uid)
            }
            val activeAppointments = ArrayList<ActiveAppointment>()
            val appointmentsData = appointmentsRef.get().await()
            for (appointment in appointmentsData) {
                // Check if the date and time is in the past
                val calendar = Calendar.getInstance()
                calendar.time = appointment.getDate("date")!!
                calendar.set(
                    Calendar.HOUR_OF_DAY,
                    appointment.getString("time")!!.split(":")[0].toInt()
                )
                val selectedDateTime = calendar.time
                if (!selectedDateTime.before(Date())) {
                    continue
                }
                val nutritionistId = appointment.getString("nutritionist")!!
                val nutritionist = db.collection("users").document(nutritionistId).get().await()
                val nutritionistName = nutritionist.getString("name")!!
                val userId = appointment.getString("user")!!
                val userName = db.collection("users").document(userId).get().await()
                    .getString("name")!!
                val date = appointment.getDate("date")!!
                val time = appointment.getString("time")!!
                val id = appointment.id
                activeAppointments.add(
                    ActiveAppointment(
                        id,
                        date,
                        time,
                        nutritionistName,
                        nutritionistId,
                        userName,
                        userId,
                        true
                    )
                )
            }

            return@withContext activeAppointments
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

    fun onClickBackButton(view: View) {
        onBackPressed()
    }
}