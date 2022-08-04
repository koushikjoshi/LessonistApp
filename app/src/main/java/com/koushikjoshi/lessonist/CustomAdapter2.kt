package com.koushikjoshi.lessonist

import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.delay as delay1


interface ClickListener {
    fun onPositionClicked(position: Int)
    fun onLongClicked(position: Int)
}


class CustomAdapter2(private val mList: List<ItemsViewModel2>) : RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {

    // create new views


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter2.ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_card_outline, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class

        Picasso.get().load(ItemsViewModel.imageURL).into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

        holder.courseButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, ViewCourseActivity::class.java)
            intent.putExtra("url", ItemsViewModel.url.toString())
            intent.putExtra("courseName", ItemsViewModel.text.toString())
            holder.itemView.context.startActivity(intent)
            var act = holder.itemView.context as Activity
        }

    }



    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView2)
        val textView: TextView = itemView.findViewById(R.id.textView2)
        val courseButton: Button = itemView.findViewById(R.id.button)
    }
}