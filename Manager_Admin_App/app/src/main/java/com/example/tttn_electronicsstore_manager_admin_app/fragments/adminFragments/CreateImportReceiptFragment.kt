package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductImportAdapter
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductImportSearchAdpater
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ProductSearchAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.DialogSearchProductLayoutBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentCreateImportReceiptBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.request.AddImportReceiptRequest
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ImportReceiptViewModel
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class CreateImportReceiptFragment : Fragment() {
    lateinit var binding: FragmentCreateImportReceiptBinding
    private val userViewModel by viewModels<UserViewModel>()
    private val productViewModel by viewModels<ProductViewModel>()
    private val importReceiptViewModel by viewModels<ImportReceiptViewModel>()

    private lateinit var user: User
    private val productSearchAdapter = ProductImportSearchAdpater()
    private val productImportAdapter = ProductImportAdapter()
    private val calendar = Calendar.getInstance()
    private var nam = calendar[Calendar.YEAR]
    private var thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
    private var ngay = calendar[Calendar.DAY_OF_MONTH]

    private val productIdList: MutableList<Int> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateImportReceiptBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
    }

    private fun setUp() {
        userViewModel.getUser()
        binding.tvCreatedDate.text = Convert.dinhDangNgay(ngay, thang, nam)
        initAdapter()
        btnEvent()

        lifecycleScope.launch {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        user = it.data!!
                        binding.tvCreatedAdmin.text = it.data!!.fullName

                    }

                    else -> {}
                }
            }

        }

        lifecycleScope.launch { importReceiptViewModel.addReceipt.collectLatest {
            when(it){
                is Resource.Loading->{
                    binding.btnSubmit.startAnimation()

                }
                is Resource.Success->{
                    binding.btnSubmit.revertAnimation()
                    Toast.makeText(requireContext(), "Thêm phiếu nhập thành công", Toast.LENGTH_SHORT).show()
                    (activity as AdminActivity).supportFragmentManager.popBackStack()
                }
                is Resource.Error->{
                    Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                }
                else->{}
            }
        } }


    }

    private fun initAdapter() {
        binding.rvProducts.adapter = productImportAdapter
        binding.rvProducts.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        productImportAdapter.setOnItemClickListener(object : ProductImportAdapter.OnItemClickListener{
            override fun onDeleteBtnClick(importReceiptDetail: ImportReceiptDetail) {
               productIdList.remove(importReceiptDetail.productId)
            }

            override fun calculateTotal(total: Long) {
               binding.tvTotal.setText(Convert.formatLongNumberWithDotSeparator(total)+"đ")
            }
        })
    }

    private fun btnEvent() {
        binding.btnExistProduct.setOnClickListener {
            openSearchDialog()
        }

        binding.btnNewProduct.setOnClickListener {
            val fragment= AddProductFragment()
            (activity as AdminActivity).replaceFragment(fragment)
        }


        binding.btnSubmit.setOnClickListener {

            if(productIdList.isEmpty()){
                Toast.makeText(requireContext(), "Phiếu nhập chưa có sản phẩm", Toast.LENGTH_SHORT).show()
            }
            else{
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Xác nhận tạo phiếu nhập ?")
            builder.setCancelable(false)
            builder.setPositiveButton("Xác nhận") { dialog, which ->
                val importReceipt = ImportReceipt(0, user.username, "", "", productImportAdapter.getTotal())
                val addImportReceiptRequest =
                    AddImportReceiptRequest(importReceipt, productImportAdapter.getDetailList())
                 importReceiptViewModel.addImportReceipt(addImportReceiptRequest)
                Log.e("Receipt", productImportAdapter.getDetailList().size.toString())
                Log.e("Receipt", productImportAdapter.getDetailList().toString())
            }

            builder.setNegativeButton("Huỷ") { dialog, which ->
                dialog.cancel()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }}


    private fun openSearchDialog() {
        val dialogBinding: DialogSearchProductLayoutBinding =
            DialogSearchProductLayoutBinding.inflate(layoutInflater)
        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialogBinding.apply {
            rvProduct.adapter = productSearchAdapter
            rvProduct.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            productSearchAdapter.setOnItemClickListener(object :
                ProductImportSearchAdpater.OnItemClickListener {
                override fun onItemClick(productSearch: Product) {

                    if (productIdList.contains(productSearch.id)) {
                        Toast.makeText(
                            requireContext(),
                            "Đã có sản phẩm ${productSearch.name}",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        val importReceiptDetail: ImportReceiptDetail = ImportReceiptDetail(
                            0,
                            0,
                            productSearch.id,
                            productSearch.name,
                            productSearch.imageList,
                            1,
                            0
                        )

                        productImportAdapter.addDetail(importReceiptDetail)
                        Toast.makeText(
                            requireContext(),
                            "Đã thêm sản phẩm ${productSearch.name}",
                            Toast.LENGTH_LONG
                        ).show()

                        productIdList.add(productSearch.id)
                    }


                }
            })
        }
        initSearch(dialogBinding)
        mDialog.show()

        dialogBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }

    }

    private fun initSearch(dialogBinding: DialogSearchProductLayoutBinding) {

        lifecycleScope.launch {
            productViewModel.getSearchProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        dialogBinding.tvWrong.visibility = View.GONE
                        dialogBinding.rvProduct.visibility = View.VISIBLE
                        productSearchAdapter.differ.submitList(it.data)
                        productSearchAdapter.notifyDataSetChanged()
                    }

                    is Resource.Error -> {
                        dialogBinding.rvProduct.visibility = View.GONE
                    }

                    else -> {}
                }
            }
        }


        var querySearch: String = ""
        val delayTime: Long = 700

        val handler = Handler()

        val search = Runnable {
            if (querySearch != "") {
                productViewModel.searchProduct(querySearch)
            }
        }
        dialogBinding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {

                if (p0?.toString().equals("") || p0 == null) {
                    productSearchAdapter.differ.submitList(ArrayList<Product>())
                    productSearchAdapter.notifyDataSetChanged()
                    dialogBinding.rvProduct.visibility = View.GONE
                    querySearch = ""
                }

                if (p0 != null && !p0.equals("")) {
                    querySearch = p0.toString()
                    handler.removeCallbacks(search)
                    handler.postDelayed(search, delayTime)

                }
            }
        })

    }
}