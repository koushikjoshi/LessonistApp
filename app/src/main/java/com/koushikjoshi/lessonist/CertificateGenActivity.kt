package com.koushikjoshi.lessonist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.TextView
import androidx.core.text.bold
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class CertificateGenActivity : AppCompatActivity() {

    lateinit var textView8: TextView
    lateinit var textView9: TextView
    lateinit var textView10: TextView
    lateinit var downloadButton: Button
    lateinit var shareButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_certificate_gen)

        textView8 = findViewById(R.id.textView8)
        textView9 = findViewById(R.id.textView9)
        textView10 = findViewById(R.id.textView10)

        downloadButton = findViewById(R.id.button4)
        shareButton = findViewById(R.id.button5)

        var courseName = intent.extras?.get("courseName")
        val user = FirebaseAuth.getInstance().currentUser
        var name = user?.displayName.toString()
        var nameFinal = name.trim().split("\\s+".toRegex()).map { it.capitalize() }.joinToString(" ")

        var string1 = "This is to certify that "
        var string2 = " has successfully, as per their own assessment, completed the course titled "

        var finalString = SpannableStringBuilder().append(string1).bold { append(nameFinal) }.append(string2).bold { append(courseName.toString()) }

        textView8.setText(finalString)

        var db = Firebase.database
        var ref = db.getReference("courses")



        ref.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    if(it.child("name").value.toString()==courseName.toString()){
                        var channel_name = it.child("channel_name").value.toString()
                        var channel_id = it.child("channel_id").value.toString()
                        var stringfortv9 = SpannableStringBuilder().append("All credit for the creation of this course belongs to ").bold { append(channel_name) }.append(" on YouTube")
                        textView9.setText(stringfortv9)
                        textView10.setText("/"+channel_id)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })





    }
}