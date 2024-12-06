package com.example.tttn_electronicsstore_customer_app.adapters

import android.R
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_customer_app.databinding.RvDetailSearchLayoutBinding
import com.example.tttn_electronicsstore_customer_app.models.Detail

class SearchDetailProductAdapter(val context: Context) :
    RecyclerView.Adapter<SearchDetailProductAdapter.DetailAddProductAdapterViewHolder>() {
    val myDetailMap: MutableMap<Int, String> = mutableMapOf()
    val myPriorityMap: MutableMap<Int, String> = mutableMapOf()

    private val callback = object : DiffUtil.ItemCallback<Detail>() {
        override fun areItemsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Detail, newItem: Detail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailAddProductAdapterViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvDetailSearchLayoutBinding.inflate(inflater, parent, false)
        return DetailAddProductAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailAddProductAdapterViewHolder, position: Int) {
        val detail = differ.currentList[position]
        holder.bind(detail)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun clearDetailValueMap() {
        myDetailMap.clear()
    }

    fun getDetailValueMap(): Map<Int, String> {
        return myDetailMap
    }
    fun getPriorityMap(): Map<Int, String> {
        return myPriorityMap
    }


    interface OnItemClickListener {
        fun onCbClick(detail: Detail, isChecked: Boolean)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class DetailAddProductAdapterViewHolder(val binding: RvDetailSearchLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detail: Detail) {
            initSp(binding, detail)

            binding.tvDetailName.text = detail.name

            binding.etValue.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(p0: Editable?) {

                    if (binding.cbCheck.isChecked) {
                        if (!p0.toString().equals(""))
                            myDetailMap[detail.id] = p0.toString()
                    }
                }
            })


            binding.cbCheck.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!binding.etValue.text.toString().equals(""))
                        myDetailMap[detail.id] = binding.etValue.text.toString()


                } else {
                    myDetailMap.remove(detail.id)
                    myPriorityMap.remove(detail.id)
                }
            }
        }


    }

    private fun initSp(binding: RvDetailSearchLayoutBinding, detail: Detail) {
        val numberList: List<String> = listOf("Cao", "Vừa", "Thấp")
        val adapter = ArrayAdapter(
            context, // context
            R.layout.simple_spinner_item,
            numberList.map { it }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spPriority.adapter = adapter


        binding.spPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Lấy giá trị được chọn
//                val selectedItem = parent.getItemAtPosition(position).toString()
//
//                // Xử lý sự kiện khi chọn item
//                Log.d("Spinner", "Selected: $selectedItem")
//

                if (parent.getItemAtPosition(position).toString().equals("Cao")) {
                    myPriorityMap[detail.id] = "3"
                }
                if (parent.getItemAtPosition(position).toString().equals("Vừa")) {
                    myPriorityMap[detail.id] = "2"
                }
                if (parent.getItemAtPosition(position).toString().equals("Thấp")) {
                    myPriorityMap[detail.id] = "1"
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }
}