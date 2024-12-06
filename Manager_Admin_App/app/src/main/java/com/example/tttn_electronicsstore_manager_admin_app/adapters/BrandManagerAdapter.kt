package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListBrandManagerItemBinding

import com.example.tttn_electronicsstore_manager_admin_app.models.Brand

class BrandManagerAdapter : RecyclerView.Adapter<BrandManagerAdapter.BrandManagerViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Brand>() {
        override fun areItemsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Brand, newItem: Brand): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandManagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListBrandManagerItemBinding.inflate(inflater, parent, false)
        return BrandManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BrandManagerViewHolder, position: Int) {
        val brand = differ.currentList[position]
        holder.bind(brand)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(brand: Brand)
        fun onItemLongClick(brand: Brand)
        fun onSwitchClick(brand: Brand, isChecked: Boolean)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class BrandManagerViewHolder(val binding: RvListBrandManagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(brand: Brand) {

            Glide.with(itemView).load(brand.image).into(binding.ivBrand)
            binding.tvBrandName.text = brand.name

            binding.switchStatusBrand.isChecked = brand.status
            if (brand.status) {
                binding.switchStatusBrand.thumbTintList = ColorStateList.valueOf(Color.GREEN)
                binding.switchStatusBrand.trackTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                binding.switchStatusBrand.thumbTintList = ColorStateList.valueOf(Color.RED)
                binding.switchStatusBrand.trackTintList = ColorStateList.valueOf(Color.RED)
            }

            binding.switchStatusBrand.setOnCheckedChangeListener { _, isChecked ->
                run {
                    if (isChecked) {
                        binding.switchStatusBrand.thumbTintList =
                            ColorStateList.valueOf(Color.GREEN)
                        binding.switchStatusBrand.trackTintList =
                            ColorStateList.valueOf(Color.GREEN)
                    } else {
                        binding.switchStatusBrand.thumbTintList = ColorStateList.valueOf(Color.RED)
                        binding.switchStatusBrand.trackTintList = ColorStateList.valueOf(Color.RED)
                    }

                    click?.onSwitchClick(brand, isChecked)
                }
            }


            itemView.setOnLongClickListener {
                click!!.onItemLongClick(brand)
                true
            }

            binding.btnEdit.setOnClickListener { click?.onItemClick(brand) }

        }
    }
}