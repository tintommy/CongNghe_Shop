package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvImportReceiptItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductImportItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductImportSearchItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductSearchItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product

class ImportReceiptAdapter() :
    RecyclerView.Adapter<ImportReceiptAdapter.ImportReceiptViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<ImportReceipt>() {
        override fun areItemsTheSame(
            oldItem: ImportReceipt,
            newItem: ImportReceipt
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ImportReceipt,
            newItem: ImportReceipt
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ImportReceiptViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvImportReceiptItemBinding.inflate(inflater, parent, false)
        return ImportReceiptViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImportReceiptViewHolder, position: Int) {
        val importReceipt = differ.currentList[position]
        holder.bind(importReceipt, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(importReceipt: ImportReceipt)


    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ImportReceiptViewHolder(val binding: RvImportReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(importReceipt: ImportReceipt, position: Int) {

            binding.apply {
                tvReceiptId.text = importReceipt.id.toString()
                tvCreatedAdmin.text = importReceipt.staffName
                tvCreatedDate.text = Convert.formatDate(importReceipt.createdDate.substring(0, 10))
                tvReceiptTotal.text =
                    Convert.formatLongNumberWithDotSeparator(importReceipt.total) + "đ"
            }
            itemView.setOnClickListener {
                click?.onItemClick(importReceipt)
            }
        }
    }


}