package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.CategoryManagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLyLoaiBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuanLyLoaiFragment : Fragment() {
    private lateinit var binding: FragmentQuanLyLoaiBinding
    private val categoryViewModel by viewModels<CategoryViewModel>()
    private val categoryAdapter by lazy { CategoryManagerAdapter() }

    private var allCategoryList: MutableList<Category> = mutableListOf()
    private var categorySearch: MutableList<Category> = mutableListOf()
    private var deleteCategory: Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLyLoaiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSearch()
        categoryViewModel.getAll()

        lifecycleScope.launch {
            categoryViewModel.allCategoty.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        categoryAdapter.differ.submitList(it.data)
                        allCategoryList.clear()
                        allCategoryList.addAll(it.data!!)

                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }


        lifecycleScope.launch {
            categoryViewModel.deleteCategory.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data == false) {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setMessage("Không thể xoá loại do có sản phẩm liên kết!!!")
                            builder.setTitle("Thông báo !")
                            builder.setCancelable(false)
                            builder.setPositiveButton("OK") { dialog, which ->
                                dialog.cancel()
                            }


                            val alertDialog = builder.create()
                            alertDialog.show()
                        } else {
                            categoryAdapter.deleteItem(deleteCategory)
                            deleteCategory = -1
                        }
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            (activity as AdminActivity).replaceFragment(AddCategoryFragment())
        }
    }

    private fun initAdapter() {
        binding.rvListCategory.adapter = categoryAdapter
        binding.rvListCategory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        categoryAdapter.setOnItemClickListener(object : CategoryManagerAdapter.OnItemClickListener {
            override fun onItemClick(category: Category) {
                val b = Bundle()
                b.putSerializable("category", category)
                val editFragment = EditCategoryFragment()
                editFragment.arguments = b
                (activity as AdminActivity).replaceFragment(editFragment)
            }

            override fun onItemLongClick(category: Category, position: Int) {
                openDeleteCategoryNotify(category, position)

            }
        })
    }

    private fun initSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                categorySearch.clear()
                if (p0!!.equals("")) {
                    categorySearch.addAll(allCategoryList)
                } else {
                    for (i in 0 until allCategoryList.size)
                        if (allCategoryList.get(i).name.contains(p0, true)) {
                            categorySearch.add(allCategoryList.get(i))
                        }
                }

                categoryAdapter.differ.submitList(categorySearch)
                categoryAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    fun openDeleteCategoryNotify(category: Category, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Xoá loại ${category.name} ")
        builder.setTitle("XÁC NHẬN XOÁ !")
        builder.setCancelable(false)
        builder.setPositiveButton("Có") { dialog, which ->
            categoryViewModel.delete(category.id)
            deleteCategory = position
        }

        builder.setNegativeButton("Không") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}