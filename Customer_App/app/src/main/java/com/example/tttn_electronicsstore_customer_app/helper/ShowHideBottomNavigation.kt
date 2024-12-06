package com.example.tttn_electronicsstore_customer_app.helper


import android.view.View
import androidx.fragment.app.Fragment
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.activity.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


fun Fragment.hideBottomNavigation() {
    val bottomNavigation =
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomMenu)
    bottomNavigation.visibility = View.GONE

    val btnChat = (activity as MainActivity).findViewById<FloatingActionButton>(R.id.btnChatExpand)
    btnChat.visibility = View.GONE

}

fun Fragment.showBottomNavigation() {
    val bottomNavigation =
        (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomMenu)
    bottomNavigation.visibility = View.VISIBLE
    (activity as MainActivity).updateBadge()
    val btnChat = (activity as MainActivity).findViewById<FloatingActionButton>(R.id.btnChatExpand)
    btnChat.visibility = View.VISIBLE

}