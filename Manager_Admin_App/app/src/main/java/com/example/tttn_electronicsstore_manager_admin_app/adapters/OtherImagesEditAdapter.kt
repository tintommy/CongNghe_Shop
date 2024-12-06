package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvOtherImagesItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.Image

class OtherImagesEditAdapter :
    RecyclerView.Adapter<OtherImagesEditAdapter.OtherImagesAdapterViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): OtherImagesAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvOtherImagesItemBinding.inflate(inflater, parent, false)
        return OtherImagesAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OtherImagesAdapterViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onDeleteClick(image: Image)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class OtherImagesAdapterViewHolder(val binding: RvOtherImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Image) {
            if (image.avatar==false) {
                itemView.visibility= View.VISIBLE
                Glide.with(itemView).load(image.link).into(binding.ivImage)
                binding.btnDelete.setOnClickListener {
                    click?.onDeleteClick(image)
                }
            }
            else{
                itemView.visibility= View.GONE
            }

        }
    }
}