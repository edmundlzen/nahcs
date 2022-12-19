package com.nachs.nutritionandhealthcaremanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
        holder.deleteButton.setOnClickListener {
            val customDialog = CustomDialog(holder.deleteButton.context)
            customDialog.setText("Are you sure you want to delete this activity?")
            customDialog.setCallback {
                val progressBarDialog = ProgressBarDialog(holder.deleteButton.context)
                progressBarDialog.show()
                val db = Firebase.firestore
                val postsRef = db.collection("activities")
                postsRef.document(currentItem.id).delete()
                    .addOnSuccessListener {
                        activities.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, activities.size)
                        progressBarDialog.dismiss()
                    }
                    .addOnFailureListener {
                        progressBarDialog.dismiss()
                        val customDialog = CustomDialog(holder.deleteButton.context)
                        customDialog.setText("Something went wrong. Please try again later.")
                        customDialog.setCancellable(false)
                        customDialog.show()
                    }
            }
            customDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return activities.size
    }

    class ActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityName = itemView.findViewById<TextView>(R.id.tvActivityName)
        val activityDate = itemView.findViewById<TextView>(R.id.tvActivityDate)
        val activityTime = itemView.findViewById<TextView>(R.id.tvActivityTime)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.ibDelete)
    }
}