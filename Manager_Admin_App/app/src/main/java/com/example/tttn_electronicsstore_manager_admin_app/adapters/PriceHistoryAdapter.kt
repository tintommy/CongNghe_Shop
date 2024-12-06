package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvPriceHistoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.OrderDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.PriceHistory

class PriceHistoryAdapter:  RecyclerView.Adapter<PriceHistoryAdapter.PriceHistoryViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<PriceHistory>() {
        override fun areItemsTheSame(oldItem: PriceHistory, newItem: PriceHistory): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: PriceHistory, newItem: PriceHistory): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceHistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvPriceHistoryBinding.inflate(inflater, parent, false)
        return PriceHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PriceHistoryViewHolder, position: Int) {
        val priceHistory = differ.currentList[position]
        holder.bind(priceHistory)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {


    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class PriceHistoryViewHolder(val binding: RvPriceHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(priceHistory: PriceHistory) {
           binding.apply {
               tvDate.text= priceHistory.createAt.substring(0,10)
               tvPrice.text= Convert.formatNumberWithDotSeparator(priceHistory.price)+"đ"
           }

        }
    }
}