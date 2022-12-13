package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class ActiveAppointmentsAdapter(private val activeAppointments: ArrayList<ActiveAppointment>) :
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
        holder.activeAppointmentDeleteButton.setOnClickListener {
            val customDialog = CustomDialog(it.context)
            customDialog.setText("Are you sure you want to delete this appointment?")
            customDialog.setCallback {
                val progressBarDialog = ProgressBarDialog(it.context)
                progressBarDialog.show()

                val db = Firebase.firestore
                db.collection("appointments").document(currentItem.id).delete()
                    .addOnSuccessListener {
                        activeAppointments.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, activeAppointments.size)
                        progressBarDialog.dismiss()
                    }
            }
            customDialog.show()
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
        val activeAppointmentDeleteButton =
            itemView.findViewById<ImageButton>(R.id.ibCancelAppointment)
    }
}