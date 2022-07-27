package com.koushikjoshi.lessonist.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.koushikjoshi.lessonist.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExploreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExploreFragment : Fragment() {

    lateinit var allCoursesRecycler: RecyclerView
    lateinit var progressBar2: ProgressBar

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var searchBar: EditText = view.findViewById(R.id.searchCourses)
        allCoursesRecycler = view.findViewById(R.id.allCoursesRecycler)
        progressBar2 = view.findViewById(R.id.progressBar2)

        allCoursesRecycler.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        allCoursesRecycler.visibility = View.GONE

        searchBar.gravity = Gravity.CENTER

        addCoursesToList(allCoursesRecycler)
    }

    private fun addCoursesToList(allCoursesRecycler: RecyclerView) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("/courses")
        val data = ArrayList<ItemsViewModel2>()
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val num_courses = dataSnapshot.childrenCount
                Log.w(ContentValues.TAG, "Adding "+num_courses.toString()+" number of courses.")
                for (i in 1..num_courses) {
                    val name = dataSnapshot.child("course$i/name").getValue(String::class.java)
                    val image =
                        dataSnapshot.child("course$i/image").getValue(String::class.java)

                    val url = image

                    data.add(ItemsViewModel2(image.toString(), name.toString()))
                    Log.w(ContentValues.TAG, "Added "+i.toString()+" element")

                    progressBar2.visibility = View.GONE
                    allCoursesRecycler.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }

        })

        val adapter = CustomAdapter2(data)
        allCoursesRecycler.adapter = adapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExploreFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}