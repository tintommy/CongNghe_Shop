package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_customer_app.databinding.RvListProductSearchItemBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Product

class ProductSearchAdpater :
    RecyclerView.Adapter<ProductSearchAdpater.ProductViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListProductSearchItemBinding.inflate(inflater, parent, false)
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
        fun onItemClick(productSearch: Product)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ProductViewHolder(val binding: RvListProductSearchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            for (image in product.imageList)
                if (image.avatar) {
                    Glide.with(itemView).load(image.link).apply(
                        RequestOptions().transform(RoundedCorners(16))).into(binding.ivImage) }

            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = Convert.formatNumberWithDotSeparator(product.price)+"đ"
            itemView.setOnClickListener {
                click?.onItemClick(product)
            }

            if(product.status==false){
                binding.tvProductPrice.text = "Ngừng kinh doanh"
            }
        }
    }
}