package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.adapters.DetailImportReceiptAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentCreateImportReceiptBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentDetailImportReceiptBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ImportReceiptViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels.ManagerImportReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailImportReceiptFragment : Fragment() {
    private lateinit var binding: FragmentDetailImportReceiptBinding
    private lateinit var receipt: ImportReceipt
    private val importReceiptViewModel by viewModels<ManagerImportReceiptViewModel>()
    private lateinit var detailImportReceiptAdapter: DetailImportReceiptAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailImportReceiptBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            receipt = b.getSerializable("receipt") as ImportReceipt
            setUpInfo()
            importReceiptViewModel.getDetailById(receipt.id)
            eventManager()
            initAdapter()
        }
    }

    private fun initAdapter() {
        detailImportReceiptAdapter = DetailImportReceiptAdapter()
        binding.apply {
            rvProducts.adapter = detailImportReceiptAdapter
            rvProducts.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun eventManager() {
        lifecycleScope.launch {
            importReceiptViewModel.detailReceipt.collectLatest {
                when (it) {
                    is Resource.Success -> { detailImportReceiptAdapter.differ.submitList(it.data)}
                    is Resource.Loading -> {


                    }
                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }
    }

    private fun setUpInfo() {
        binding.apply {
            tvReceiptId.text = receipt.id.toString()
            tvCreatedDate.text = Convert.formatDate(receipt.createdDate.substring(0, 10))
            tvCreatedAdmin.text = receipt.staffName
            tvTotal.text = Convert.formatLongNumberWithDotSeparator(receipt.total) + "đ"
        }
    }
}