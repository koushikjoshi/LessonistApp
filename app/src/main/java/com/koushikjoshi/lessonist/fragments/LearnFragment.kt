package com.koushikjoshi.lessonist.fragments

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.koushikjoshi.lessonist.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


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
    lateinit var coursesViewButton: Button

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

        cardViewBottom = view.findViewById(R.id.cardView2)
        progressBar = view.findViewById(R.id.progressBar)

        coursesRecycler.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)

        userHasCourses(cardViewBottom, progressBar, coursesRecycler)
    }

    private fun addRecyclerViewElements(
        coursesRecycler: RecyclerView,
        cardViewBottom: CardView,
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
                this@LearnFragment.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })
                val adapter = CustomAdapter2(data)
                coursesRecycler.adapter = adapter

    }

    private fun userHasCourses(
        cardViewBottom: CardView,
        progressBar: ProgressBar,
        coursesRecycler: RecyclerView,
    ) {
        progressBar.visibility = View.VISIBLE

        val db = Firebase.database

//        var existence: Boolean = false
        val user = FirebaseAuth.getInstance().currentUser
        var email = user?.email.toString().dropLast(4)
        val myRef = db.getReference("users/"+email+"/courses_enrolled")
        GlobalScope.async { myRef.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = snapshot.childrenCount
                Log.d(TAG, "Value is: " + value)
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
                Log.w(TAG, "Failed to read value.", error.toException())
            }

        })
        }
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