package com.koushikjoshi.lessonist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MyCertificatesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_certificates)

        val user = FirebaseAuth.getInstance().currentUser

        val email = user?.email.toString().dropLast(4)

    }
}