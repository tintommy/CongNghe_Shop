package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvImportReceiptItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailImportReceiptItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Image
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail

class DetailImportReceiptAdapter() :
    RecyclerView.Adapter<DetailImportReceiptAdapter.ImportReceiptViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<ImportReceiptDetail>() {
        override fun areItemsTheSame(
            oldItem: ImportReceiptDetail,
            newItem: ImportReceiptDetail
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ImportReceiptDetail,
            newItem: ImportReceiptDetail
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
        val binding = RvListDetailImportReceiptItemBinding.inflate(inflater, parent, false)
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

    inner class ImportReceiptViewHolder(val binding: RvListDetailImportReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(importReceiptDetail: ImportReceiptDetail, position: Int) {

            binding.apply {
                tvProductName.text = importReceiptDetail.productName
                tvProductQuantity.text = importReceiptDetail.quantity.toString()
                tvProductPrice.text =
                    Convert.formatNumberWithDotSeparator(importReceiptDetail.price) + "đ"

                Glide.with(itemView).load(importReceiptDetail.imageList[importReceiptDetail.imageList.indexOfFirst { it.avatar }].link).into(ivImage)
            }

        }
    }


}