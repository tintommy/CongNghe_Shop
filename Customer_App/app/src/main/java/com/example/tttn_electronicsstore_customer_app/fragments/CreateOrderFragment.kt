package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.activity.MainActivity
import com.example.tttn_electronicsstore_customer_app.activity.PaymentActivity
import com.example.tttn_electronicsstore_customer_app.adapters.CartOrderDetailAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.ReceiveAddressAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.DistrictSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.ProvinceSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.adapters.giaoHangNhanhAdapter.WardSpinnerAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.AddReceiveAddressLayoutDialogBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentCreateOrderBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.helper.hideBottomNavigation
import com.example.tttn_electronicsstore_customer_app.models.Cart
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Province
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.TotalFeeRequest
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Ward
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.CartViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.OrderViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.ReceiveAddressViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.giaoHangNhanh.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.Serializable


@AndroidEntryPoint
class CreateOrderFragment : Fragment() {

    private lateinit var binding: FragmentCreateOrderBinding
    private val receiveAddressViewModel by viewModels<ReceiveAddressViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private val addressViewModel by viewModels<AddressViewModel>()
    private val receiveAddressAdapter = ReceiveAddressAdapter()
    private val cartOrderDetailAdapter = CartOrderDetailAdapter()
    private var total = 0
    private var productsPrice = 0
    private var ship = 0
    private var selectedAddress: ReceiveAddress? = null;
    private var onlinePay = false

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
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateOrderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        hideBottomNavigation()
        initAdapter()
        initSpinner()
        eventManager()

        receiveAddressViewModel.getAddress()
        cartViewModel.getCartOrder()
        buttonEventManager()

    }

    private fun buttonEventManager() {
        binding.btnAddAddress.setOnClickListener {
            openGetAddressDialog()
        }

        binding.btnOrder.setOnClickListener {

            if (selectedAddress != null) {

                val builder = AlertDialog.Builder(requireContext())
                builder.setMessage("Xác nhận đặt hàng ?")
                builder.setCancelable(false)
                builder.setPositiveButton("Đặt") { dialog, which ->
                    if (onlinePay) {
                        val intent = Intent(requireContext(), PaymentActivity::class.java)

                        intent.putExtra("selectedAddress", selectedAddress as Serializable)
                        intent.putExtra("productPrice", productsPrice)
                        intent.putExtra("ship", ship)
                        startActivity(intent)

                    } else {
                        orderViewModel.addOrder(selectedAddress!!, productsPrice,ship, onlinePay)
                    }
                }

                builder.setNegativeButton("Huỷ") { dialog, which ->
                    dialog.cancel()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                Toast.makeText(requireContext(), "Hãy chọn địa chỉ", Toast.LENGTH_SHORT).show()
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
//                    receiverAddress
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

    private fun eventManager() {
        lifecycleScope.launch {
            receiveAddressViewModel.address.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        if (it.data == null) {

                        } else {

                        }
                        receiveAddressAdapter.differ.submitList(it.data)

                    }

                    else -> {}
                }
            }
        }

        lifecycleScope.launch {
            cartViewModel.getCartOrder.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        if (it.data == null) {

                        } else {

                        }
                        cartOrderDetailAdapter.differ.submitList(it.data)
                        totalCalculate(it.data!!)
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
            orderViewModel.addOrder.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        (activity as MainActivity).showDialogOrderSuccess()
                        findNavController().navigateUp()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }

        }

        lifecycleScope.launch {
            addressViewModel.totalFee.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        ship = it.data!!.total
                        if (ship > 100000) {
                            ship -= 100000
                        } else {
                            ship = 0
                        }
                        Log.e("Total", ship.toString())
                        total = ship + productsPrice
                        binding.tvTotal.setText(Convert.formatNumberWithDotSeparator(total) + "đ")
                        binding.tvShip.setText(Convert.formatNumberWithDotSeparator(ship) + "đ")
                        binding.tvProductPrice.setText(Convert.formatNumberWithDotSeparator(productsPrice) + "đ")

                    }

                    is Resource.Loading -> {}
                    else -> {

                    }
                }
            }
        }
    }

    private fun initAdapter() {
        binding.rvListAddress.apply {
            adapter = receiveAddressAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        receiveAddressAdapter.setOnItemClickListener(object :
            ReceiveAddressAdapter.OnItemClickListener {
            override fun onItemClick(receiveAddress: ReceiveAddress) {
                calculateShip(receiveAddress)
                selectedAddress = receiveAddress
                setLayoutAddress(receiveAddress)
                receiveAddressAdapter.notifyDataSetChanged()
            }

        })



        binding.rvDetailOrder.apply {
            adapter = cartOrderDetailAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }
    }

    private fun calculateShip(receiveAddress: ReceiveAddress) {

        var addressCode= receiveAddress.addressCode.split(";")
        var totalFeeRequest =
            TotalFeeRequest(
                "",
                1451,
                "20901",
                50,
                productsPrice/10,
                50,
                2,
                addressCode[0].toInt(),
                addressCode[1],
                5000,
                50
            )

        addressViewModel.getTotalFee(totalFeeRequest)
    }

    private fun setLayoutAddress(receiveAddress: ReceiveAddress) {
        binding.apply {
            tvReceiverAddress.visibility = View.VISIBLE
            tvReceiverName.visibility = View.VISIBLE
            tvReceiverPhone.visibility = View.VISIBLE
            tvReceiverAddress.text = receiveAddress.receiverAddress
            tvReceiverName.text = receiveAddress.receiverName
            tvReceiverPhone.text = receiveAddress.receiverPhone
        }
    }

    fun totalCalculate(cartList: List<Cart>) {
        productsPrice = 0
        for (cart in cartList) {
            if (cart.status) productsPrice += cart.productPrice * cart.quantity
        }
        total=productsPrice
        binding.tvProductPrice.text = Convert.formatNumberWithDotSeparator(productsPrice) + "đ"
        binding.tvShip.text = Convert.formatNumberWithDotSeparator(ship) + "đ"
        binding.tvTotal.text = Convert.formatNumberWithDotSeparator(productsPrice + ship) + "đ"
    }

    fun initSpinner() {
        val items = arrayOf("Thanh toán khi nhận hàng", "Thanh toán zalopay")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spPay.adapter = adapter

        binding.spPay.onItemSelectedListener =
            object : AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    if (p2 == 0) {
                        onlinePay = false
                    } else if (p2 == 1) {
                        onlinePay = true
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                }

            }

    }

    override fun onResume() {
        super.onResume()
        Log.e("Order", "On resume")

        if (MainActivity.onlinePaySuccess) {
            MainActivity.onlinePaySuccess = false
            (activity as MainActivity).showDialogOrderSuccess()
            findNavController().navigateUp()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.e("Order", "On stop")
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
//                    receiverAddress
//                )
//                mDialog.dismiss()
//            } else {
//                Toast.makeText(requireContext(), "Hãy điền đủ các thông tin", Toast.LENGTH_LONG)
//                    .show()
//            }
//        }


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