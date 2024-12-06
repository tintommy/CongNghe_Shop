package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvOtherImagesItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail

class OtherImagesAdapter : RecyclerView.Adapter<OtherImagesAdapter.OtherImagesAdapterViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Uri>() {
        override fun areItemsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: Uri, newItem: Uri): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherImagesAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvOtherImagesItemBinding.inflate(inflater, parent, false)
        return OtherImagesAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OtherImagesAdapterViewHolder, position: Int) {
        val uri = differ.currentList[position]
        holder.bind(uri,position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onDeleteClick(position: Int)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class OtherImagesAdapterViewHolder(val binding: RvOtherImagesItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri,position: Int) {
           Glide.with(itemView).load(uri).into(binding.ivImage)
            binding.btnDelete.setOnClickListener {
                click?.onDeleteClick(position)
            }

        }
    }
}