package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.AdminManagerAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLyKhachHangBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments.EditStaffFragment
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class QuanLyKhachHangFragment : Fragment() {

    private lateinit var binding:FragmentQuanLyKhachHangBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val adminManagerAdapter = AdminManagerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding= FragmentQuanLyKhachHangBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel.getUserByRole("customer")
        eventManager()
        initAdapter()

    }
    private fun eventManager() {
        lifecycleScope.launch {
            userViewModel.userByRole.collectLatest {
                when (it) {

                    is Resource.Success -> {
                        adminManagerAdapter.differ.submitList(it.data)
                      //  allStaffList.addAll(it.data!!)
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
//                val b= Bundle()
//                b.putSerializable("user",user)
//                val fragment= EditStaffFragment()
//                fragment.arguments=b
//                (activity as ManagerActivity).replaceFragment(fragment)
            }
        })
    }
}