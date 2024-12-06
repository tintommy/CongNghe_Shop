package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.databinding.RvListOderDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.helper.Convert

class CartOrderDetailAdapter : RecyclerView.Adapter<CartOrderDetailAdapter.OrderDetailViewHolder>() {

    private val callback = object : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListOderDetailItemBinding.inflate(inflater, parent, false)
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val cart = differ.currentList[position]
        holder.bind(cart)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class OrderDetailViewHolder(val binding: RvListOderDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cart: Cart) {
            binding.apply {
                tvProductName.text = cart.productName
                tvProductPrice.text = Convert.formatNumberWithDotSeparator(cart.productPrice)+"đ"
                tvProductQuantity.text = "Số lượng: " + cart.quantity.toString()
                for (image in cart.imageList) {
                    if (image.avatar)
                        Glide.with(itemView).load(image.link).into(ivProductAvatar)
                }
            }

        }
    }

}