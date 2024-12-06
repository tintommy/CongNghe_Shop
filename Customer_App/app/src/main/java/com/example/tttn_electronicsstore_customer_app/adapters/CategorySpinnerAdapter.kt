package com.example.tttn_electronicsstore_customer_app.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tttn_electronicsstore_customer_app.models.Category

class CategorySpinnerAdapter(
    context: Context,
    private val categoryList: List<Category>
) : ArrayAdapter<Category>(context, android.R.layout.simple_spinner_dropdown_item, categoryList) {

    override fun getItemId(position: Int): Long {
        return categoryList[position].id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = categoryList[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).text = categoryList[position].name
        return view
    }
}