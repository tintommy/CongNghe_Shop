package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ImportReceiptAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentImportReceiptHistoryBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ImportReceiptViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ImportReceiptHistoryFragment : Fragment() {


    private var duringDay = ""
    private var fromDay = ""
    private var toDay = ""
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var duringDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private var fromDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private var toDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private var tvTieuDe = ""
    private lateinit var binding: FragmentImportReceiptHistoryBinding
    private val importReceiptViewModel by viewModels<ImportReceiptViewModel>()
    private lateinit var importReceiptAdapter: ImportReceiptAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImportReceiptHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRvAdapter()
        binding.tvDuringDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        binding.tvFromDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        binding.tvToDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        duringDay = Convert.dinhDangNgayAPI(ngay, thang, nam)
        fromDay = Convert.dinhDangNgayAPI(ngay, thang, nam)
        toDay = Convert.dinhDangNgayAPI(ngay, thang, nam)



        binding.btnDuringDay.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, day ->
                    binding.tvDuringDate.setText(Convert.dinhDangNgay(day, month, year))
                    duringDay = Convert.dinhDangNgayAPI(day, month, year)
                    duringDayTemp= Convert.dinhDangNgay(day, month, year)
                    ngay = day
                    thang = month
                    nam = year
                }, nam, thang, ngay
            )
            dialog.show()
        }

        binding.btnFromDate.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, day ->
                    binding.tvFromDate.setText(Convert.dinhDangNgay(day, month, year))
                    fromDay = Convert.dinhDangNgayAPI(day, month, year)
                    fromDayTemp= Convert.dinhDangNgay(day, month, year)
                    ngay = day
                    thang = month
                    nam = year
                }, nam, thang, ngay
            )
            dialog.show()
        }

        binding.btnToDate.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, day ->
                    binding.tvToDate.setText(Convert.dinhDangNgay(day, month, year))
                    toDay = Convert.dinhDangNgayAPI(day, month, year)
                    toDayTemp= Convert.dinhDangNgay(day, month, year)
                    ngay = day
                    thang = month
                    nam = year
                }, nam, thang, ngay
            )
            dialog.show()
        }


        binding.btnGetHistory.setOnClickListener {
            if (binding.rdDuringDay.isChecked) {
                importReceiptViewModel.getByDate(duringDay, duringDay)
                tvTieuDe = "Danh sách phiếu nhập\n trong ngày ${duringDayTemp}"
            } else {
                importReceiptViewModel.getByDate(fromDay, toDay)
                tvTieuDe = "Danh sách phiếu nhập\n từ ngày ${fromDayTemp} đến ngày ${toDayTemp}"
            }

        }


        lifecycleScope.launch {
            importReceiptViewModel.receiptByDate.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            tvNoReceipt.visibility = View.GONE
                            rvListReceipt.visibility = View.GONE
                        }

                    }

                    is Resource.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            if (it.data==null) {
                                tvNoReceipt.visibility = View.VISIBLE
                                binding.tvTitle.visibility = View.GONE
                            } else {

                                binding.tvTitle.text = tvTieuDe
                                binding.tvTitle.visibility = View.VISIBLE
                                importReceiptAdapter.differ.submitList(it.data)
                                rvListReceipt.visibility = View.VISIBLE


                            }


                        }
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }
    }

    private fun initRvAdapter() {
        importReceiptAdapter = ImportReceiptAdapter()
        binding.apply {
            rvListReceipt.adapter = importReceiptAdapter
            rvListReceipt.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        }
        importReceiptAdapter.setOnItemClickListener(object : ImportReceiptAdapter.OnItemClickListener{
            override fun onItemClick(importReceipt: ImportReceipt) {
                val b = Bundle()
                b.putSerializable("receipt", importReceipt)
                val fragment = DetailImportReceiptFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }

        })
    }

}