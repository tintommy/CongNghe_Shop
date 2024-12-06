package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductReview
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductSale
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMostBoughtItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMostReviewItemBinding

class ProductMostReviewAdapter :
    RecyclerView.Adapter<ProductMostReviewAdapter.ProductViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<ProductReview>() {
        override fun areItemsTheSame(oldItem: ProductReview, newItem: ProductReview): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: ProductReview, newItem: ProductReview): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvMostReviewItemBinding.inflate(inflater, parent, false)
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
        fun onItemClick(product: ProductReview)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ProductViewHolder(val binding: RvMostReviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductReview) {
            for (image in product.imageList)
                if (image.avatar) {
                    Glide.with(itemView).load(image.link).apply(
                        RequestOptions().transform(RoundedCorners(16))
                    ).into(binding.ivProductAvatar)
                }

            binding.tvProductName.text = product.productName
            if (product.review5 != 0)
                binding.tvProductReview5.text = "${product.review5} lượt 5"
            else {
                binding.tvProductReview5.visibility = View.GONE
            }
            if (product.review4 != 0)
                binding.tvProductReview4.text = "${product.review4} lượt 4"
            else {
                binding.tvProductReview4.visibility = View.GONE
            }
            itemView.setOnClickListener {
                click?.onItemClick(product)
            }
        }
    }
}