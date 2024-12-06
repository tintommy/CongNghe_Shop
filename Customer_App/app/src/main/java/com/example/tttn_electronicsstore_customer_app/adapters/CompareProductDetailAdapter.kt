package com.example.tttn_electronicsstore_customer_app.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvCompareLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.RvProductDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.models.ProductDetail
import com.example.tttn_electronicsstore_customer_app.util.CompareValue

class CompareProductDetailAdapter :
    RecyclerView.Adapter<CompareProductDetailAdapter.ProductDetailViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<CompareValue>() {
        override fun areItemsTheSame(oldItem: CompareValue, newItem: CompareValue): Boolean {
            return oldItem.detailName == newItem.detailName
        }

        override fun areContentsTheSame(oldItem: CompareValue, newItem: CompareValue): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvCompareLayoutBinding.inflate(inflater, parent, false)
        return ProductDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductDetailViewHolder, position: Int) {
        val compareValue = differ.currentList[position]
        holder.bind(compareValue,position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    inner class ProductDetailViewHolder(val binding: RvCompareLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(compareValue: CompareValue,position: Int) {
         binding.apply {
             tvDetailName.text=compareValue.detailName
             tvValue1.text= compareValue.value1
             tvValue2.text=compareValue.value2

             if(position%2==0){
                 layout.setBackgroundColor(Color.parseColor("#DCDEFBFF"))
             }
             else{
                 layout.setBackgroundColor(Color.parseColor("#56FBF29C"))
             }
         }
        }
    }
}