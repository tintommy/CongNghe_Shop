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
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductImportItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductImportSearchItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductSearchItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product

class ProductImportAdapter() :
    RecyclerView.Adapter<ProductImportAdapter.ProductViewHolder>() {
    private val importReceiptDetailList: MutableList<ImportReceiptDetail> = mutableListOf()
    private var total: Long = 0
    private val callback = object : DiffUtil.ItemCallback<ImportReceiptDetail>() {
        override fun areItemsTheSame(
            oldItem: ImportReceiptDetail,
            newItem: ImportReceiptDetail
        ): Boolean {
            return oldItem.productId == newItem.productId
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
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListProductImportItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val importReceiptDetail = differ.currentList[position]
        holder.bind(importReceiptDetail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun getDetailList(): MutableList<ImportReceiptDetail> {
        return importReceiptDetailList;
    }

    fun getTotal(): Long {
        total = 0
        for (detail: ImportReceiptDetail in importReceiptDetailList) {
            total += detail.price * detail.quantity
        }

        return total
    }

    fun addDetail(importReceiptDetail: ImportReceiptDetail) {
        val updatedList = importReceiptDetailList.toMutableList()
        updatedList.add(importReceiptDetail)
        differ.submitList(updatedList)
        notifyDataSetChanged()
        importReceiptDetailList.add(importReceiptDetail)

    }

    fun removeDetail(importReceiptDetail: ImportReceiptDetail) {

//        if (importReceiptDetailList.size == 1) {
//            importReceiptDetailList.clear()
//        } else {
//            importReceiptDetailList.removeAt(position)
//        }

        importReceiptDetailList.removeIf { it.productId == importReceiptDetail.productId }
        differ.submitList(mutableListOf())
        differ.submitList(importReceiptDetailList.toList())


    }

//    private fun updateDetail(importReceiptDetail: ImportReceiptDetail, position: Int) {
//        val updatedList = importReceiptDetailList.toMutableList()
//        updatedList[position] = importReceiptDetail
//        differ.submitList(updatedList)
//
//    }

    interface OnItemClickListener {

        fun onDeleteBtnClick(importReceiptDetail: ImportReceiptDetail)
        fun calculateTotal(total: Long)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ProductViewHolder(val binding: RvListProductImportItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(importReceiptDetail: ImportReceiptDetail) {

            val temp = position
            for (image in importReceiptDetail.imageList)
                if (image.avatar) {
                    Glide.with(itemView).load(image.link).apply(
                        RequestOptions().transform(RoundedCorners(16))
                    ).into(binding.ivImage)
                }

            binding.tvProductName.text = importReceiptDetail.productName

            binding.btnDelete.setOnClickListener {
                removeDetail(importReceiptDetail)
                click?.onDeleteBtnClick(importReceiptDetail)
            }

            binding.etProductPrice.setText(importReceiptDetailList[adapterPosition].price.toString())
            binding.etProductQuantity.setText(importReceiptDetailList[adapterPosition].quantity.toString())


            binding.etProductPrice.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                }

                override fun afterTextChanged(p0: Editable?) {

                    if (!p0.isNullOrEmpty()) {
                        importReceiptDetail.price = p0.toString().replace(".", "").toInt()
                        importReceiptDetailList[adapterPosition] = importReceiptDetail

                        getTotal()
                        click?.calculateTotal(total)

                        binding.etProductPrice.removeTextChangedListener(this)

                        val formatted = Convert.formatNumber(p0.toString())
                        binding.etProductPrice.setText(formatted)

                        binding.etProductPrice.setSelection(formatted.length)

                        binding.etProductPrice.addTextChangedListener(this)

                    }


                }
            })

            binding.etProductQuantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                    if (!p0.isNullOrEmpty()) {
//                        importReceiptDetail.quantity = p0.toString().toInt()
//                        importReceiptDetailList[adapterPosition] = importReceiptDetail
//                        Log.e("Receipt", temp.toString())
//                        Log.e("Receipt", importReceiptDetailList.toString())


                        importReceiptDetail.quantity = p0.toString().replace(".", "").toInt()
                        importReceiptDetailList[adapterPosition] = importReceiptDetail

                        getTotal()
                        click?.calculateTotal(total)

                        binding.etProductQuantity.removeTextChangedListener(this)

                        val formatted = Convert.formatNumber(p0.toString())
                        binding.etProductQuantity.setText(formatted)

                        binding.etProductQuantity.setSelection(formatted.length)

                        binding.etProductQuantity.addTextChangedListener(this)
                    }
                }
            })


        }
    }


}