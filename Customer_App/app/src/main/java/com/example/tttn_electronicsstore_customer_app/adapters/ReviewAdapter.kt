package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvCategoryItemBinding
import com.example.tttn_electronicsstore_customer_app.databinding.RvListReviewLayoutBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.models.Review

class ReviewAdapter  :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReviewViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListReviewLayoutBinding.inflate(inflater, parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onItemClick(category: Category)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class ReviewViewHolder(val binding: RvListReviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
           binding.tvFullName.text= review.userFullName
            binding.tvStar.text= review.value.toString()
            binding.tvDate.text= Convert.formatDate(review.date.substring(0,10))
             binding.tvContent.text= review.content
        }
    }
}