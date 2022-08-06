package com.koushikjoshi.lessonist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MyCertificatesActivity : AppCompatActivity() {

    lateinit var certificatesRecyclerView: RecyclerView
    lateinit var noCertifsText: TextView
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_certificates)

        certificatesRecyclerView = findViewById(R.id.certificatesRecycler)
        noCertifsText = findViewById(R.id.noCertifs)
        progressBar = findViewById(R.id.progressBar5)

        certificatesRecyclerView.visibility = View.GONE
        noCertifsText.visibility = View.GONE

        val user = FirebaseAuth.getInstance().currentUser

        val email = user?.email.toString().dropLast(4)

        certificatesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val db = Firebase.database
        val data = ArrayList<ItemsViewModel4>()
        val myRef = db.getReference("/users/"+email+"/courses_completed")
        myRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach {
                        val courseName = it.key.toString()
                        val imgUrl = snapshot.child(courseName+"/image").value.toString()
                        data.add(ItemsViewModel4(imgUrl.toString(), courseName.toString()))
                    }
                    val adapter = CustomAdapter4(data)
                    certificatesRecyclerView.adapter = adapter

                    certificatesRecyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
                else{
                    progressBar.visibility = View.GONE
                    noCertifsText.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}