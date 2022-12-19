package com.nachs.nutritionandhealthcaremanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*


class AppointmentsReportAdapter(
    private val appointments: ArrayList<ActiveAppointment>,
) :
    RecyclerView.Adapter<AppointmentsReportAdapter.ActiveAppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveAppointmentViewHolder {
        return ActiveAppointmentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.appointments_report_pdf_layout_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActiveAppointmentViewHolder, position: Int) {
        val currentItem = appointments[position]
        holder.username.text = currentItem.userName
        holder.nutritionistName.text = currentItem.nutritionistName
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
        holder.date.text = SimpleDateFormat("dd/MM/yyyy").format(calendar.time)
        holder.time.text = SimpleDateFormat("hh:mm a").format(calendar.time)
        if (position == 0) {
            val newLinearLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            newLinearLayoutParams.setMargins(0, 15, 0, 0)
            holder.linearLayoutAppointmentsReportRow.layoutParams = newLinearLayoutParams
        }
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    class ActiveAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username = itemView.findViewById<TextView>(R.id.tvUsername)
        val nutritionistName = itemView.findViewById<TextView>(R.id.tvNutritionistName)
        val date =
            itemView.findViewById<TextView>(R.id.tvDate)
        val time =
            itemView.findViewById<TextView>(R.id.tvTime)
        val linearLayoutAppointmentsReportRow =
            itemView.findViewById<LinearLayout>(R.id.llAppointmentsReportRow)
    }
}