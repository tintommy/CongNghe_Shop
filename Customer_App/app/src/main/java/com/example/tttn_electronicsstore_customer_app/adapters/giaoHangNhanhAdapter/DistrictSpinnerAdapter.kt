package com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District


class DistrictSpinnerAdapter(
    context: Context,
    private val districtList: List<District>
) : ArrayAdapter<District>(context, android.R.layout.simple_spinner_dropdown_item, districtList) {

    override fun getItemId(position: Int): Long {
        return districtList[position].districtID.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = districtList[position].districtName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).text = districtList[position].districtName
        return view
    }
}