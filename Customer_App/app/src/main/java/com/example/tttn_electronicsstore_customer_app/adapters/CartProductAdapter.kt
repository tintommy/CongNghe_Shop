package com.example.tttn_electronicsstore_customer_app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.tttn_electronicsstore_customer_app.databinding.RvCartItemBinding
import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.helper.Convert

class CartProductAdapter :
    RecyclerView.Adapter<CartProductAdapter.CartProductViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<Cart>() {
        override fun areItemsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cart, newItem: Cart): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvCartItemBinding.inflate(inflater, parent, false)
        return CartProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartProductViewHolder, position: Int) {
        val cart = differ.currentList[position]
        holder.bind(cart)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun removeItem(cart: Cart) {
        val currentList = differ.currentList.toMutableList()
        currentList.remove(cart)
        differ.submitList(currentList)
    }

    interface OnItemClickListener {
        fun onItemClick(cart: Cart)
        fun onCbClick(cart: Cart, isChecked: Boolean)
        fun onBtnDeleteClick(cart: Cart)
        fun onBtnMinusClick(cart: Cart)
        fun onBtnAddClick(cart: Cart)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class CartProductViewHolder(val binding: RvCartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            for (image in cart.imageList)
                if (image.avatar) {
                    Glide.with(itemView).load(image.link).apply(
                        RequestOptions().transform(RoundedCorners(16))
                    ).into(binding.ivPhoto)
                }

            binding.tvProductName.text = cart.productName
            binding.tvProductPrice.text =
                Convert.formatNumberWithDotSeparator(cart.productPrice) + "đ"

            if(cart.productStatus==false){
                binding.tvProductPrice.text ="Ngừng kinh doanh"
            }

            binding.etNumber.setText(cart.quantity.toString())
            if (cart.status) {
                binding.cbCheck.isChecked = true
            } else {
                binding.cbCheck.isChecked = false
            }


            binding.cbCheck.setOnCheckedChangeListener { _, ischecked ->

                click?.onCbClick(cart, ischecked)
                cart.status = ischecked

            }

            binding.btnDelete.setOnClickListener {
                click?.onBtnDeleteClick(cart)
            }


            var number = cart.quantity
            binding.btnMinus.setOnClickListener {

                if (binding.etNumber.text.toString().toInt() <= 1) {
                    binding.btnMinus.isEnabled = false

                } else {
                    number = binding.etNumber.text.toString().toInt()
                    number -= 1
                    binding.etNumber.setText(number.toString())
                    click?.onBtnMinusClick(cart)
                }
                cart.quantity = binding.etNumber.text.toString().toInt()

            }

            binding.btnPlus.setOnClickListener {
                binding.btnMinus.isEnabled = true

                number = binding.etNumber.text.toString().toInt()
                number += 1
                binding.etNumber.setText(number.toString())
                click?.onBtnAddClick(cart)
                cart.quantity = binding.etNumber.text.toString().toInt()
            }

            itemView.setOnClickListener {
                click?.onItemClick(cart)
            }
        }
    }
}