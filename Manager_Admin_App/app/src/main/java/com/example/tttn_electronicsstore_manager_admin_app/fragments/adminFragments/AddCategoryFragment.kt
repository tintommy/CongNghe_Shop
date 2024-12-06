package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.DetailAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.AddDetailDialogBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentAddCategoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.CategoryViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCategoryFragment : Fragment() {

    private lateinit var binding: FragmentAddCategoryBinding
    private val detailViewModel by viewModels<DetailViewModel>()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val detailList: MutableList<Detail> = mutableListOf()
    private val detailPickList: MutableList<String> = mutableListOf()
    private val idDetailPickList: MutableList<Int> = mutableListOf()
    private val detailAdapter by lazy { DetailAdapter() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        detailViewModel.getAll()
        eventManager()

        binding.btnSave.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()
            categoryViewModel.add(categoryName, idDetailPickList)
        }

        binding.btnAddDetail.setOnClickListener {
            openAddDetailDialog()
        }

    }


    private fun eventManager() {
        lifecycleScope.launch {
            detailViewModel.allDetail.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data != null) {
                            detailAdapter.differ.submitList(it.data)
                            detailList.clear()
                            detailList.addAll(it.data)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            categoryViewModel.addCategory.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {

                        if (it.data != null) {
                            val toast = Toast.makeText(
                                requireContext(), "Thêm loại ${it.data.name} thành công",
                                Toast.LENGTH_LONG
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            (activity as AdminActivity).supportFragmentManager.popBackStack()
                        }


                    }

                    is Resource.Error -> {
                        binding.btnSave.revertAnimation()
                        val toast = Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                    else -> {}
                }

            }

        }


        lifecycleScope.launch {
            detailViewModel.addDetail.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {

                        detailViewModel.getAll()


                    }

                    is Resource.Error -> {
                        val toast = Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                    else -> {}
                }

            }

        }
    }

    private fun initAdapter() {
        binding.rvListDetail.adapter = detailAdapter
        binding.rvListDetail.layoutManager = GridLayoutManager(requireContext(), 2)
        detailAdapter.setOnItemClickListener(object : DetailAdapter.OnItemClickListener {
            override fun onCbClick(detail: Detail, isChecked: Boolean) {
                if (isChecked) {
                    detailPickList.add(detail.name)
                    idDetailPickList.add(detail.id)
                    setStringPickDetail()
                } else {
                    detailPickList.remove(detail.name)
                    idDetailPickList.remove(detail.id)
                    setStringPickDetail()
                }
            }

        })
    }

    private fun openAddDetailDialog() {
        val addDetailDialogBinding: AddDetailDialogBinding =
            AddDetailDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(activity).setView(addDetailDialogBinding.root)

            .setPositiveButton("Thêm") { dialog, which ->
                var check = 0
                val detailName = addDetailDialogBinding.etBrandName.text.toString().trim()
                for (detail in detailList) {
                    if (detail.name.equals(detailName)) {
                        val toast = Toast.makeText(
                            requireContext(),
                            "Thông số này đã tồn tại",
                            Toast.LENGTH_LONG
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                        check = 1
                    }

                }
                if (check == 0) {
                    detailViewModel.add(detailName)
                    dialog.dismiss()
                }
            }.setNegativeButton("Huỷ") { dialog, which ->
                dialog.dismiss()
            }

        val mDialog = builder.create()
        mDialog.window?.setBackgroundDrawableResource(R.drawable.white_layout_rounded_corners)

        mDialog.show()

    }


    private fun setStringPickDetail() {
        var str = "Đã chọn: "
        for (i in 0 until detailPickList.size) {
            str += detailPickList.get(i) + ", "
        }
        binding.tvPickDetail.text = str
    }
}