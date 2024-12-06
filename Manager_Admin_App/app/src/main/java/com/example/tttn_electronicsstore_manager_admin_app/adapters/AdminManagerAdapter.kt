package com.example.tttn_electronicsstore_manager_admin_app.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListCategoryManagerItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvListDetailPickItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.RvStaffManageItemBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.models.User

class AdminManagerAdapter :
    RecyclerView.Adapter<AdminManagerAdapter.AdminManagerViewHolder>() {


    private val callback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, callback)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminManagerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvStaffManageItemBinding.inflate(inflater, parent, false)
        return AdminManagerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdminManagerViewHolder, position: Int) {
        val category = differ.currentList[position]
        holder.bind(category, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    interface OnItemClickListener {
        fun onSwitchClick(user: User)
        fun onItemClick(user: User)

    }

    var click: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        click = listener
    }

    inner class AdminManagerViewHolder(val binding: RvStaffManageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User, position: Int) {
            binding.tvAdminName.text = user.fullName
            binding.tvAdminEmail.text = user.email


            binding.switchStatus.setOnCheckedChangeListener(null)
            if (user.active) {
                binding.switchStatus.isChecked = true
                binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.GREEN)
                binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.GREEN)
            } else {
                binding.switchStatus.isChecked = false
                binding.switchStatus.thumbTintList = ColorStateList.valueOf(Color.RED)
                binding.switchStatus.trackTintList = ColorStateList.valueOf(Color.RED)
            }

            if (!user.role.equals("customer"))
            {
                binding.layoutRole.visibility=View.VISIBLE
                when (user.role) {
                    "admin" -> {
                        binding.tvRole.text = "Nhân viên"
                    }

                    "shipper" -> {
                        binding.tvRole.text = "Shipper"
                    }
                }}
            else{
                binding.layoutRole.visibility=View.GONE
            }



            binding.switchStatus.setOnCheckedChangeListener { _, isChecked ->

                user.active = isChecked
                notifyItemChanged(position)
                click?.onSwitchClick(user)

            }

            itemView.setOnClickListener {
                click?.onItemClick(user)
            }

        }
    }
}