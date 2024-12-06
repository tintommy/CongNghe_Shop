package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListCategoryManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail

class CategoryManagerAdapter :
    RecyclerView.Adapter<CategoryManagerAdapter.CategoryManagerAdapterViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryManagerAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListCategoryManagerItemBinding.inflate(inflater, parent, false)
        return CategoryManagerAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryManagerAdapterViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category, position)
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
        fun onItemClick(category: Category)
        fun onItemLongClick(category: Category, position: Int)
    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class CategoryManagerAdapterViewHolder(val binding: RvListCategoryManagerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            binding.tvCategoryName.text = category.name
            itemView.setOnClickListener {
                click?.onItemClick(category)
            }

            itemView.setOnLongClickListener {
                click?.onItemLongClick(category, position)
                true
            }
        }
    }
}