package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.adapters.CategoryEditDetailAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.AddDetailDialogBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentEditCategoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.CategoryViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditCategoryFragment : Fragment() {
    private lateinit var binding: FragmentEditCategoryBinding
    private lateinit var category: Category
    private lateinit var detailPickList: MutableList<String>
    private lateinit var idDetailList: MutableList<Int>
    private lateinit var deleteIdDetailList: MutableList<Int>
    private lateinit var addIdDetailList: MutableList<Int>
    private lateinit var detailAdapter: CategoryEditDetailAdapter
    private val detailViewModel by viewModels<DetailViewModel>()
    private val categoryViewModel by viewModels<CategoryViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            category = b.getSerializable("category") as Category
        }
        setUp()
        initAdapter()
        detailViewModel.getAll()
        eventManager()


        binding.btnSave.setOnClickListener {
            category.name = binding.etCategoryName.text.toString().trim()
            categoryViewModel.update(category, addIdDetailList, deleteIdDetailList)
        }

        binding.btnAddDetail.setOnClickListener {
            openAddDetailDialog()
        }
    }

    fun eventManager() {
        lifecycleScope.launch {
            detailViewModel.allDetail.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data != null) {
                            detailAdapter.differ.submitList(it.data)
//                            detailList.clear()
//                            detailList.addAll(it.data)
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
            categoryViewModel.updateCategory.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnSave.revertAnimation()
                        val toast =
                            Toast.makeText(requireContext(), "Lưu thành công", Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
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
        detailAdapter = CategoryEditDetailAdapter(idDetailList)
        binding.rvListDetail.adapter = detailAdapter
        binding.rvListDetail.layoutManager = GridLayoutManager(requireContext(), 2)
        detailAdapter.setOnItemClickListener(object :
            CategoryEditDetailAdapter.OnItemClickListener {
            override fun onCbClick(detail: Detail, isChecked: Boolean) {
                if (isChecked) {
                    deleteIdDetailList.remove(detail.id)
                    detailPickList.add(detail.name)

                    if (!idDetailList.contains(detail.id)) {
                        addIdDetailList.add(detail.id)
                    }
                } else {
                    deleteIdDetailList.add(detail.id)
                    detailPickList.remove(detail.name)
                    if (idDetailList.contains(detail.id)) {
                        addIdDetailList.remove(detail.id)
                    }
                }

                setTextPick()
            }

        })
    }

    private fun setUp() {
        detailPickList = mutableListOf()
        idDetailList = mutableListOf()
        deleteIdDetailList = mutableListOf()
        deleteIdDetailList.add(0)
        addIdDetailList = mutableListOf()
       addIdDetailList.add(0)
        for (detail in category.details) {
            idDetailList.add(detail.id)
            detailPickList.add(detail.name)
        }
        binding.etCategoryName.setText(category.name)
        setTextPick()

    }

    fun setTextPick() {
        var str = "Đã chọn: "
        for (detailName in detailPickList) {
            str += "${detailName}, "
        }
        binding.tvPickDetail.setText(str)
    }

    private fun openAddDetailDialog() {
        val addDetailDialogBinding: AddDetailDialogBinding =
            AddDetailDialogBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(activity).setView(addDetailDialogBinding.root)

            .setPositiveButton("Thêm") { dialog, which ->
                var check = 0
                val detailName = addDetailDialogBinding.etBrandName.text.toString().trim()
                for (detail in detailPickList) {
                    if (detail.equals(detailName)) {
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
}