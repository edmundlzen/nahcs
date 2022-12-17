package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class AppointmentEditing : AppCompatActivity() {
    private var selectedDate: Date? = null
    private var nutritionistToId: HashMap<String, String> = HashMap()
    private lateinit var appointmentId: String

    val bookingSlots = arrayOf(
        "8:00 AM",
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
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_appointment_editing)

        val isNutritionist =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isNutritionist", false)

        if (isNutritionist) {
//            // Disable all fields
//            findViewById<Spinner>(R.id.spnTime).isEnabled = false
//            findViewById<Spinner>(R.id.spnNutritionist).isEnabled = false
//            findViewById<Button>(R.id.btnDate).isEnabled = false
//            findViewById<Button>(R.id.btnPost).isEnabled = false
//            val customDialog = CustomDialog(this)
//            customDialog.setText("Nutritionists can only delete appointments.")
//            customDialog.setCallback {
            val spnTime: Spinner = findViewById(R.id.spnTime)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookingSlots)
            spnTime.adapter = adapter
            val progressBarDialog = ProgressBarDialog(this)
            progressBarDialog.show()
            val db = Firebase.firestore
            val nutritionistsRef = db.collection("users").whereEqualTo("isNutritionist", true)
            nutritionistsRef.get().addOnSuccessListener { documents ->
                val nutritionists = ArrayList<String>()
                for (document in documents) {
                    nutritionists.add(document.data["name"].toString())
                    nutritionistToId[document.data["name"].toString()] = document.id
                }

                val s: Spinner = findViewById(R.id.spnNutritionist)
                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        nutritionists
                    )
                s.adapter = adapter

                val bundle = intent.extras
                appointmentId = bundle?.getString("appointmentId")!!
                val nutritionistId = bundle.getString("nutritionistId")
                val nutritionistName = bundle.getString("nutritionistName")
                val date = bundle.getLong("date")
                val time = bundle.getString("time")
                findViewById<Spinner>(R.id.spnNutritionist).setSelection(
                    nutritionists.indexOf(
                        nutritionistName
                    )
                )
                findViewById<TextView>(R.id.btnDate).text =
                    SimpleDateFormat("dd/MM/yyyy").format(Date(date))
                findViewById<Spinner>(R.id.spnTime).setSelection(bookingSlots.indexOf(time))
                selectedDate = Date(date)
                progressBarDialog.dismiss()
//                }
            }
