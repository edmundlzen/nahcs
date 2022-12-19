package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gkemon.XMLtoPDF.PdfGenerator
import com.gkemon.XMLtoPDF.PdfGeneratorListener
import com.gkemon.XMLtoPDF.model.FailureResponse
import com.gkemon.XMLtoPDF.model.SuccessResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@RuntimePermissions
class NutritionistSettings : AppCompatActivity() {
    private var xmlToPDFLifecycleObserver: PdfGenerator.XmlToPDFLifecycleObserver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nutritionist_settings)

        val notificationsOn =
            applicationContext.getSharedPreferences("prefs", 0).getBoolean("notificationsOn", true)
        val switch: SwitchCompat = findViewById(R.id.switchNotifications)
        switch.isChecked = notificationsOn

        xmlToPDFLifecycleObserver = PdfGenerator.XmlToPDFLifecycleObserver(this)
        lifecycle.addObserver(xmlToPDFLifecycleObserver!!)
    }

    fun onClickNotifications(view: View) {
        val switch: SwitchCompat = findViewById(R.id.switchNotifications)
        switch.isChecked = !switch.isChecked
        applicationContext.getSharedPreferences("prefs", 0).edit()
            .putBoolean("notificationsOn", switch.isChecked).apply()
    }

    fun onClickBackButton(view: View) {
        onBackPressed()
    }

    fun onClickLogoutButton(view: View) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.signOut()
        view.context.getSharedPreferences("data", MODE_PRIVATE).edit().putStringSet(
            "notifiedAppointmentNotificationIds",
            mutableSetOf()
        ).clear().apply()
        val intent: Intent = Intent(this, UserTypeSelection::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    fun onClickProfileSettings(view: View) {
        val intent: Intent = Intent(this, UserProfile::class.java)
        startActivity(intent)
    }

    fun onClickAppointmentsHistory(view: View) {
        val intent: Intent = Intent(this, AppointmentsHistoryReport::class.java)
        startActivity(intent)
    }

    fun onClickActiveAppointments(view: View) {
        val intent: Intent = Intent(this, ActiveAppointmentsReport::class.java)
        startActivity(intent)
    }

    fun onClickTnc(view: View) {
        val builder: CustomDialog = CustomDialog(this)
        builder.setText("You have to agree to the terms and conditions to use this app.")
        builder.setCancellable(false)
        builder.show()
    }

    fun onClickAbout(view: View) {
        val builder: CustomDialog = CustomDialog(this)
        builder.setText("This app is developed by __ and __ for the health and wellness of the people.")
        builder.setCancellable(false)
        builder.show()
    }

    fun onClickCreatePost(view: View) {
        val intent: Intent = Intent(this, NutritionistContentPosting::class.java)
        startActivity(intent)
    }

    @NeedsPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    fun onClickGenerateAppointmentsReport(view: View) {
        val progressBarDialog = ProgressBarDialog(this)
        progressBarDialog.show()
        val appointmentConfirmationPDFView = layoutInflater.inflate(
            R.layout.appointments_report_pdf_layout,
            null
        )
        val executor = Executors.newSingleThreadExecutor()
        executor.submit(Runnable {
            lifecycleScope.launch {
                val appointments = getAppointments()
                runOnUiThread {
                    val recyclerView: RecyclerView =
                        appointmentConfirmationPDFView.findViewById(R.id.rvAppointments)
                    val activeAppointmentsAdapter = AppointmentsReportAdapter(
                        appointments
                    )
                    recyclerView.adapter = activeAppointmentsAdapter

                    PdfGenerator.getBuilder()
                        .setContext(this@NutritionistSettings)
                        .fromViewSource()
                        .fromView(appointmentConfirmationPDFView)
                        // You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array . * /
                        .setFileName(
                            "Appointments Report " + SimpleDateFormat(
                                "dd-MM-yyyy",
                                Locale.getDefault()
                            ).format(Date())
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
                                progressBarDialog.dismiss()
                            }

                            override fun showLog(log: String) {
                                super.showLog(log)
                                /*It shows logs of events inside the pdf generation process*/
                            }

                            override fun onSuccess(response: SuccessResponse) {
                                super.onSuccess(response)
                                Toast.makeText(view.context, "PDF Generated", Toast.LENGTH_SHORT)
                                    .show()
                                progressBarDialog.dismiss()
                            }
                        })
                }
            }
        })
    }

    private suspend fun getAppointments(): ArrayList<ActiveAppointment> =
        withContext(Dispatchers.Default) {
            val db = Firebase.firestore
            val auth = Firebase.auth
            val appointmentsRef = db.collection("appointments")
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
//                if (selectedDateTime.before(Date())) {
//                    continue
//                }
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

            // Sort the appointments by date and time (oldest to newest)
            activeAppointments.sortWith { o1, o2 ->
                val calendar1 = Calendar.getInstance()
                calendar1.time = o1.date
                calendar1.set(
                    Calendar.HOUR,
                    if (o1.time.split(":")[0].toInt() == 12) 0 else o1.time.split(":")[0].toInt()
                )
                calendar1.set(
                    Calendar.AM_PM,
                    if (o1.time.split(" ")[1] == "AM") {
                        Calendar.AM
                    } else {
                        Calendar.PM
                    }
                )
                val calendar2 = Calendar.getInstance()
                calendar2.time = o2.date
                calendar2.set(
                    Calendar.HOUR,
                    if (o2.time.split(":")[0].toInt() == 12) 0 else o2.time.split(":")[0].toInt()
                )
                calendar2.set(
                    Calendar.AM_PM,
                    if (o2.time.split(" ")[1] == "AM") {
                        Calendar.AM
                    } else {
                        Calendar.PM
                    }
                )
                calendar1.time.compareTo(calendar2.time)
            }

            // Reverse the list for the newest to oldest order
            return@withContext ArrayList(activeAppointments.reversed())
        }
}