package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentQuanLyPhieuNhapBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuanLyPhieuNhapFragment : Fragment() {

 lateinit var binding:FragmentQuanLyPhieuNhapBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding= FragmentQuanLyPhieuNhapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddNewReceipt.setOnClickListener {
            (activity as AdminActivity).replaceFragment(CreateImportReceiptFragment())
        }

        binding.btnReceiptHistory.setOnClickListener {
            (activity as AdminActivity).replaceFragment(ImportReceiptHistoryFragment())
        }

    }
}