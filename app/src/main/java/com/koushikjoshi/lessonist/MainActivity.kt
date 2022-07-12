package com.koushikjoshi.lessonist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    lateinit var SignInbutton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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