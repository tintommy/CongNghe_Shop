package com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.orderFragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.adapters.OrderDetailAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.CancelReasonBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentOrderDetailShipperBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.SuccessOrderBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.shipperViewModels.ShipperOrderViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class OrderDetailShipperFragment : Fragment() {
    private lateinit var binding: FragmentOrderDetailShipperBinding
    private lateinit var order: Order
    private val orderViewModel by viewModels<ShipperOrderViewModel>()
    private val orderDetailAdapter: OrderDetailAdapter = OrderDetailAdapter()
    private var selectedImage: Uri = "".toUri()
    private var cancelReasonList: List<String> = listOf(
        "Khách từ chối nhận hàng",
        "Không liên lạc được với khách hàng",
        "Địa chỉ không chính xác",
        "Sản phẩm bị hư hỏng",
        "Lí do khác"
    )

    private var cancelReason = ""
    private lateinit var successDialogBinding: SuccessOrderBinding
    private lateinit var selectSuccessImage: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailShipperBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = arguments
        if (b != null) {
            order = b.getSerializable("order") as Order
            setUpInfo()
            orderViewModel.getDetailByOrderId(order.id)
            eventManager()
            initAdapter()
        }

        selectSuccessImage =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    //Mutiple images selected
                    if (intent?.clipData != null) {
                        selectedImage = intent.clipData?.getItemAt(0)?.uri!!

                    } else {
                        val imageUri = intent?.data
                        selectedImage = imageUri!!
                    }

                    Glide.with(requireContext()).load(selectedImage)
                        .into(successDialogBinding.btnAddPhoto)
                }

            }
    }

    private fun initAdapter() {
        binding.rvListOrderDetail.adapter = orderDetailAdapter
        binding.rvListOrderDetail.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun eventManager() {
        lifecycleScope.launch {
            orderViewModel.detailOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        orderDetailAdapter.differ.submitList(it.data)

                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }

        }

        lifecycleScope.launch {
            orderViewModel.updateOrder.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        order = it.data!!
                        setUpInfo()

                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }

        }
    }

    private fun setUpInfo() {
        binding.apply {

            when (order.status) {
                0 -> {
                    tvOrderStatus.text = "Đã huỷ"
                }

                1 -> {
                    tvOrderStatus.text = "Đang chờ"
                }

                2 -> {
                    tvOrderStatus.text = "Đang chuẩn bị"
                }

                3 -> {
                    tvOrderStatus.text = "Đang giao"
                }

                4 -> {
                    tvOrderStatus.text = "Đã giao hàng"
                }

                5 -> {
                    tvOrderStatus.text = "Thành công"
                }

                6 -> {
                    tvOrderStatus.text = "Giao hàng thất bại"
                }


                else -> {}
            }




            tvReceiverName.text = order.receiverName
            tvReceiverPhone.text = order.receiverPhone
            tvReceiverAddress.text = order.receiverAddress
            tvOrderPrice.text = Convert.formatNumberWithDotSeparator(order.total) + "đ"
            tvOrderShip.text = Convert.formatNumberWithDotSeparator(order.ship) + "đ"
            tvOrderTotal.text = Convert.formatNumberWithDotSeparator(order.total + order.ship) + "đ"





            if (order.status == 3) {
                layoutBtn.visibility = View.VISIBLE
            } else {
                layoutBtn.visibility = View.GONE
            }


        }

        if (order.onlinePay) {
            binding.tvOrderPay.text = "Đã thanh toán online"
        }



        if (order.status == 4 || order.status == 5) {
            binding.layoutShippedImage.visibility = View.VISIBLE
            Glide.with(requireContext()).load(order.shippedImage).into(binding.ivShippedOrder)
        } else {
            binding.layoutShippedImage.visibility = View.GONE
        }


        binding.btnCancelOrder.setOnClickListener {


            openCancelDialog()


//            val builder = AlertDialog.Builder(requireContext())
//            builder.setMessage("Chắc chắn huỷ đơn hàng  ?")
//            builder.setTitle("Xác nhận !")
//            builder.setCancelable(false)
//            builder.setPositiveButton("Huỷ") { dialog, which ->
//                orderViewModel.changeOrderStatus(order.id, 0)
//            }
//
//            builder.setNegativeButton("Không") { dialog, which ->
//                dialog.cancel()
//            }
//            val alertDialog = builder.create()
//            alertDialog.show()

        }
        binding.btnAcceptOrder.setOnClickListener {
            openConfirmDialog()
        }
        binding.btnCallOrder.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL);
            intent.data = Uri.parse("tel:${order.receiverPhone}")
            startActivity(intent)
        }
        binding.btnMap.setOnClickListener {
            navigateToDestination()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            navigateToDestination() // Gọi lại hàm khi được cấp quyền
        } else {
            Toast.makeText(
                requireContext(),
                "Cần cấp quyền để sử dụng chức năng này",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun navigateToDestination() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Yêu cầu quyền nếu chưa được cấp
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        // Lấy vị trí hiện tại
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                //  val origin = LatLng(location.latitude, location.longitude)
                val destination = getCoordinatesFromAddress(order.receiverAddress) // Tọa độ đích
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("google.navigation:q=${destination?.first},${destination?.second}")
                )
                intent.setPackage("com.google.android.apps.maps")
                startActivity(intent)
            } else {
                // Không tìm thấy vị trí
                Toast.makeText(
                    requireContext(),
                    "Hãy mở gps",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getCoordinatesFromAddress(address: String): Pair<Double, Double>? {
        return try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocationName(address, 1) // Lấy 1 kết quả đầu tiên
            if (!addresses.isNullOrEmpty()) {
                val location = addresses[0]
                Pair(location.latitude, location.longitude)
            } else {
                null // Không tìm thấy tọa độ
            }
        } catch (e: Exception) {
            Log.e("GeocoderError", "Error getting coordinates: ${e.message}")
            null
        }
    }

    private fun openConfirmDialog() {
        successDialogBinding = SuccessOrderBinding.inflate(layoutInflater)
        val mDialog = AlertDialog.Builder(activity).setView(successDialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        successDialogBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }
        mDialog.show()

        successDialogBinding.btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectSuccessImage.launch(intent)


        }

        successDialogBinding.btnConfirm.setOnClickListener {
            if (selectedImage.toString().equals("")) {
                Toast.makeText(requireContext(), "Hãy chọn hình ảnh", Toast.LENGTH_SHORT).show()
            } else {
                orderViewModel.confirmOrderShipped(order.id, selectedImage, requireContext())
            }
        }


        mDialog.setOnCancelListener {
            selectedImage = "".toUri()
        }
        mDialog.setOnDismissListener {
            selectedImage = "".toUri()

        }


        lifecycleScope.launch {
            orderViewModel.confirmOrderShipped.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        successDialogBinding.btnConfirm.startAnimation()

                    }

                    is Resource.Success -> {
                        successDialogBinding.btnConfirm.revertAnimation()
                        mDialog.dismiss()
                        order = it.data!!
                        setUpInfo()
                    }

                    is Resource.Error -> {
                        successDialogBinding.btnConfirm.revertAnimation()
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun openCancelDialog() {


        val dialogBinding: CancelReasonBinding = CancelReasonBinding.inflate(layoutInflater)
        initReasonAdapter(dialogBinding)
        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogBinding.btnClose.setOnClickListener {
            mDialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            if (cancelReason.equals("Lí do khác")) {
                if (dialogBinding.etOtherReason.text.toString().equals("")) {
                    Toast.makeText(requireContext(), "Hãy nhập lí do", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else
                    cancelReason = dialogBinding.etOtherReason.text.toString()

            }
            orderViewModel.cancelOrder(order.id, cancelReason)
        }

        mDialog.show()



        dialogEvent(mDialog)

    }

    private fun dialogEvent(mDialog: AlertDialog) {
        lifecycleScope.launch {
            orderViewModel.cancelOrder.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        mDialog.dismiss()
                        Toast.makeText(requireContext(), "Đã huỷ đơn hàng", Toast.LENGTH_LONG)
                            .show()
                        order = it.data!!
                        setUpInfo()
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initReasonAdapter(dialogBinding: CancelReasonBinding) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, cancelReasonList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spReason.adapter = adapter


        dialogBinding.spReason.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (cancelReasonList[position].equals("Lí do khác"))
                        dialogBinding.etOtherReason.visibility = View.VISIBLE
                    else {
                        dialogBinding.etOtherReason.visibility = View.GONE

                    }
                    cancelReason = cancelReasonList[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
    }


}