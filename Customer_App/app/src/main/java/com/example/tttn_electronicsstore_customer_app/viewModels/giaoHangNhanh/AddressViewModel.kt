package com.example.tttn_electronicsstore_customer_app.viewModels.giaoHangNhanh

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.giaoHangNhanh_api.AddressApiService
import com.example.tttn_electronicsstore_customer_app.api.giaoHangNhanh_api.GiaoHangNhanhAPI_Instance
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.District
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Province
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.TotalFee
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.TotalFeeRequest
import com.example.tttn_electronicsstore_customer_app.models.giaoHangNhanh.Ward
import com.example.tttn_electronicsstore_customer_app.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ConnectException

class AddressViewModel : ViewModel() {
    private lateinit var addressApiService: AddressApiService

    init {
        initApiService()
    }

    fun initApiService() {

        var retrofit = GiaoHangNhanhAPI_Instance.getClient()
        addressApiService = retrofit.create(AddressApiService::class.java)
    }

    private val _getAllProvince = MutableSharedFlow<Resource<List<Province>>>()
    val allProvince = _getAllProvince.asSharedFlow()
    private val _getAllDistrict = MutableSharedFlow<Resource<List<District>>>()
    val allDistrict = _getAllDistrict.asSharedFlow()
    private val _getAllWard = MutableSharedFlow<Resource<List<Ward>>>()
    val allWard = _getAllWard.asSharedFlow()
    private val _getTotalFee = MutableSharedFlow<Resource<TotalFee>>()
    val totalFee = _getTotalFee.asSharedFlow()


    fun getAllProvince() {
        viewModelScope.launch {
            _getAllProvince.emit(Resource.Loading())
            try {
                val response = addressApiService.getAllProvince()

                if (response.isSuccessful) {
                    Log.e("Address", "Success");
                    _getAllProvince.emit(Resource.Success(response.body()!!.data.sortedBy { it.provinceName }))
                } else {
                    Log.e("Address", "Error");
                    _getAllProvince.emit(Resource.Error("Đã xảy ra lỗi"))
                }
            } catch (e: ConnectException) {
                _getAllProvince.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }


    fun getAllDistrict(provinceId: Int) {
        viewModelScope.launch {
            _getAllDistrict.emit(Resource.Loading())
            try {
                val response = addressApiService.getAllDistrict(provinceId)

                if (response.isSuccessful) {
                    _getAllDistrict.emit(Resource.Success(response.body()!!.data.sortedBy { it.districtName }))
                } else {
                    _getAllDistrict.emit(Resource.Error("Đã xảy ra lỗi"))
                }
            } catch (e: ConnectException) {
                _getAllDistrict.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }

    fun getAllWard(districtId: Int) {
        viewModelScope.launch {
            _getAllWard.emit(Resource.Loading())
            try {
                val response = addressApiService.getAllWard(districtId)

                if (response.isSuccessful) {
                    _getAllWard.emit(Resource.Success(response.body()!!.data.sortedBy { it.wardName }))
                } else {
                    _getAllWard.emit(Resource.Error("Đã xảy ra lỗi"))
                }
            } catch (e: ConnectException) {
                _getAllWard.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }

    fun getTotalFee(totalFeeRequest: TotalFeeRequest) {
        viewModelScope.launch {
            _getTotalFee.emit(Resource.Loading())
            try {
                val response = addressApiService.getFee(
                    totalFeeRequest.coupon,
                    totalFeeRequest.fromDistrictId,
                    totalFeeRequest.fromWardCode,
                    totalFeeRequest.height,
                    totalFeeRequest.insuranceValue,
                    totalFeeRequest.length,
                    totalFeeRequest.serviceTypeId,
                    totalFeeRequest.toDistrictId,
                    totalFeeRequest.toWardCode,
                    totalFeeRequest.weight,
                    totalFeeRequest.width
                )

                if (response.isSuccessful) {
                    _getTotalFee.emit(Resource.Success(response.body()!!.data!!))
                    Log.e("Address", response.body()!!.data!!.total.toString())
                } else {
                    _getTotalFee.emit(Resource.Error("Đã xảy ra lỗi"))
                    Log.e("Address", "lỗi")

                }
            } catch (e: ConnectException) {
                _getTotalFee.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }
}