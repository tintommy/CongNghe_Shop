package com.example.tttn_electronicsstore_manager_admin_app.adapters.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMessageNotSeenBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMessageSeenBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.History
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SEEN = 1
        const val VIEW_TYPE_NOT_SEEN = 0
    }

    private val differCallback = object : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {

            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {

            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)


    fun submitList(list: List<History>) {
        differ.submitList(list)
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].seen==1) {
            VIEW_TYPE_SEEN
        } else {
            VIEW_TYPE_NOT_SEEN
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SEEN) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvMessageSeenBinding.inflate(inflater, parent, false)
            return MessageSeenViewHolder(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvMessageNotSeenBinding.inflate(inflater, parent, false)
            return MessageNotSeenViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val history = differ.currentList[position]
        if (holder is MessageSeenViewHolder) {
            holder.bind(history)
        } else if (holder is MessageNotSeenViewHolder) {
            holder.bind(history)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }




    interface OnItemClickListener {
        fun onItemClick(history: History)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class MessageSeenViewHolder(val binding: RvMessageSeenBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(history: History) {
            binding.tvUsername.text = history.username
            binding.tvTime.text = history.time
            binding.tvDate.text=formatDate(history.date)
            itemView.setOnClickListener {
                click?.onItemClick(history)
            }
        }
    }

    inner class MessageNotSeenViewHolder(val binding: RvMessageNotSeenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(history: History) {
            binding.tvUsername.text = history.username
            binding.tvTime.text = history.time
            binding.tvDate.text=formatDate(history.date)

            itemView.setOnClickListener {
                click?.onItemClick(history)
            }
        }
    }
    fun formatDate(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val date = inputFormat.parse(input)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Invalid Date"
        }
    }
}