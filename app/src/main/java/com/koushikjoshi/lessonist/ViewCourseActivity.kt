package com.koushikjoshi.lessonist

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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
        GlobalScope.launch {
           async {
               courseExists(email, courseName, url, enrollButton, progressBar)
           }.await()
            async{
                addVideos(progressBar, url.toString())
            }.await()

        }




        enrollButton.setOnClickListener {
            enrollButton.isClickable = false
            GlobalScope.launch {
                var function = async {
                    buttonStuff(email, courseName, enrollButton)
                }
                function.await()
//                var intent = Intent(this@ViewCourseActivity, HomeActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            Toast.makeText(this, "Enrolled!", Toast.LENGTH_SHORT).show()

        }
    }

    private suspend fun courseExists(
        email: String,
        courseName: Any?,
        url: Any?,
        enrollButton: Button,
        progressBar: ProgressBar
    ) {
        var db = Firebase.database
        var myRef = db.getReference("users/" + email)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("courses_enrolled/" + courseName).exists()) {
                    this@ViewCourseActivity.enrollButton.visibility = View.GONE
                } else if (snapshot.child("courses_completed/" + courseName).exists()) {
                    this@ViewCourseActivity.enrollButton.visibility = View.GONE
                } else {
                    this@ViewCourseActivity.enrollButton.visibility = View.VISIBLE
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
    }

    private suspend fun buttonStuff(email: String, courseName: Any?, enrollButton: Button) {
        enrollButton.isClickable = false
        var db = Firebase.database
        var userRef = db.getReference("users/"+email+"/courses_enrolled")
        var courseRef = db.getReference("courses")
        courseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var courseNum = snapshot.childrenCount
                for (i in 1..courseNum) {
                    if (snapshot.child("course$i/name")
                            .getValue(String::class.java) == courseName
                    ) {
                        var image = snapshot.child("course$i/image").getValue(String::class.java)
                        var num_videos =
                            snapshot.child("course$i/num_videos").getValue(Int::class.java)
                        var num_watched = 0
                        var url = snapshot.child("course$i/url").getValue(String::class.java)

                        var points = mutableMapOf(
                            "image" to image.toString(),
                            "num_videos" to num_videos?.toInt(),
                            "num_watched" to num_watched?.toInt(),
                            "url" to url.toString()
                        )
                        var data = mutableMapOf(
                            courseName to points
                        )

                        userRef.updateChildren(data as Map<String, Any>)
                        enrollButton.visibility = View.GONE
                    }
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

