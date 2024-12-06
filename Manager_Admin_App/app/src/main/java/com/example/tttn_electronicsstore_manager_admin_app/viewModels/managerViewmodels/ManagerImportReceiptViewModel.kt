package com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService.ImportReceiptService
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceipt
import com.example.tttn_electronicsstore_manager_admin_app.models.ImportReceiptDetail
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import com.example.tttn_electronicsstore_manager_admin_app.request.AddImportReceiptRequest
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManagerImportReceiptViewModel @Inject constructor(private var sharedPref: SharedPreferences) :
    ViewModel() {

    private lateinit var token: String
    private lateinit var username: String
    private lateinit var importReceiptService: ImportReceiptService

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        importReceiptService = retrofit.create(ImportReceiptService::class.java)

    }



    private val _receiptByDate = MutableStateFlow<Resource<List<ImportReceipt>>>(Resource.Unspecified())
    val receiptByDate = _receiptByDate.asStateFlow()

    private val _detailReceipt = MutableStateFlow<Resource<List<ImportReceiptDetail>>>(Resource.Unspecified())
    val detailReceipt = _detailReceipt.asStateFlow()


    fun getByDate(fromDate:String, toDate:String){
        viewModelScope.launch {
            val response= importReceiptService.getAllByDate(fromDate, toDate)
            _receiptByDate.emit(Resource.Loading())
            if(response.isSuccessful)
            {
                _receiptByDate.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _receiptByDate.emit(Resource.Error("Lỗi khi tải phiếu nhập"))
            }
        }
    }

    fun getDetailById(id:Int){
        viewModelScope.launch {
            val response= importReceiptService.getDetailById(id)
            _detailReceipt.emit(Resource.Loading())
            if(response.isSuccessful)
            {
                _detailReceipt.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _detailReceipt.emit(Resource.Error("Lỗi khi tải chi tiết phiếu nhập"))
            }
        }
    }
}