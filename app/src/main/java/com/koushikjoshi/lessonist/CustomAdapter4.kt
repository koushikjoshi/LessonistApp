package com.koushikjoshi.lessonist

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class CustomAdapter4(private val mList: List<ItemsViewModel4>) : RecyclerView.Adapter<CustomAdapter4.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_card_outline, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        Picasso.get().load(ItemsViewModel.imageURL).into(holder.imageView)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = ItemsViewModel.text

        holder.viewButton.setText("View Certificate")

        holder.viewButton.setOnClickListener {
            var intent: Intent = Intent(holder.itemView.context, CertificateGenActivity::class.java)
            intent.putExtra("courseName", ItemsViewModel.text.toString())
            holder.itemView.context.startActivity(intent)
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
        val viewButton: Button = itemView.findViewById(R.id.button)
    }
}