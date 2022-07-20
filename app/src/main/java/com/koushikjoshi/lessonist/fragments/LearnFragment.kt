package com.koushikjoshi.lessonist.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.koushikjoshi.lessonist.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LearnFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LearnFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var coursesRecycler: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var cardViewTop: CardView
    lateinit var cardViewBottom: CardView
    lateinit var hoursText: TextView
    lateinit var constraintLayout: ConstraintLayout
    lateinit var nameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coursesRecycler = view.findViewById(R.id.learnRecycler)
        cardViewBottom = view.findViewById(R.id.cardView2)
        progressBar = view.findViewById(R.id.progressBar)
        constraintLayout = view.findViewById(R.id.parent_layout)
        nameText = view.findViewById(R.id.textView)

        val user = FirebaseAuth.getInstance().currentUser

        var name = user?.displayName
        var i = name?.indexOf(' ')?.toInt()
        var finalName = name?.substring(0, i!!)

        val textFinal = "Welcome, "+finalName.toString().capitalize()

        nameText.setText(textFinal.toString())
        coursesRecycler.visibility = View.GONE
        cardViewBottom.visibility = View.GONE

        coursesRecycler.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

    viewLifecycleOwner.lifecycleScope.launch{
        if(userHasCourses()){
            addRecyclerViewElements(coursesRecycler)
            progressBar.visibility = View.GONE
//            addTestElements(coursesRecycler)
            coursesRecycler.visibility = View.VISIBLE
//            addRecyclerVIewScrollAnimation(coursesRecycler, constraintLayout)
        }else{
            progressBar.visibility = View.GONE
            cardViewBottom.visibility = View.VISIBLE
        }
    }

    }



    private fun addTestElements(coursesRecycler: RecyclerView) {

        val data = ArrayList<ItemsViewModel2>()
//        data.add(ItemsViewModel(R.drawable.web_development_course, "Learn Web Development"))
//        data.add(ItemsViewModel(R.drawable.python_course, "Learn Python"))
//        data.add(ItemsViewModel(R.drawable.data_science_course, "Data Science"))
//        data.add(ItemsViewModel(R.drawable.creative_writing_course, "Creative Writing"))
//        data.add(ItemsViewModel(R.drawable.time_management_course, "Time Management"))

        val adapter = CustomAdapter2(data)
//
        coursesRecycler.adapter = adapter

    }

    private fun addRecyclerViewElements(coursesRecycler: RecyclerView) {
        val db = Firebase.firestore
        val data = ArrayList<ItemsViewModel2>()
        var user = FirebaseAuth.getInstance().currentUser
        var email = user?.email.toString()
        db.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { courses->

               var map: Map<String, Map<String, String>> = courses.data?.get("courses_enrolled") as Map<String, Map<String, String>>
                Log.d("TAG", "value of courses enrolled is ${courses.data?.get("courses_enrolled")}")

                for ((key, value) in map) {
                    println("$key")
                    for((key2, value2) in value){
                        println("$key2 = $value2")
                        if(key2.toString()=="image"){
                            data.add(ItemsViewModel2(value2.toString(), key.toString()))
                        }
                    }
                }
                val adapter = CustomAdapter2(data)
//
                coursesRecycler.adapter = adapter


            }

    }

    private suspend fun userHasCourses(): Boolean {
        val db = Firebase.firestore
        var existence: Boolean = false
        val user = FirebaseAuth.getInstance().currentUser
        var email = user?.email.toString()
        db.collection("users")
            .document(email)
            .get()
            .addOnSuccessListener { courses->

                if(courses.data?.get("courses_enrolled")!=""){
                    existence = true
                }
                else{
                    existence = false
                }
//                existence = courses.data?.get("courses_enrolled").toString()!=""
                Log.d("TAG", "value of courses is ${courses.data?.get("courses_enrolled")}")
                Log.d("TAG", "EXISTENCE 1 VALUE is "+existence.toString())
            }.await()
        Log.d("TAG", "EXISTENCE 2 VALUE is "+existence.toString())
        return existence
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learn, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LearnFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LearnFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}