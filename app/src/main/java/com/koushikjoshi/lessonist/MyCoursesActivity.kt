package com.koushikjoshi.lessonist

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
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

class MyCoursesActivity : AppCompatActivity() {

    lateinit var coursesRecyclerView: RecyclerView
    lateinit var progressBar7: ProgressBar
    lateinit var noCourses: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_courses)

        progressBar7 = findViewById(R.id.progressBar7)

        coursesRecyclerView = findViewById(R.id.myCoursesRecyclerView)
        coursesRecyclerView.visibility = View.GONE

        noCourses = findViewById(R.id.textView12)
        noCourses.visibility = View.GONE

        coursesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        userHasCourses(noCourses, progressBar7, coursesRecyclerView)

    }

    private fun userHasCourses(
        cardViewBottom: TextView,
        progressBar: ProgressBar,
        coursesRecycler: RecyclerView,
    ) {
        progressBar.visibility = View.VISIBLE

        val db = Firebase.database

//        var existence: Boolean = false
        val user = FirebaseAuth.getInstance().currentUser
        var email = user?.email.toString().dropLast(4)
        val myRef = db.getReference("users/"+email+"/courses_enrolled")
        myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.childrenCount
                Log.d(ContentValues.TAG, "Value is: " + value)
                if(value>0){
                    Log.d("TAG", "Calling addelements function")
                    addRecyclerViewElements(coursesRecycler, cardViewBottom, progressBar)
                }
                else{
                    progressBar.visibility = View.GONE
                    cardViewBottom.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })

    }

    private fun addRecyclerViewElements(
        coursesRecycler: RecyclerView,
        cardViewBottom: TextView,
        progressBar: ProgressBar
    ) {
        val db = Firebase.database
        val data = ArrayList<ItemsViewModel2>()
        var user = FirebaseAuth.getInstance().currentUser
        var email = user?.email.toString().dropLast(4)
        val myRef = db.getReference("users/"+email+"/courses_enrolled")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val num_courses = dataSnapshot.childrenCount
                Log.w(ContentValues.TAG, "Adding "+num_courses.toString()+" number of courses.")
                dataSnapshot.children.forEach() {
                    val name = it.key.toString()
                    val image =
                        dataSnapshot.child(name+"/image").getValue(String::class.java)

                    val url = dataSnapshot.child(name+"/url").getValue(String::class.java)

                    data.add(ItemsViewModel2(image.toString(), name.toString(), url.toString()))
                    Log.w(ContentValues.TAG, "Added "+name.toString()+" element")
                }
                coursesRecycler.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
        val adapter = CustomAdapter2(data)
        coursesRecycler.adapter = adapter

    }

}