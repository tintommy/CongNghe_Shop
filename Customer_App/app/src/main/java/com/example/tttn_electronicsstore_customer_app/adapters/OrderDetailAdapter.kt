package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.databinding.RvListOderDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Order
import com.example.tttn_electronicsstore_customer_app.models.OrderDetail

class OrderDetailAdapter : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<OrderDetail>() {
        override fun areItemsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
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
        val orderDetail = differ.currentList[position]
        holder.bind(orderDetail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(order: Order)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class OrderDetailViewHolder(val binding: RvListOderDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderdetail: OrderDetail) {
            binding.apply {
                tvProductName.text = orderdetail.productName
                tvProductPrice.text = Convert.formatNumberWithDotSeparator(orderdetail.price)+"đ"
                tvProductQuantity.text = "Số lượng: " + orderdetail.quantity.toString()
                for (image in orderdetail.productImageList) {
                    if (image.avatar)
                        Glide.with(itemView).load(image.link).into(ivProductAvatar)
                }
            }

        }
    }


}