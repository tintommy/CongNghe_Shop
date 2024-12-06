package com.example.tttn_electronicsstore_customer_app.viewModels


import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.BrandApiService
import com.example.tttn_electronicsstore_customer_app.models.Brand
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject


@HiltViewModel
class BrandViewModel @Inject constructor(private var sharedPref: SharedPreferences) : ViewModel() {

    private lateinit var token: String
    private lateinit var username: String
    private lateinit var brandApiService: BrandApiService

    private val _getAll = MutableSharedFlow<Resource<List<Brand>>>()
    val getAll = _getAll.asSharedFlow()

    private val _addBrand = MutableSharedFlow<Resource<Boolean>>()
    val addBrand = _addBrand.asSharedFlow()

    private val _deleteBrand = MutableSharedFlow<Resource<Boolean>>()
    val deleteBrand = _deleteBrand.asSharedFlow()

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        brandApiService = retrofit.create(BrandApiService::class.java)

    }

    fun getAllBrand() {
        viewModelScope.launch {
            _getAll.emit(Resource.Loading())
            try {
                val response = brandApiService.getAll()

                if (response.isSuccessful) {
                    _getAll.emit(Resource.Success(response.body()!!.data.sortedBy { it.name }))
                } else {
                    _getAll.emit(Resource.Error("Đã xảy ra lỗi"))
                }
            } catch (e: ConnectException) {
                _getAll.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }





}