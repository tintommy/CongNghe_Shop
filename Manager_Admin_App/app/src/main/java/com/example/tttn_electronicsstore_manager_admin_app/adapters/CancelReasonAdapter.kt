package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvCancelReasonItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail

class CancelReasonAdapter : RecyclerView.Adapter<CancelReasonAdapter.CancelReasonViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Pair<String, Int>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>
        ): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, Int>,
            newItem: Pair<String, Int>
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CancelReasonAdapter.CancelReasonViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvCancelReasonItemBinding.inflate(inflater, parent, false)
        return CancelReasonViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CancelReasonAdapter.CancelReasonViewHolder,
        position: Int
    ) {
        val reason = differ.currentList[position]
        holder.bind(reason)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onBtnClick(reason: String)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class CancelReasonViewHolder(val binding: RvCancelReasonItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(reason: Pair<String, Int>) {


            binding.tvReason.text = reason.first
            binding.tvQantity.text = reason.second.toString()

            binding.btnMore.setOnClickListener {
                click?.onBtnClick(reason.first)
            }
        }
    }


}