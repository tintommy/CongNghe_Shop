package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.databinding.RvListOderDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.databinding.RvListSuccessOderDetailItemBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.Order
import com.example.tttn_electronicsstore_customer_app.models.OrderDetail

class SuccessOrderDetailAdapter : RecyclerView.Adapter<SuccessOrderDetailAdapter.OrderDetailViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<OrderDetail>() {
        override fun areItemsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem.toString() == newItem.toString()
        }

        override fun areContentsTheSame(oldItem: OrderDetail, newItem: OrderDetail): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvListSuccessOderDetailItemBinding.inflate(inflater, parent, false)
        return OrderDetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        val orderDetail = differ.currentList[position]
        holder.bind(orderDetail,position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onBtnClick(orderDetail: OrderDetail, star:Int,content:String)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }


    inner class OrderDetailViewHolder(val binding: RvListSuccessOderDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(orderDetail: OrderDetail, position: Int) {
            binding.apply {
                tvProductName.text = orderDetail.productName
                tvProductPrice.text = Convert.formatNumberWithDotSeparator(orderDetail.price)+"đ"
                tvProductQuantity.text = "Số lượng: " + orderDetail.quantity.toString()
                for (image in orderDetail.productImageList) {
                    if (image.avatar)
                        Glide.with(itemView).load(image.link).into(ivProductAvatar)
                }
            }


            if(!orderDetail.reviewStatus){
                binding.layoutCmt.visibility= View.VISIBLE
                binding.tvSend.visibility=View.GONE
                binding.btnSubmit.setOnClickListener {
                    val star= binding.ratingStar.rating
                    val content= binding.etCmt.text.toString().trim()
                    click?.onBtnClick(orderDetail,star.toInt(),content)
                    binding.layoutCmt.visibility= View.GONE
                    binding.tvSend.visibility=View.VISIBLE
                    orderDetail.reviewStatus=true
                    notifyItemChanged(position)
                }
            }
            else{
                binding.layoutCmt.visibility= View.GONE
                binding.tvSend.visibility=View.VISIBLE
            }

        }
    }


}