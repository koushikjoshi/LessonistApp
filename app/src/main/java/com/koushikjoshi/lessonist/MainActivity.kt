package com.koushikjoshi.lessonist

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var SignInbutton: Button
    val Req_Code:Int=123
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var progessbar: ProgressBar
    lateinit var blacKImage: ImageView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth

        progessbar = findViewById(R.id.progressBar3)
        progessbar.visibility = View.GONE
        blacKImage = findViewById(R.id.blackImage)
        blacKImage.visibility = View.GONE

//        Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

//        getting the value of gso inside the GoogleSigninClient
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)


//        initialize the firebaseAuth variable
        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser

        if(user != null){
            val homeIntent: Intent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            finish()
        }



//        initialize widgets
        SignInbutton = findViewById(R.id.signIn)

//        Open home activity when button pressed
        SignInbutton.setOnClickListener {

            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            progessbar.visibility = View.VISIBLE
            blacKImage.visibility = View.VISIBLE
            SignInbutton.isEnabled = false
            SignInbutton.isClickable = false
            signInGoogle()
        }


    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                userExists(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToDatabase(account: GoogleSignInAccount) {

            val db = Firebase.database
            val myRef = db.getReference("users")
            val trueEmail = account.email.toString().dropLast(4)

            val name = mutableMapOf(
                "name" to account.displayName.toString()
            )
            val map = mutableMapOf(
                "courses_enrolled" to "",
                "courses_completed" to "",
                "details" to name
            )
            val data = mutableMapOf(
                trueEmail to map
            )

            myRef.updateChildren(data as Map<String, Any>)

        UpdateUI(account)
    }

    private fun userExists(account: GoogleSignInAccount?) {

        val db = Firebase.database
        val trueEmail = account?.email.toString().dropLast(4)
        val myRef = db.getReference("users")
        myRef.child(trueEmail).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if (account != null) {
                        UpdateUI(account)
                    }
                }
                else{
                    if (account != null) {
                        addUserToDatabase(account)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
    }

    private fun UpdateUI(account: GoogleSignInAccount) {

        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {

                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("email", account.email.toString())
                intent.putExtra("name", account.displayName.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}