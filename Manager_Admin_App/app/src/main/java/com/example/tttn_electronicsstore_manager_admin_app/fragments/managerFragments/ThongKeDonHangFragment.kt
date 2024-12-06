package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService.OrderApiService
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentThongKeDonHangBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.EditProductFragment
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels.ManagerOrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import ir.mahozad.android.PieChart
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayList
import java.util.Calendar

@AndroidEntryPoint
class ThongKeDonHangFragment : Fragment() {
    private lateinit var binding: FragmentThongKeDonHangBinding
    private var duringDay = ""
    private var fromDay = ""
    private var toDay = ""
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var tvTieuDe = ""
    private var duringDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private var fromDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private var toDayTemp = Convert.dinhDangNgay(ngay, thang, nam)
    private val orderViewModel by viewModels<ManagerOrderViewModel>()
    private var order: List<Order> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongKeDonHangBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDuringDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        binding.tvFromDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        binding.tvToDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        duringDay = Convert.dinhDangNgayAPI(ngay, thang, nam)
        fromDay = Convert.dinhDangNgayAPI(ngay, thang, nam)
        toDay = Convert.dinhDangNgayAPI(ngay, thang, nam)

        binding.layoutAnalyst.visibility = View.GONE




        lifecycleScope.launch {

            orderViewModel.orderByDate.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data != null) {
                            initPieChart(it.data!!)
                            binding.layoutAnalyst.visibility = View.VISIBLE
                            order = it.data

                        } else {
                            Toast.makeText(requireContext(), "Không có dữ liệu", Toast.LENGTH_LONG)
                                .show()
                            binding.layoutAnalyst.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()

                    }

                    else -> {}
                }
            }

        }






        binding.btnDuringDay.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(),
                { datePicker, year, month, day ->
                    binding.tvDuringDate.setText(Convert.dinhDangNgay(day, month, year))
                    duringDayTemp = Convert.dinhDangNgay(day, month, year)
                    duringDay = Convert.dinhDangNgayAPI(day, month, year)
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
                    fromDayTemp = Convert.dinhDangNgay(day, month, year)
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
                    toDayTemp = Convert.dinhDangNgay(day, month, year)
                    ngay = day
                    thang = month
                    nam = year
                }, nam, thang, ngay
            )
            dialog.show()
        }



        binding.btnAnalyst.setOnClickListener {
            if (binding.rdDuringDay.isChecked) {

                orderViewModel.getByDate(duringDay, duringDay)
                tvTieuDe = "Thống kê đơn hàng\n trong ngày ${duringDayTemp}"
            } else {
                orderViewModel.getByDate(fromDay, toDay)
                tvTieuDe = "Thống kê đơn hàng\n từ ngày ${fromDayTemp} đến ${toDayTemp}"
            }
        }


        binding.tvSuccess.setOnClickListener {
            val b = Bundle()
            b.putSerializable(
                "orderList",
                ArrayList(order.filter { it.status == 5 })
            )
            val fragment = ListOrderFragment()
            fragment.arguments = b
            (activity as ManagerActivity).replaceFragment(fragment)

        }
        binding.tvCancel.setOnClickListener {
            val b = Bundle()
            b.putSerializable(
                "orderList",
                ArrayList(order.filter { it.status == 0 })
            )
            val fragment = ListOrderFragment()
            fragment.arguments = b
            (activity as ManagerActivity).replaceFragment(fragment)
        }

        binding.tvShipFailed.setOnClickListener {
            val b = Bundle()
            b.putSerializable("orderList", ArrayList(order.filter { it.status == 6 }))
            val fragment = CancelReasonOrderManagerFragment()
            fragment.arguments = b
            (activity as ManagerActivity).replaceFragment(fragment)
        }
    }


    private fun initPieChart(orders: List<Order>) {

        binding.tvTime.text = tvTieuDe


        var success = 0
        var cancel = 0
        var shipFailed = 0
        var other = 0
        var total: BigInteger = BigInteger.valueOf(0)



        for (o in orders) {
            if (o.status == 0)
                cancel += 1
            else if (o.status == 5) {
                success += 1
                total += BigInteger.valueOf(o.total.toLong())
            } else if (o.status == 6) {
                shipFailed += 1
            } else
                other += 1


        }

        binding.pieChart.slices = listOf(
            PieChart.Slice(
                success.toFloat() / orders.size.toFloat(),
                Color.parseColor("#5bd1d7")
            ),
            PieChart.Slice(
                cancel.toFloat() / orders.size.toFloat(),

                Color.parseColor("#b00b1e")
            ), PieChart.Slice(
                shipFailed.toFloat() / orders.size.toFloat(),
                Color.parseColor("#FF5722")
            ),
            PieChart.Slice(
                other.toFloat() / orders.size.toFloat(),
                Color.parseColor("#000000")
            )
        )


        binding.apply {
            tvTotalOrder.setText("Tổng số đơn : ${orders.size}")
            tvSuccess.setText("Số đơn hàng thành công : ${success}")
            tvCancel.setText("Số đơn hàng đã huỷ : ${cancel}")
            tvShipFailed.setText("Số đơn hàng giao thất bại : ${shipFailed}")
            tvOther.setText("Khác : ${other}")
            tvTotal.setText(Convert.formatBigIntegerWithThousandSeparators(total) + "đ")
        }
    }
}