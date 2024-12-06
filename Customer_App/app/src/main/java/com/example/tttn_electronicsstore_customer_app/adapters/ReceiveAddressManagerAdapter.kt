package com.example.tttn_electronicsstore_customer_app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvAddressItemBinding
import com.example.tttn_electronicsstore_customer_app.databinding.RvAddressManagerItemBinding
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress

class ReceiveAddressManagerAdapter :
    RecyclerView.Adapter<ReceiveAddressManagerAdapter.ReceiveAddressViewHolder>() {


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
        val binding = RvAddressManagerItemBinding.inflate(inflater, parent, false)
        return ReceiveAddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReceiveAddressViewHolder, position: Int) {
        val receiveAddress = differ.currentList[position]
        holder.bind(receiveAddress, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    fun deleteItem(position: Int) {
        val currentList = differ.currentList.toMutableList()
        if (position >= 0 && position < currentList.size) {
            currentList.removeAt(position)
            differ.submitList(currentList)
            notifyDataSetChanged()
        }
    }

    interface OnItemClickListener {
        fun onItemClick(receiveAddress: ReceiveAddress)
        fun onEditClick(receiveAddress: ReceiveAddress)
        fun onDeleteClick(receiveAddress: ReceiveAddress,position: Int)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class ReceiveAddressViewHolder(val binding: RvAddressManagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(receiveAddress: ReceiveAddress, position: Int) {

            binding.tvAddressName.text = receiveAddress.addressName
            binding.tvReceiverName.text = receiveAddress.receiverName
            binding.tvReceiverPhone.text = receiveAddress.receiverPhone
            binding.tvReceiverAddress.text = receiveAddress.receiverAddress
            binding.btnEdit.setOnClickListener {
                click?.onEditClick(receiveAddress)
            }
            binding.btnDelete.setOnClickListener {
                click?.onDeleteClick(receiveAddress,position)
            }
        }
    }
}