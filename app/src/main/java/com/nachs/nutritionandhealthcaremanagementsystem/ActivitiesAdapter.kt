package com.nachs.nutritionandhealthcaremanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ActivitiesAdapter(private val activities: ArrayList<ActivityTrackerActivity>) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.activity_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val currentItem = activities[position]
        holder.activityName.text = currentItem.activityName
        holder.activityDate.text =
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentItem.activityTime)
        holder.activityTime.text =
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(currentItem.activityTime)
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName = itemView.findViewById<TextView>(R.id.tvActivityName)
        val activityDate = itemView.findViewById<TextView>(R.id.tvActivityDate)
        val activityTime = itemView.findViewById<TextView>(R.id.tvActivityTime)
    }
}