package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.AdminManagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLyTaiKhoanNhanVienBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.immutableListOf

@AndroidEntryPoint
class QuanLyTaiKhoanNhanVienFragment : Fragment() {

    private lateinit var binding: FragmentQuanLyTaiKhoanNhanVienBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val adminManagerAdapter = AdminManagerAdapter()
    private val staffSearch = mutableListOf<User>()
    private val allStaffList = mutableListOf<User>()
    private val adminList = mutableListOf<User>()
    private val shipperList = mutableListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuanLyTaiKhoanNhanVienBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getAllStaff()
        eventManager()
        initAdapter()
        initSearch()
        initSpinner()

        binding.btnAdd.setOnClickListener {
            (activity as ManagerActivity).replaceFragment(AddStaffFragment())
        }
    }

    private fun initSpinner() {
        val items = listOf("Tất cả", "Nhân viên", "Shipper")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            items
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> {
                        adminManagerAdapter.differ.submitList(allStaffList)
                    }

                    1 -> {
                        adminManagerAdapter.differ.submitList(adminList)
                    }

                    2 -> {
                        adminManagerAdapter.differ.submitList(shipperList)
                    }

                    else -> {}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }


    }

    private fun eventManager() {
        lifecycleScope.launch {
            userViewModel.getAllStaff.collectLatest {
                when (it) {

                    is Resource.Success -> {

                        adminManagerAdapter.differ.submitList(it.data)
                        it.data?.let { it1 -> adminList.addAll(it1.filter { it.role.equals("admin") }) }
                        it.data?.let { it1 -> shipperList.addAll(it1.filter { it.role.equals("shipper") }) }
                        allStaffList.clear()
                        allStaffList.addAll(it.data!!)

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }

                    else -> {}
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvListAdmin.apply {
            adapter = adminManagerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }
        adminManagerAdapter.setOnItemClickListener(object :
            AdminManagerAdapter.OnItemClickListener {
            override fun onSwitchClick(user: User) {
                userViewModel.updateStaff(user)
            }

            override fun onItemClick(user: User) {
                val b = Bundle()
                b.putSerializable("user", user)
                val fragment = EditStaffFragment()
                fragment.arguments = b
                (activity as ManagerActivity).replaceFragment(fragment)
            }
        })
    }

    private fun initSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                staffSearch.clear()
                if (p0!!.equals("")) {
                    staffSearch.addAll(allStaffList)
                    adminManagerAdapter.notifyDataSetChanged()
                } else {
                    for (i in 0 until allStaffList.size)
                        if ((allStaffList.get(i).fullName + " " + allStaffList.get(i).username).contains(
                                p0,
                                true
                            )
                        ) {
                            staffSearch.add(allStaffList.get(i))
                        }
                }

                adminManagerAdapter.differ.submitList(staffSearch)
                adminManagerAdapter.notifyDataSetChanged()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }

}