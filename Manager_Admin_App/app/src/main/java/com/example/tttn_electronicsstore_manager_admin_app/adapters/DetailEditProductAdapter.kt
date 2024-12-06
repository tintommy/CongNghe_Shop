package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListAddDetailProductItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.ProductDetail

class DetailEditProductAdapter: RecyclerView.Adapter<DetailEditProductAdapter.DetailEditProductViewHolder>() {
    private val myDetailMap:MutableMap<Int,String> = mutableMapOf()

    private val callback = object : DiffUtil.ItemCallback<ProductDetail>() {
        override fun areItemsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductDetail, newItem: ProductDetail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailEditProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListAddDetailProductItemBinding.inflate(inflater, parent, false)
        return DetailEditProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailEditProductViewHolder, position: Int) {
        val detail = differ.currentList[position]
        holder.bind(detail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun clearDetailValueMap(){
        myDetailMap.clear()
    }
    fun getDetailValueMap():Map<Int,String>{
        return myDetailMap
    }


    inner class DetailEditProductViewHolder(val binding: RvListAddDetailProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: ProductDetail) {
          binding.tvDetailName.text= detail.detailName
            binding.etDetailValue.setText(detail.value)
            myDetailMap[detail.detailId]= detail.value

            binding.etDetailValue.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                  myDetailMap[detail.detailId]= p0.toString()

                }
            })
        }
    }
}