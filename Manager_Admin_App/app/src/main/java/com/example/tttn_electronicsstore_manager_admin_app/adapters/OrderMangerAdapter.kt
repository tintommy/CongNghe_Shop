package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListOrderManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvOtherImagesItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class OrderMangerAdapter : RecyclerView.Adapter<OrderMangerAdapter.OrderMangerViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderMangerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListOrderManagerItemBinding.inflate(inflater, parent, false)
        return OrderMangerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderMangerViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.bind(order)
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


    inner class OrderMangerViewHolder(val binding: RvListOrderManagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.apply {
                tvOrderId.text = order.id.toString()
                tvOrderDate.text = order.date.substring(0, 10)
                tvOrderTotal.text = formatNumberWithDotSeparator(order.total) + "đ"
                tvOrderAddress.text = order.receiverAddress
                tvOrderUsername.text = order.username
                when (order.status) {
                    0 -> {
                        tvOrderStatus.text = "Đã huỷ"

                    }

                    1 -> {
                        tvOrderStatus.text = "Đang chờ"
                    }

                    2 -> {
                        tvOrderStatus.text = "Đang chuẩn bị"
                    }

                    3 -> {
                        tvOrderStatus.text = "Đang giao"
                    }

                    4 -> {
                        tvOrderStatus.text = "Đã giao hàng"
                    }

                    5 -> {
                        tvOrderStatus.text = "Thành công"
                    }

                    6 -> {
                        tvOrderStatus.text = "Giao hàng thất bại"
                    }

                    else -> {}
                }

                if (order.onlinePay)
                    tvOrderOnlinePay.visibility = View.VISIBLE
                else
                    tvOrderOnlinePay.visibility = View.GONE

                if(order.status==0){
                    binding.tvOrderOnlinePay.visibility=View.GONE
                }
            }
            itemView.setOnClickListener {
                click?.onItemClick(order)
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