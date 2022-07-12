package com.koushikjoshi.lessonist.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.koushikjoshi.lessonist.CustomAdapter
import com.koushikjoshi.lessonist.ItemsViewModel
import com.koushikjoshi.lessonist.R

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


        coursesRecycler.visibility = View.GONE
        cardViewBottom.visibility = View.GONE

        coursesRecycler.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)


        if(userHasCourses()){
            progressBar.visibility = View.GONE
//            addRecyclerViewElements(coursesRecycler)
            addTestElements(coursesRecycler)
            coursesRecycler.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
            cardViewBottom.visibility = View.VISIBLE
        }

    }

    private fun addTestElements(coursesRecycler: RecyclerView) {

        val data = ArrayList<ItemsViewModel>()
        data.add(ItemsViewModel(R.drawable.web_development_course, "Learn Web Development"))
        data.add(ItemsViewModel(R.drawable.python_course, "Learn Python"))
        data.add(ItemsViewModel(R.drawable.data_science_course, "Data Science"))
        data.add(ItemsViewModel(R.drawable.creative_writing_course, "Creative Writing"))
        data.add(ItemsViewModel(R.drawable.time_management_course, "Time Management"))

        val adapter = CustomAdapter(data)
//
        coursesRecycler.adapter = adapter

    }

    private fun addRecyclerViewElements(coursesRecycler: RecyclerView) {
        TODO("Not yet implemented")
    }

    private fun userHasCourses(): Boolean {
        return true
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