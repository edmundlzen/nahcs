package com.nachs.nutritionandhealthcaremanagementsystem

import DatePickerFragment
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@RuntimePermissions
class ActiveAppointmentsReport : AppCompatActivity() {
    private var xmlToPDFLifecycleObserver: PdfGenerator.XmlToPDFLifecycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_appointments_report)
        val isNutritionist =
            applicationContext.getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("isNutritionist", false)

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
                    val activeAppointmentsAdapter = ActiveAppointmentsAdapter(
                        activeAppointments,
                        ::generateAppointmentConfirmationPDF
                    )
                    recyclerView.adapter = activeAppointmentsAdapter
                    progressBarDialog.dismiss()
                }
            }
        })

        xmlToPDFLifecycleObserver = PdfGenerator.XmlToPDFLifecycleObserver(this)
        lifecycle.addObserver(xmlToPDFLifecycleObserver!!)
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
                    Calendar.HOUR,
                    if (appointment.getString("time")!!
                            .split(":")[0].toInt() == 12
                    ) 0 else appointment.getString(
                        "time"
                    )!!.split(":")[0].toInt()
                )
                calendar.set(
                    Calendar.AM_PM,
                    if (appointment.getString("time")!!.split(" ")[1] == "AM") {
                        Calendar.AM
                    } else {
                        Calendar.PM
                    }
                )
                val selectedDateTime = calendar.time
                if (selectedDateTime.before(Date())) {
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
                        userId
                    )
                )
            }

            return@withContext activeAppointments
        }

    @NeedsPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun generateAppointmentConfirmationPDF(
        view: View,
        forName: String,
        withName: String,
        Date: Date
    ) {
        val appointmentConfirmationPDFView = layoutInflater.inflate(
            R.layout.appointment_confirmation_pdf_layout,
            null
        )
        appointmentConfirmationPDFView.findViewById<TextView>(R.id.tvForName).text = "For: $forName"
        appointmentConfirmationPDFView.findViewById<TextView>(R.id.tvWithName).text =
            "With: $withName"
        appointmentConfirmationPDFView.findViewById<TextView>(R.id.tvDate).text =
            "Date: ${SimpleDateFormat("h:mm a, MMMM dd yyyy").format(Date)}"
        PdfGenerator.getBuilder()
            .setContext(this)
            .fromViewSource()
            .fromView(appointmentConfirmationPDFView)
            // You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array . * /
            .setFileName(
                "Appointment Confirmation: With $withName, ${
                    SimpleDateFormat("h:mm a, MMMM dd yyyy").format(
                        Date
                    )
                }"
            )
            .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN) /*If you want to save your pdf in shared storage (where other apps can also see your pdf even after the app is uninstall).
			 * You need to pass an xmt to pdf lifecycle observer by the following method. To get complete overview please see the MainActivity of 'sample' folder */
            .savePDFSharedStorage(xmlToPDFLifecycleObserver) /* It true then the generated pdf will be shown after generated. */
            .build(object : PdfGeneratorListener() {
                override fun onFailure(failureResponse: FailureResponse) {
                    super.onFailure(failureResponse)
                    /* If pdf is not generated by an error then you will findout the reason behind it
				 * from this FailureResponse. */
                    Log.d("PDF", "onFailure: " + failureResponse.errorMessage)
                }

                override fun onStartPDFGeneration() {
                    /*When PDF generation begins to start*/
                    Log.d("PDF", "onStartPDFGeneration: ")
                }

                override fun onFinishPDFGeneration() {
                    /*When PDF generation is finished*/
                    Log.d("PDF", "onFinishPDFGeneration: ")
                    Toast.makeText(view.context, "PDF Generated", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun showLog(log: String) {
                    super.showLog(log)
                    /*It shows logs of events inside the pdf generation process*/
                }

                override fun onSuccess(response: SuccessResponse) {
                    super.onSuccess(response)
                    Toast.makeText(view.context, "PDF Generated", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    @OnNeverAskAgain(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun onWriteExternalStorageNeverAskAgain() {
        Toast.makeText(this, "Please allow storage permission in settings", Toast.LENGTH_SHORT)
            .show()
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