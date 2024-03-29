package com.koushikjoshi.lessonist.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.koushikjoshi.lessonist.*
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var logoutText: TextView
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var nameText: TextView
    lateinit var certificatesButton: Button
    lateinit var coursesButton: Button
    lateinit var suggestCourseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logoutText = view.findViewById(R.id.logOutText)
        nameText = view.findViewById(R.id.textView3)
        certificatesButton = view.findViewById(R.id.myCertificatesButton)
        coursesButton = view.findViewById(R.id.myCoursesButton)
        suggestCourseButton = view.findViewById(R.id.suggestCourseButton)

        val user = FirebaseAuth.getInstance().currentUser
        var name = user?.displayName.toString()
        var nameFinal = name.trim().split("\\s+".toRegex()).map { it.capitalize() }.joinToString(" ")
        nameText.setText(nameFinal.toString())

        logoutText.setOnClickListener {
            Firebase.auth.signOut()
            AuthUI.getInstance().signOut(view.context)
            Toast.makeText(view.context, "Logging Out", Toast.LENGTH_SHORT).show()
            val intent: Intent = Intent(view.context, MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        certificatesButton.setOnClickListener{
            val intent: Intent = Intent(view.context, MyCertificatesActivity::class.java)

            startActivity(intent)
        }

        coursesButton.setOnClickListener {
            val intent: Intent = Intent(view.context, MyCoursesActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}