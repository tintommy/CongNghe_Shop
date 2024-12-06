package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvProductDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.models.ProductDetail

class ProductDetailAdapter :
    RecyclerView.Adapter<ProductDetailAdapter.ProductDetailViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<ProductDetail>() {
        override fun areItemsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvProductDetailItemBinding.inflate(inflater, parent, false)
        return ProductDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val productDetail = differ.currentList[position]
        holder.bind(productDetail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    inner class ProductDetailViewHolder(val binding: RvProductDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(productDetail: ProductDetail) {
            binding.tvProductName.text = productDetail.detailName + ":"
            binding.tvProductDetailvalue.text = productDetail.value
        }
    }
}