package com.koushikjoshi.lessonist

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CustomAdapter3(private val mList: List<ItemsViewModel3>) : RecyclerView.Adapter<CustomAdapter3.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.video_list_template, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(ItemsViewModel.image)
        holder.imageView.setTag(ItemsViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textViewTitle.text = ItemsViewModel.text
        holder.textViewSl.text = ItemsViewModel.slText

        holder.ytButton.setOnClickListener {
                var intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(ItemsViewModel.url.toString()))
                holder.itemView.context.startActivity(intent)

        }

        val db = Firebase.database
        var courseName = ItemsViewModel.courseName
        var email = ItemsViewModel.email
        var myref = db.getReference("users/"+email)
        var enrolled_ref = db.getReference("users/"+email+"/courses_enrolled")
        var completed_ref = db.getReference("users/"+email+"/courses_completed")

        holder.imageView.setOnClickListener{
            if(holder.imageView.tag == R.drawable.ic_baseline_check_circle_outline_24){
                holder.imageView.setImageResource(R.drawable.ic_baseline_panorama_fish_eye_24)
                holder.imageView.setTag(R.drawable.ic_baseline_panorama_fish_eye_24)
            myref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.child("courses_enrolled/"+courseName).exists()){
                        var num_watched = Math.toIntExact(snapshot.child("courses_enrolled/"+courseName+"/num_watched").value as Long)
                        myref.child("courses_enrolled/"+courseName+"/num_watched").setValue(num_watched-1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })



            }
            else{
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_circle_outline_24)
                holder.imageView.setTag(R.drawable.ic_baseline_check_circle_outline_24)


                isEnrolledInCourse(db, courseName, email, enrolled_ref, completed_ref, holder, ItemsViewModel)

            }
        }


    }

    private fun isEnrolledInCourse(
        db: FirebaseDatabase,
        courseName: String,
        email: String,
        enrolled_ref: DatabaseReference,
        completed_ref: DatabaseReference,
        holder: ViewHolder,
        ItemsViewModel: ItemsViewModel3
    ) {
        enrolled_ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(courseName).exists()){
                    var num_videos = Math.toIntExact(snapshot.child(courseName+"/num_videos").value as Long)
                    var num_watched = Math.toIntExact(snapshot.child(courseName+"/num_watched").value as Long)
                    Log.i("num_videos", num_videos.toString())
                    Log.i("num_watched", num_watched.toString())
                    enrolled_ref.child(courseName+"/num_watched").setValue(num_watched+1)
                    if(num_videos == num_watched+1){
                        enrolled_ref.child(courseName+"/num_watched").setValue(num_watched+1)
                        Toast.makeText(holder.itemView.context, "You have completed this course! You can now download your certificate", Toast.LENGTH_SHORT).show()
                        var image = snapshot.child(courseName+"/image").getValue(String::class.java)

                        var url = snapshot.child(courseName+"/url").getValue(String::class.java)

                        var points = mutableMapOf(
                            "image" to image.toString(),
                            "num_videos" to num_videos?.toInt(),
                            "num_watched" to num_watched?.toInt(),
                            "url" to url.toString()
                        )
                        var data = mutableMapOf(
                            courseName to points
                        )

                        completed_ref.updateChildren(data as Map<String, Any>)

                        enrolled_ref.child(courseName).removeValue()

                    }


                }
                else{
                    hasFinishedCourse(db, courseName, email, enrolled_ref, completed_ref, holder, ItemsViewModel)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun hasFinishedCourse(
        db: FirebaseDatabase,
        courseName: String,
        email: String,
        enrolled_ref: DatabaseReference,
        completed_ref: DatabaseReference,
        holder: CustomAdapter3.ViewHolder,
        itemsViewModel: ItemsViewModel3
    ) {



    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView4)
        val textViewTitle: TextView = itemView.findViewById(R.id.textView6)
        val textViewSl: TextView = itemView.findViewById(R.id.textView4)
        val ytButton: Button = itemView.findViewById(R.id.button3)

    }
}
