package com.shahbaz.letstalk.helper

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.shahbaz.letstalk.MainActivity
import com.shahbaz.letstalk.R

fun Fragment.hideBottomNavigation(){
    val bottomNavigationView =(activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.visibility=View.GONE
}

fun Fragment.showBottomNavigation(){
    val bottomNavigationView =(activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bottomNavigationView.visibility=View.VISIBLE
}