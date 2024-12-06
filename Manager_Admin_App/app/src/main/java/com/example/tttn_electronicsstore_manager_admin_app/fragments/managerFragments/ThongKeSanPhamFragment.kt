package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductMostBoughtAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductMostReviewAdapter
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductReview
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductSale
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentThongKeSanPhamBinding
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class ThongKeSanPhamFragment : Fragment() {
    private lateinit var binding: FragmentThongKeSanPhamBinding
    private val productMostBoughtAdapter = ProductMostBoughtAdapter()
    private val productMostReviewAdapter = ProductMostReviewAdapter()
    private val productViewModel by viewModels<ProductViewModel>()
    private var duringDay = ""
    private var fromDay = ""
    private var toDay = ""
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]
    private var tvTieuDe=""
    private var duringDayTemp=Convert.dinhDangNgay(ngay,thang,nam)
    private var fromDayTemp=Convert.dinhDangNgay(ngay,thang,nam)
    private var toDayTemp=Convert.dinhDangNgay(ngay,thang,nam)
    private lateinit var fromDay2: String
    private lateinit var toDay2: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThongKeSanPhamBinding.inflate(layoutInflater)
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
        binding.layoutAnalystReview.visibility = View.GONE
        lifecycleScope.launch {
            productViewModel.mostBoughtProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data != null) {
                            setUpMostBought(it.data!!)
                            binding.layoutAnalyst.visibility = View.VISIBLE


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Không có dữ liệu lượt bán",
                                Toast.LENGTH_LONG
                            )
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


        lifecycleScope.launch {
            productViewModel.mostReviewProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data != null) {
                            setUpMostReview(it.data!!)
                            binding.layoutAnalystReview.visibility = View.VISIBLE


                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Không có dữ liệu về thống kê đánh giá",
                                Toast.LENGTH_LONG
                            )
                                .show()
                            binding.layoutAnalystReview.visibility = View.GONE

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
                    duringDay = Convert.dinhDangNgayAPI(day, month, year)
                    duringDayTemp=Convert.dinhDangNgay(day, month, year)
//                    ngay = day
//                    thang = month
//                    nam = year
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
                    fromDayTemp=Convert.dinhDangNgay(day, month, year)
//                    ngay = day
//                    thang = month
//                    nam = year
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
                    toDayTemp=Convert.dinhDangNgay(day, month, year)
//                    ngay = day
//                    thang = month
//                    nam = year
                }, nam, thang, ngay
            )
            dialog.show()
        }


        binding.btnAnalyst.setOnClickListener {
            if (binding.rdDuringDay.isChecked) {
                productViewModel.getMostBoughtProduct(duringDay, duringDay, 10)
                productViewModel.getMostReviewProduct(duringDay, duringDay, 10)
                fromDay2=duringDay
                toDay2=duringDay

                tvTieuDe="Thống kê sản phẩm \n trong ngày ${duringDayTemp}"
            } else {
                productViewModel.getMostBoughtProduct(fromDay, toDay, 10)
                productViewModel.getMostReviewProduct(fromDay, toDay, 10)
                fromDay2=fromDay
                toDay2=toDay
                tvTieuDe="Thống kê sản phẩm \n từ ${fromDayTemp} đến ${toDayTemp}"
            }
        }


    }

    private fun setUpMostReview(data: List<ProductReview>) {
        binding.tvTitle.text=tvTieuDe
        productMostReviewAdapter.differ.submitList(data.sortedWith(compareByDescending<ProductReview> { it.review5 }
            .thenByDescending { it.review4 }))
        binding.rvMostReview.apply {
            adapter = productMostReviewAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        productMostReviewAdapter.setOnItemClickListener(object :
            ProductMostReviewAdapter.OnItemClickListener {
            override fun onItemClick(product: ProductReview) {
                val b = Bundle()
                b.putInt("productId", product.productId)
                b.putString("fromDay", fromDay2)
                b.putString("toDay", toDay2)
                val fragment = TopReviewFragment()
                fragment.arguments = b
                (activity as ManagerActivity).replaceFragment(fragment)
            }
        })
    }

    private fun setUpMostBought(data: List<ProductSale>) {
        binding.tvTitle.text=tvTieuDe
        productMostBoughtAdapter.differ.submitList(data)
        binding.rvMostBought.apply {
            adapter = productMostBoughtAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        }


    }

}