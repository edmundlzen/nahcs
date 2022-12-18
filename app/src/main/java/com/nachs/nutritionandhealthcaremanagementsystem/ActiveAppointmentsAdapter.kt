package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KFunction4


class ActiveAppointmentsAdapter(
    private val activeAppointments: ArrayList<ActiveAppointment>,
    private val generateAppointmentConfirmationPDF: (KFunction4<View, String, String, Date, Unit>)?,
) :
    RecyclerView.Adapter<ActiveAppointmentsAdapter.ActiveAppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveAppointmentViewHolder {
        return ActiveAppointmentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.active_appointment_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActiveAppointmentViewHolder, position: Int) {
        val isNutritionist =
            holder.activeAppointmentNutritionistName.context.getSharedPreferences(
                "prefs",
                MODE_PRIVATE
            )
                .getBoolean("isNutritionist", false)
        val currentItem = activeAppointments[position]
        holder.activeAppointmentDate.text = SimpleDateFormat("MMM dd").format(currentItem.date)
        holder.activeAppointmentTime.text = currentItem.time
        holder.activeAppointmentNutritionistName.text = if (isNutritionist) {
            currentItem.userName
        } else {
            currentItem.nutritionistName
        }
        if (currentItem.isHistory) {
            holder.activeAppointmentEditButton.visibility = View.GONE
        }
        holder.activeAppointmentEditButton.setOnClickListener {
            val intent = Intent(
                holder.activeAppointmentNutritionistName.context,
                AppointmentEditing::class.java
            )
            intent.putExtra("appointmentId", currentItem.id)
            intent.putExtra("nutritionistId", currentItem.nutritionistId)
            intent.putExtra("nutritionistName", currentItem.nutritionistName)
            intent.putExtra("date", currentItem.date.time)
            intent.putExtra("time", currentItem.time)

            holder.activeAppointmentNutritionistName.context.startActivity(intent)
        }
//        if (isNutritionist) {
//            holder.printButton.visibility = View.GONE
//            return
//        }
        val calendar = Calendar.getInstance()
        calendar.time = currentItem.date
        calendar.set(
            Calendar.HOUR,
            if (currentItem.time.split(":")[0].toInt() == 12
            ) 0 else currentItem.time.split(":")[0].toInt()
        )
        calendar.set(
            Calendar.AM_PM,
            if (currentItem.time.split(" ")[1] == "AM") {
                Calendar.AM
            } else {
                Calendar.PM
            }
        )
        holder.printButton.setOnClickListener {
            if (generateAppointmentConfirmationPDF !== null) {
                val auth = Firebase.auth
                val db = Firebase.firestore
                db.collection("users").document(
                    if (isNutritionist) {
                        currentItem.userId
                    } else {
                        auth.currentUser!!.uid
                    }
                )
                    .get().addOnSuccessListener { document ->
                        if (document != null) {
                            val name = document.getString("name")
                            generateAppointmentConfirmationPDF!!(
                                it,
                                name!!,
                                currentItem.nutritionistName,
                                calendar.time
                            )
                        }
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return activeAppointments.size
    }

    class ActiveAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activeAppointmentDate = itemView.findViewById<TextView>(R.id.tvAppointmentDate)
        val activeAppointmentTime = itemView.findViewById<TextView>(R.id.tvAppointmentTime)
        val activeAppointmentNutritionistName =
            itemView.findViewById<TextView>(R.id.tvNutritionistName)
        val activeAppointmentEditButton =
            itemView.findViewById<ImageButton>(R.id.ibEditAppointment)
        val printButton = itemView.findViewById<ImageButton>(R.id.ibPrint)
    }
}