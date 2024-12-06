package com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Ward

class WardSpinnerAdapter(
    context: Context,
    private val wardList: List<Ward>
) : ArrayAdapter<Ward>(context, android.R.layout.simple_spinner_dropdown_item, wardList) {

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = wardList[position].wardName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).text = wardList[position].wardName
        return view
    }
}