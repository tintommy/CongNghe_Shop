package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.BrandManagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentEditBrandBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLiThuongHieuBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.BrandViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QuanLyThuongHieuFragment : Fragment() {


    private lateinit var binding: FragmentQuanLiThuongHieuBinding
    private val brandViewModel by viewModels<BrandViewModel>()
    private val brandManagerAdapter by lazy { BrandManagerAdapter() }
    private var allBrandList: MutableList<Brand> = mutableListOf()
    private var brandSearch: MutableList<Brand> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLiThuongHieuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initSearch()
        brandViewModel.getAllBrand()

        lifecycleScope.launch {
            brandViewModel.getAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        brandManagerAdapter.differ.submitList(it.data)
                        allBrandList.clear()
                        allBrandList.addAll(it.data!!)
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {

                    }
                }
            }
        }

        lifecycleScope.launch {
            brandViewModel.deleteBrand.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {

                        if (it.data == true) {
                            brandViewModel.getAllBrand()
                        } else {
                            val builder = AlertDialog.Builder(requireContext())
                            builder.setMessage("Không thể xoá thương hiệu này do có sản phẩm liên kết !!")
                            builder.setTitle("Thông báo !")
                            builder.setCancelable(false)
                            builder.setNegativeButton("OK") { dialog, which ->
                                dialog.cancel()
                            }
                            val alertDialog = builder.create()
                            alertDialog.show()
                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }

                    else -> {

                    }
                }
            }
        }



        binding.btnAdd.setOnClickListener {
            (activity as AdminActivity).replaceFragment(AddBrandFragment())
        }

    }

    private fun initSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                brandSearch.clear()
                if (p0!!.equals("")) {
                    brandSearch.addAll(allBrandList)
                } else {
                    for (i in 0 until allBrandList.size)
                        if (allBrandList.get(i).name.contains(p0, true)) {
                            brandSearch.add(allBrandList.get(i))
                        }
                }

                brandManagerAdapter.differ.submitList(brandSearch)
                brandManagerAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

    fun initAdapter() {
        binding.rvListBrand.adapter = brandManagerAdapter
        binding.rvListBrand.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        brandManagerAdapter.setOnItemClickListener(object :
            BrandManagerAdapter.OnItemClickListener {
            override fun onItemClick(brand: Brand) {
                val b = Bundle()
                b.putSerializable("brand", brand)
                val fragment = EditBrandFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }

            override fun onItemLongClick(brand: Brand) {
                openDeleteBrandNotify(brand)
            }

            override fun onSwitchClick(brand: Brand, isChecked: Boolean) {
                brand.status = isChecked
                brandViewModel.updateBrand(brand)
            }

        })
    }

    fun openDeleteBrandNotify(brand: Brand) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Xoá thương hiệu ${brand.name}  ?")
        builder.setTitle("XÁC NHẬN XOÁ !")
        builder.setCancelable(false)
        builder.setPositiveButton("Có") { dialog, which ->
            brandViewModel.deleteBrand(brand)
        }

        builder.setNegativeButton("Không") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}