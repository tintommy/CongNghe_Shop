package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.databinding.SpBrandItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand

class BrandSpinnerAdapter(
    private val context: Context,
    private val brandList: List<Brand>
) : BaseAdapter() {


    override fun getCount(): Int {
        return brandList.size
    }

    override fun getItem(p0: Int): Any {
        return brandList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return brandList.get(p0).id.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val binding: SpBrandItemBinding
        val view: View

        if (p1 == null) {
            binding = SpBrandItemBinding.inflate(LayoutInflater.from(context), p2, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = p1.tag as SpBrandItemBinding
            view = p1
        }

        binding.tvBrandName.text = brandList[p0].name
        Glide.with(view).load(brandList[p0].image).into(binding.ivBrand)

        return view
    }
}