package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductSearchItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ProductSearchAdapter :
    RecyclerView.Adapter<ProductSearchAdapter.ProductManagerViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductManagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListProductSearchItemBinding.inflate(inflater, parent, false)
        return ProductManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductManagerViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(product: Product)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class ProductManagerViewHolder(val binding: RvListProductSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                for (image in product.imageList)
                    if (image.avatar)
                        Glide.with(itemView).load(image.link).into(binding.ivImage)

                tvProductName.text = product.name
                tvBought.text = "Đã bán: " + product.bought.toString()
                tvProductPrice.text = formatNumberWithDotSeparator(product.price) + "đ"
                tvQuantity.text = "Còn lại: " + product.quantity.toString()
            }

            itemView.setOnClickListener {
                click?.onItemClick(product)
            }


        }
    }

    fun formatNumberWithDotSeparator(number: Int): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = '.'
        }
        val decimalFormat = DecimalFormat("#,###", symbols)
        return decimalFormat.format(number)
    }
}