package com.example.tttn_electronicsstore_manager_admin_app.adapters.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMessageReceiveBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvMessageSendBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.Message

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    private val differCallback = object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {

            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {

            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)


    fun submitList(list: List<Message>) {
        differ.submitList(list)
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].customer==0) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_SENT) {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvMessageSendBinding.inflate(inflater, parent, false)
            return SentMessageViewHolder(binding)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RvMessageReceiveBinding.inflate(inflater, parent, false)
            return ReceivedMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = differ.currentList[position]
        if (holder is SentMessageViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class SentMessageViewHolder(val binding: RvMessageSendBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(message: Message) {
            binding.tvSend.text = message.text
        }
    }

    inner class ReceivedMessageViewHolder(val binding: RvMessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.tvSend.text = message.text
        }
    }
}