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
import androidx.recyclerview.widget.LinearLayoutManager
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

        videosRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        var url = intent.extras?.get("url")
        var courseName = intent.extras?.get("courseName")

        courseNameText = findViewById(R.id.courseNameText)
        courseNameText.setText(courseName.toString())

//        check if user has already enrolled in the course
        GlobalScope.launch {
           async {
               courseExists(email, courseName, url, enrollButton, progressBar, videosRecyclerView)
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
        progressBar: ProgressBar,
        videosRecyclerView: RecyclerView
    ) {
        var db = Firebase.database
        var myRef = db.getReference("users/" + email)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("courses_enrolled/" + courseName).exists()) {
                    var num_watched = Math.toIntExact(snapshot.child("courses_enrolled/"+courseName+"/num_watched").value as Long)
                    enrollButton.visibility = View.GONE
                    addVideos(progressBar, url.toString(), courseName, email, videosRecyclerView, num_watched)
                } else if (snapshot.child("courses_completed/" + courseName).exists()) {
                    var num_watched = Math.toIntExact(snapshot.child("courses_completed/"+courseName+"/num_watched").value as Long)
                    enrollButton.visibility = View.GONE
                    addVideos(progressBar, url.toString(), courseName, email, videosRecyclerView, num_watched)
                } else {
                    var num_watched = 0
                    enrollButton.visibility = View.VISIBLE
                    addVideos(progressBar, url.toString(), courseName, email, videosRecyclerView, num_watched)
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
        courseRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                var courseNum = snapshot.childrenCount
                for (i in 1..courseNum)
                {
                    if (snapshot.child("course$i/name")
                            .getValue(String::class.java) == courseName)
                    {
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
    private fun addVideos(
        progressBar: ProgressBar,
        url: String,
        courseName: Any?,
        email: String,
        videosRecyclerView: RecyclerView,
        num_watched: Int
    ) {

        var db = Firebase.database
        val data = ArrayList<ItemsViewModel3>()
        var userRef = db.getReference("users/"+email+"/courses_enrolled")
        var courseRef = db.getReference("courses")

        courseRef.addValueEventListener(object: ValueEventListener
        {
            override fun onDataChange(snapshot: DataSnapshot)
            {
                var num_children = snapshot.childrenCount
                snapshot.children.forEach()
                {

                    if(it.child("name").value.toString() == courseName.toString()){

                        var num_vids: Int = Math.toIntExact(it.child("num_videos").value as Long)
                        var i = 1

                        it.child("videos").children.forEach()
                        {

                            var slno = i
                            var image = R.drawable.ic_baseline_panorama_fish_eye_24
                            if(i<=num_watched){
                                image = R.drawable.ic_baseline_check_circle_outline_24
                            }

                            var title = it.child("title").value.toString()
                            var url = it.child("url").value.toString()
                            i++

                            var titleFinal = title.toString().substring(0, Math.min(title.length, 30))+"..."

                            data.add(ItemsViewModel3(image, title.toString(), slno.toString(), url.toString(), courseName.toString(), email.toString()))
                        }
                    }
                }


                progressBar.visibility = View.GONE
                videosRecyclerView.visibility = View.VISIBLE

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
        val adapter = CustomAdapter3(data)
        videosRecyclerView.adapter = adapter

    }

}

