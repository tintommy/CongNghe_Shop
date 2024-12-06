package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductSale
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMostBoughtItemBinding

class ProductMostBoughtAdapter :
    RecyclerView.Adapter<ProductMostBoughtAdapter.ProductViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<ProductSale>() {
        override fun areItemsTheSame(oldItem: ProductSale, newItem: ProductSale): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductSale, newItem: ProductSale): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvMostBoughtItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(product: ProductSale)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ProductViewHolder(val binding: RvMostBoughtItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductSale) {
            for (image in product.imageList)
                if (image.avatar) {
                    Glide.with(itemView).load(image.link).apply(
                        RequestOptions().transform(RoundedCorners(16))).into(binding.ivProductAvatar) }

            binding.tvProductName.text = product.productName
            binding.tvProductSum.text = "${product.sum} được bán"
            itemView.setOnClickListener {
                click?.onItemClick(product)
            }
        }
    }
}