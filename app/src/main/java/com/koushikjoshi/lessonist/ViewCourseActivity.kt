package com.koushikjoshi.lessonist

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ViewCourseActivity : AppCompatActivity() {

    lateinit var courseNameText: TextView
    lateinit var backButton: TextView
    lateinit var enrollButton: Button
    lateinit var videosRecyclerView: RecyclerView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_course)

        enrollButton = findViewById(R.id.button2)
        videosRecyclerView = findViewById(R.id.videosRecycler)
        progressBar = findViewById(R.id.progressBar4)

        var email = FirebaseAuth.getInstance().currentUser?.email.toString().dropLast(4)

        enrollButton.visibility = View.GONE
        videosRecyclerView.visibility = View.GONE

        var url = intent.extras?.get("url")
        var courseName = intent.extras?.get("courseName")

        courseNameText = findViewById(R.id.courseNameText)
        courseNameText.setText(courseName.toString())

//        check if user has already enrolled in the course

        var db = Firebase.database
        var myRef = db.getReference("users/"+email)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child("courses_enrolled/"+courseName).exists()){
                    enrollButton.visibility = View.GONE
                    addVideos(progressBar, url.toString())
                }
                else if(snapshot.child("courses_completed/"+courseName).exists()){
                    enrollButton.visibility = View.GONE
                    addVideos(progressBar, url.toString())
                }
                else{
                    enrollButton.visibility = View.VISIBLE
                    addVideos(progressBar, url.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })



    }

    private fun addVideos(progressBar: ProgressBar, url: String) {
        progressBar.visibility = View.GONE


    }
}