package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListAddDetailProductItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail

class DetailAddProductAdapter: RecyclerView.Adapter<DetailAddProductAdapter.DetailAddProductAdapterViewHolder>() {
    val myDetailMap:MutableMap<Int,String> = mutableMapOf()

    private val callback = object : DiffUtil.ItemCallback<Detail>() {
        override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailAddProductAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListAddDetailProductItemBinding.inflate(inflater, parent, false)
        return DetailAddProductAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailAddProductAdapterViewHolder, position: Int) {
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


    inner class DetailAddProductAdapterViewHolder(val binding: RvListAddDetailProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: Detail) {
          binding.tvDetailName.text= detail.name

            binding.etDetailValue.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {
                  myDetailMap[detail.id]= p0.toString()
                }
            })
        }
    }
}