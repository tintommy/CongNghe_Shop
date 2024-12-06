package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.models.User

class UserSpinnerAdapter(
    context: Context,
    private val users: List<User>
) : ArrayAdapter<User>(context, 0, users) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.spinner_user_item, parent, false)
        val user = getItem(position)
        val textView = view.findViewById<TextView>(R.id.tvShipperFullname)
        textView.text = user?.fullName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    fun getUsername(position: Int): String? {
        return users[position].username
    }
}