//            customDialog.setCancellable(false)
//            customDialog.show()
        } else {
            val spnTime: Spinner = findViewById(R.id.spnTime)
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bookingSlots)
            spnTime.adapter = adapter
            val progressBarDialog = ProgressBarDialog(this)
            progressBarDialog.show()
            val db = Firebase.firestore
            val nutritionistsRef = db.collection("users").whereEqualTo("isNutritionist", true)
            nutritionistsRef.get().addOnSuccessListener { documents ->
                val nutritionists = ArrayList<String>()
                for (document in documents) {
                    nutritionists.add(document.data["name"].toString())
                    nutritionistToId[document.data["name"].toString()] = document.id
                }

                val s: Spinner = findViewById(R.id.spnNutritionist)
                val adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        nutritionists
                    )
                s.adapter = adapter

                val bundle = intent.extras
                appointmentId = bundle?.getString("appointmentId")!!
                val nutritionistId = bundle.getString("nutritionistId")
                val nutritionistName = bundle.getString("nutritionistName")
                val date = bundle.getLong("date")
                val time = bundle.getString("time")
                findViewById<Spinner>(R.id.spnNutritionist).setSelection(
                    nutritionists.indexOf(
                        nutritionistName
                    )
                )
                findViewById<TextView>(R.id.btnDate).text =
                    SimpleDateFormat("dd/MM/yyyy").format(Date(date))
                findViewById<Spinner>(R.id.spnTime).setSelection(bookingSlots.indexOf(time))
                selectedDate = Date(date)
                progressBarDialog.dismiss()
            }
        }
    }

    fun onClickSelectDateButton(view: View) {
        val datePickerFragment = DatePickerFragment()
        datePickerFragment.show(supportFragmentManager, "datePicker")
        supportFragmentManager.setFragmentResultListener("datePicker", this) { key, bundle ->
            val date = bundle.getString("date")
            val textView: TextView = findViewById(R.id.btnDate)
            textView.text = date
            selectedDate = date?.let { SimpleDateFormat("dd/MM/yyyy").parse(it) }!!
        }
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickConfirmButton(view: View) {
        Log.d(
            "AppointmentBooking",
            "date: $selectedDate, time: ${findViewById<Spinner>(R.id.spnTime).selectedItem}, nutritionist: ${
                findViewById<Spinner>(R.id.spnNutritionist).selectedItem
            }"
        )

        if (selectedDate == null) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please select a date")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        // Check if the selected date and time is in the past
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate!!
        calendar.set(
            Calendar.HOUR,
            findViewById<Spinner>(R.id.spnTime).selectedItem.toString().split(":")[0].toInt()
        )
        calendar.set(
            Calendar.AM_PM,
            if (findViewById<Spinner>(R.id.spnTime).selectedItem.toString().split(" ")[1] == "AM") {
                Calendar.AM
            } else {
                Calendar.PM
            }
        )
        val selectedDateTime = calendar.time
        if (selectedDateTime.before(Date())) {
            val customDialog = CustomDialog(this)
            customDialog.setText("Please select a date and time in the future")
            customDialog.setCancellable(false)
            customDialog.show()
            return
        }

        val db = Firebase.firestore
        val auth = Firebase.auth
        val appointmentsRef = db.collection("appointments")
        // Check if there is any appointment at the same date and time with the same nutritionist
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()
        appointmentsRef
            .whereEqualTo(
                "nutritionist",
                nutritionistToId[findViewById<Spinner>(R.id.spnNutritionist).selectedItem.toString()]
            )
            .whereEqualTo("date", selectedDate)
            .whereEqualTo("time", findViewById<Spinner>(R.id.spnTime).selectedItem.toString())
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // If there is no appointment at the same date and time with the same nutritionist, create a new appointment
                    val appointment = hashMapOf(
                        "date" to selectedDate,
                        "time" to findViewById<Spinner>(R.id.spnTime).selectedItem,
                        "nutritionist" to nutritionistToId[findViewById<Spinner>(R.id.spnNutritionist).selectedItem],
                        "user" to auth.currentUser?.uid
                    )
                    appointmentsRef.add(appointment).addOnSuccessListener {
                        progressBarDialog.dismiss()
                        val customDialog = CustomDialog(this)
                        customDialog.setText("Appointment booked successfully!")
                        customDialog.setCancellable(false)
                        customDialog.setCallback {
                            val intent = Intent(this, ActiveAppointmentsReport::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                        }
                        customDialog.show()
                    }
                } else {
                    progressBarDialog.dismiss()
                    val customDialog = CustomDialog(this)
                    customDialog.setText("An appointment already exists at the same time for this nutritionist, please select another time slot")
                    customDialog.setCancellable(false)
                    customDialog.show()
                }
            }
    }

    fun onClickDelete(view: View) {
        val customDialog = CustomDialog(view.context)
        customDialog.setText("Are you sure you want to delete this appointment?")
        customDialog.setCallback {
            val progressBarDialog = ProgressBarDialog(view.context)
            progressBarDialog.show()

            val db = Firebase.firestore
            db.collection("appointments").document(appointmentId).delete()
                .addOnSuccessListener {
                    val executor = Executors.newSingleThreadExecutor()
                    executor.submit(Runnable {
                        lifecycleScope.launch {
                            refreshAppointmentNotifications(applicationContext)
                            runOnUiThread {
                                val intent =
                                    Intent(view.context, ActiveAppointmentsReport::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                startActivity(intent)
                            }
                        }
                    })
                }
        }
        customDialog.setCancellable(false)
        customDialog.show()
    }

    fun onClickCancelButton(view: View) {
        onBackPressed()
    }
}