package com.nachs.nutritionandhealthcaremanagementsystem

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

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
        val postedDaysAgo =
            (Date().time - currentItem.postedAt.time) / 1000 / 60 / 60 / 24
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
        if (holder.profilePicture.context.getSharedPreferences("prefs", 0)
                .getBoolean("isNutritionist", false)
        ) {
            holder.editButton.setOnClickListener(View.OnClickListener {
                val intent =
                    Intent(holder.editButton.context, NutritionistContentEditing::class.java)
                intent.putExtra("postTitle", currentItem.title)
                intent.putExtra("postContent", currentItem.content)
                intent.putExtra("postId", currentItem.id)
                holder.editButton.context.startActivity(intent)
            })

            holder.deleteButton.setOnClickListener(View.OnClickListener {
                val customDialog = CustomDialog(holder.deleteButton.context)
                customDialog.setText("Are you sure you want to delete this post?")
                customDialog.setCallback {
                    val progressBarDialog = ProgressBarDialog(holder.deleteButton.context)
                    progressBarDialog.show()
                    val db = Firebase.firestore
                    val postsRef = db.collection("posts")
                    postsRef.document(currentItem.id).delete()
                        .addOnSuccessListener {
                            posts.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, posts.size)
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
            })
            holder.postActions.visibility = View.VISIBLE
            val auth = Firebase.auth
            if (currentItem.authorId != auth.currentUser?.uid) {
                holder.editButton.visibility = View.GONE
            }
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
        val editButton = itemView.findViewById<ImageButton>(R.id.btnEdit)
        val deleteButton = itemView.findViewById<ImageButton>(R.id.btnDelete)
        val postActions = itemView.findViewById<LinearLayout>(R.id.llPostActions)
    }
}