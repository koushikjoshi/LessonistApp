package com.koushikjoshi.lessonist

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var SignInbutton: Button
    val Req_Code:Int=123
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        auth = Firebase.auth

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
                if(userExists(account.email)){
                    UpdateUI(account)
                }
                else{
                    addUserToDatabase(account)
                    UpdateUI(account)
                }

            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUserToDatabase(account: GoogleSignInAccount) {
        val db = Firebase.firestore



    }

    private fun userExists(email: String?): Boolean {

        val db = Firebase.firestore

        var docRef = db.collection("users").document(email.toString())

        if(docRef.get()!=null){
            return true
        }
        else{
            return false
        }
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

        private fun createAccount() {

//        Add empty database collections for completed courses and enrolled courses


    }

    private fun openNextScreen() {
        val intent: Intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun accountExists(email: String?): Boolean {
        val db = FirebaseFirestore.getInstance()

        return true
    }
}