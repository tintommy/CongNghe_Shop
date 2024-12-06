package com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvCategoryItemBinding
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Province

class ProvinceSpinnerAdapter(
    context: Context,
    private val provinceList: List<Province>
) : ArrayAdapter<Province>(context, android.R.layout.simple_spinner_dropdown_item, provinceList) {

    override fun getItemId(position: Int): Long {
        return provinceList[position].provinceID.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = provinceList[position].provinceName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).text = provinceList[position].provinceName
        return view
    }
}