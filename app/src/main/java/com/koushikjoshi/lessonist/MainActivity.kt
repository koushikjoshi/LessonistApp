package com.koushikjoshi.lessonist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var SignInbutton: Button

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!=null) {
            openNextScreen()
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth

//        initialize widgets
        SignInbutton = findViewById(R.id.signIn)

//        Do something when button pressed
        SignInbutton.setOnClickListener {
            if(accountExists()){
                openNextScreen()
            }
            else{
                createAccount()
                openNextScreen()
            }
        }


    }

    private fun createAccount() {
        TODO("Not yet implemented")
    }

    private fun openNextScreen() {
        val intent: Intent
        intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun accountExists(): Boolean {
        return true
    }
}