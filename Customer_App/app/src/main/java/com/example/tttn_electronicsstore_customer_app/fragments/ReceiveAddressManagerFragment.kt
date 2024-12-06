package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.ReceiveAddressManagerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.DistrictSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.ProvinceSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.WardSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.AddReceiveAddressLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.AddReceiveAddressLayoutDialogBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentReceiveAddressManagerBinding
import com.example.tttn_electronicsstore_customer_app.helper.hideBottomNavigation
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Province
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Ward
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.ReceiveAddressViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.giaoHangNhanh.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReceiveAddressManagerFragment : Fragment() {
    private lateinit var binding: FragmentReceiveAddressManagerBinding
    private val receiveAddressManagerAdapter = ReceiveAddressManagerAdapter()
    private val receiveAddressViewModel by viewModels<ReceiveAddressViewModel>()
    private val addressViewModel by viewModels<AddressViewModel>()
    private var deleteAddress = -1


    private var address: String = ""
    private var provinceId: Int = 0
    private lateinit var provinceName: String
    private var districtId: Int = 0
    private lateinit var districtName: String
    private var wardCode: String = ""
    private lateinit var wardName: String
    private lateinit var addAddressCode: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceiveAddressManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideBottomNavigation()
        eventManager()
        initAdapter()
        receiveAddressViewModel.getAddress()


        binding.btnAdd.setOnClickListener {
            openGetAddressDialog()
        }
    }

    private fun eventManager() {
        lifecycleScope.launch {
            receiveAddressViewModel.address.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        if (it.data == null) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        } else {
                            binding.tvEmpty.visibility = View.GONE
                            binding.rvAddress.visibility = View.VISIBLE
                        }
                        receiveAddressManagerAdapter.differ.submitList(it.data)

                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            receiveAddressViewModel.addAddress.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        receiveAddressViewModel.getAddress()
                        Toast.makeText(requireContext(), "Thêm thành công", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }

        }




        lifecycleScope.launch {
            receiveAddressViewModel.deleteAddress.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), "Xoá thành công", Toast.LENGTH_SHORT)
                            .show()
                        receiveAddressManagerAdapter.deleteItem(deleteAddress)
                        deleteAddress = -1
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }

        }
    }

    private fun initAdapter() {
        binding.rvAddress.apply {
            adapter = receiveAddressManagerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

        receiveAddressManagerAdapter.setOnItemClickListener(object :
            ReceiveAddressManagerAdapter.OnItemClickListener {
            override fun onItemClick(receiveAddress: ReceiveAddress) {

            }

            override fun onEditClick(receiveAddress: ReceiveAddress) {
                openEditDialog(receiveAddress)
            }

            override fun onDeleteClick(receiveAddress: ReceiveAddress, position: Int) {
                openDeleteDialog(receiveAddress, position)
            }
        })
    }

    private fun openDeleteDialog(receiveAddress: ReceiveAddress, position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Bạn có chắc chắn xoá địa chỉ \"${receiveAddress.addressName}\" ?")
        builder.setCancelable(false)
        builder.setPositiveButton("Xoá") { dialog, which ->
            receiveAddressViewModel.deleteAddress(receiveAddress.id)
            deleteAddress = position
        }

        builder.setNegativeButton("Không") { dialog, which ->
            dialog.cancel()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun openEditDialog(receiveAddress: ReceiveAddress) {
        val dialogBinding: AddReceiveAddressLayoutDialogBinding =
            AddReceiveAddressLayoutDialogBinding.inflate(layoutInflater)

        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDialog.show()

        dialogBinding.apply {
            dialogBinding.etReceiveName.setText(receiveAddress.addressName)
            dialogBinding.etReceiverName.setText(receiveAddress.receiverName)
            dialogBinding.etReceiverPhone.setText(receiveAddress.receiverPhone)
            dialogBinding.etDetailAddress.setText(receiveAddress.receiverAddress)
        }



        addressViewModel.getAllProvince()
        lifecycleScope.launch {
            addressViewModel.allProvince.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initProvinceSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }
        lifecycleScope.launch {
            addressViewModel.allDistrict.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initDistrictSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }
        lifecycleScope.launch {
            addressViewModel.allWard.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initWardSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }


        dialogBinding.btnAddAddress.text = "Sửa địa chỉ"
        dialogBinding.btnAddAddress.setOnClickListener {

            val addressName = dialogBinding.etReceiveName.text.toString().trim()
            val receiverName = dialogBinding.etReceiverName.text.toString().trim()
            val receiverPhone = dialogBinding.etReceiverPhone.text.toString().trim()
            val detailAddress = dialogBinding.etDetailAddress.text.toString().trim()

            if (isFullyDialog(dialogBinding)) {
                addAddressCode = districtId.toString() + ";" + wardCode
                if (!detailAddress.equals(receiveAddress.receiverAddress)) {
                    address =
                        dialogBinding.etDetailAddress.text.toString() + ", " + wardName + ", " + districtName + ", " + provinceName

                } else {
                    address = receiveAddress.receiverAddress

                }



                receiveAddressViewModel.addAddress(
                    receiveAddress.id,
                    addressName,
                    receiverName,
                    receiverPhone,
                    address,
                    addAddressCode
                )
                mDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Hãy điền đủ các thông tin", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun openGetAddressDialog() {
        val dialogBinding: AddReceiveAddressLayoutDialogBinding =
            AddReceiveAddressLayoutDialogBinding.inflate(layoutInflater)

        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDialog.show()

        addressViewModel.getAllProvince()
        lifecycleScope.launch {
            addressViewModel.allProvince.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initProvinceSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }
        lifecycleScope.launch {
            addressViewModel.allDistrict.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initDistrictSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }
        lifecycleScope.launch {
            addressViewModel.allWard.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        initWardSPAdapter(dialogBinding, it.data!!);
                    }

                    is Resource.Loading -> {}
                    else -> {}
                }
            }

        }


        dialogBinding.btnAddAddress.setOnClickListener {
            address =
                dialogBinding.etDetailAddress.text.toString() + ", " + wardName + ", " + districtName + ", " + provinceName
            addAddressCode = districtId.toString() + ";" + wardCode
            Log.e("Address", address)






            if (isFullyDialog(dialogBinding)) {
                val addressName = dialogBinding.etReceiveName.text.toString()
                val receiverName = dialogBinding.etReceiverName.text.toString()
                val receiverPhone = dialogBinding.etReceiverPhone.text.toString()
                val detailAddress = dialogBinding.etDetailAddress.text.toString()
                if (detailAddress.isNotEmpty()) {
                    address =
                        dialogBinding.etDetailAddress.text.toString() + ", " + wardName + ", " + districtName + ", " + provinceName

                } else {
                    address =
                        wardName + ", " + districtName + ", " + provinceName

                }
                receiveAddressViewModel.addAddress(
                    0,
                    addressName,
                    receiverName,
                    receiverPhone,
                    address,
                    addAddressCode
                )
                mDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Hãy điền đủ các thông tin", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


//    private fun openAddAddressDialog() {
//        val dialogBinding: AddReceiveAddressLayoutBinding =
//            AddReceiveAddressLayoutBinding.inflate(layoutInflater)
//
//        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
//        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//
//        mDialog.show()
//
//        dialogBinding.btnAddAddress.setOnClickListener {
//
//            val addressName = dialogBinding.etReceiveName.text.toString()
//            val receiverName = dialogBinding.etReceiverName.text.toString()
//            val receiverPhone = dialogBinding.etReceiverPhone.text.toString()
//            val receiverAddress = dialogBinding.etReceiverAddress.text.toString()
//
//            if (isFullyDialog(dialogBinding)) {
//                receiveAddressViewModel.addAddress(
//                    0,
//                    addressName,
//                    receiverName,
//                    receiverPhone,
//                    receiverAddress,
//                    "addressCode"
//                )
//                mDialog.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "Hãy điền đủ các thông tin", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }
//
//    }

    private fun isFullyDialog(dialogBinding: AddReceiveAddressLayoutDialogBinding): Boolean {
        dialogBinding.apply {
            val addressName = etReceiveName.text.toString()
            val receiverName = etReceiverName.text.toString()
            val receiverPhone = etReceiverPhone.text.toString()

            if (addressName.isEmpty() || receiverName.isEmpty() || receiverPhone.isEmpty()) {
                return false
            }
            return true
        }
    }


    private fun initProvinceSPAdapter(
        dialogBinding: AddReceiveAddressLayoutDialogBinding, provinceList: List<Province>
    ) {
        val adapter = ProvinceSpinnerAdapter(
            requireContext(), provinceList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spProvince.adapter = adapter

        dialogBinding.spProvince.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                selectedCategoryId = p3.toInt()
                    addressViewModel.getAllDistrict(p3.toInt());
                    provinceId = provinceList[p2].provinceID
                    provinceName = provinceList[p2].provinceName
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun initDistrictSPAdapter(
        dialogBinding: AddReceiveAddressLayoutDialogBinding, districtList: List<District>
    ) {
        val adapter = DistrictSpinnerAdapter(
            requireContext(), districtList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spDistrict.adapter = adapter

        dialogBinding.spDistrict.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                selectedCategoryId = p3.toInt()
                    addressViewModel.getAllWard(p3.toInt());
                    districtId = districtList[p2].districtID
                    districtName = districtList[p2].districtName
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

            }
    }

    private fun initWardSPAdapter(
        dialogBinding: AddReceiveAddressLayoutDialogBinding, wardList: List<Ward>
    ) {
        val adapter = WardSpinnerAdapter(
            requireContext(), wardList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spWard.adapter = adapter

        dialogBinding.spWard.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                selectedCategoryId = p3.toInt()
//                loadRvDetail(categoryList[p2].details)

                wardCode = wardList[p2].wardCode
                wardName = wardList[p2].wardName
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }
}