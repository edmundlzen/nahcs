package com.nachs.nutritionandhealthcaremanagementsystem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import java.util.*
import kotlin.math.abs

class PostsAdapter(private val posts: ArrayList<Post>) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentItem = posts[position]
        holder.nutritionist.text = "Posted by ${currentItem.authorName}"
        holder.title.text = currentItem.title
        val postedDaysAgo = abs((currentItem.postedAt.time - Date().time) / 1000 / 60 / 60 / 24)
        holder.date.text = if (postedDaysAgo == 0L) {
            "Today"
        } else if (postedDaysAgo == 1L) {
            "Yesterday"
        } else {
            "$postedDaysAgo days ago"
        }
        holder.content.text = currentItem.content
        if (currentItem.authorProfilePicture == null) {
            holder.profilePicture.setImageResource(R.drawable.profile_pic_test)
        } else {
            holder.profilePicture.setImageBitmap(currentItem.authorProfilePicture)
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nutritionist = itemView.findViewById<TextView>(R.id.tvNutritionistName)
        val title = itemView.findViewById<TextView>(R.id.tvTitlePost)
        val date = itemView.findViewById<TextView>(R.id.tvDate)
        val content = itemView.findViewById<TextView>(R.id.tvContentPost)
        val profilePicture = itemView.findViewById<ShapeableImageView>(R.id.ivProfilePicture)
    }
}