package com.example.tttn_electronicsstore_customer_app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvAddressItemBinding
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress

class ReceiveAddressAdapter :
    RecyclerView.Adapter<ReceiveAddressAdapter.ReceiveAddressViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<ReceiveAddress>() {
        override fun areItemsTheSame(oldItem: ReceiveAddress, newItem: ReceiveAddress): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReceiveAddress, newItem: ReceiveAddress): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReceiveAddressViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvAddressItemBinding.inflate(inflater, parent, false)
        return ReceiveAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiveAddressViewHolder, position: Int) {
        val receiveAddress = differ.currentList[position]
        holder.bind(receiveAddress, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(receiveAddress: ReceiveAddress)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    private var selectedAddress = -1

    inner class ReceiveAddressViewHolder(val binding: RvAddressItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(receiveAddress: ReceiveAddress, position: Int) {
            binding.tvNameAddress.text = receiveAddress.addressName
            itemView.setOnClickListener {
                click?.onItemClick(receiveAddress)
                selectedAddress = position
            }


            if (position == selectedAddress) {
                binding.layout.setBackgroundColor(Color.parseColor("#2196F3"))
                binding.tvNameAddress.setTextColor(Color.parseColor("#FFFFFF"))
            } else {
                binding.layout.setBackgroundColor(Color.parseColor("#DADADA"))
                binding.tvNameAddress.setTextColor(Color.parseColor("#000000"))
            }
        }
    }
}