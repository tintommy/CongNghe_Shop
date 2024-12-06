package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListBrandManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail

class DetailAdapter : RecyclerView.Adapter<DetailAdapter.DetailViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Detail>() {
        override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListDetailPickItemBinding.inflate(inflater, parent, false)
        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = differ.currentList[position]
        holder.bind(detail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onCbClick(detail: Detail, isChecked: Boolean)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class DetailViewHolder(val binding: RvListDetailPickItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: Detail) {
            binding.cbDetail.text = detail.name
                binding.cbDetail.setOnCheckedChangeListener{_, isChecked->
                    click?.onCbClick(detail,isChecked)
                }
        }
    }
}