package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListProductManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ProductManagerAdapter :
    RecyclerView.Adapter<ProductManagerAdapter.ProductManagerViewHolder>() {


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
        val binding = RvListProductManagerItemBinding.inflate(inflater, parent, false)
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
        fun onLongClick(product: Product)
        fun onSwitchClick(product: Product, isChecked: Boolean)
        fun onPriceHistoryClick(product: Product)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class ProductManagerViewHolder(val binding: RvListProductManagerItemBinding) :
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

                binding.switchStatus.isChecked = product.status
            }



            if (product.status) {
                binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.GREEN)
                binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.RED)
                binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.RED)
            }

            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->
                run {
                    if (isChecked) {
                        binding.switchStatus.thumbTintList =
                            ColorStateList.valueOf(Color.GREEN)
                        binding.switchStatus.trackTintList =
                            ColorStateList.valueOf(Color.GREEN)
                    } else {
                        binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.RED)
                        binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.RED)
                    }

                     click?.onSwitchClick(product, isChecked)
                }
            }



            itemView.setOnClickListener {
                click?.onItemClick(product)
            }

            itemView.setOnLongClickListener {
                click?.onLongClick(product)
                true
            }

            binding.btnPriceHistory.setOnClickListener {
                click?.onPriceHistoryClick(product)
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