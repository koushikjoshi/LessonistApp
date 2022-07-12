package com.koushikjoshi.lessonist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.koushikjoshi.lessonist.fragments.ExploreFragment
import com.koushikjoshi.lessonist.fragments.LearnFragment
import com.koushikjoshi.lessonist.fragments.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private val learnFragment = LearnFragment()
    private val exploreFragment = ExploreFragment()
    private val profileFragment = ProfileFragment()


    lateinit var bottomNavBar: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        replaceFragment(learnFragment)

        bottomNavBar = findViewById(R.id.bottom_navigation)

        bottomNavBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.ic_learn -> replaceFragment(learnFragment)
                R.id.ic_explore -> replaceFragment(exploreFragment)
                R.id.ic_profile -> replaceFragment(profileFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment!=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentContainer, fragment)
            transaction.commit()
        }
    }
}