package com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.DetailApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val sharedPref: SharedPreferences) : ViewModel() {

    private lateinit var token: String
    private lateinit var username: String
    private lateinit var detailApiService: DetailApiService

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        detailApiService = retrofit.create(DetailApiService::class.java)

    }

    private val _alldetail = MutableStateFlow<Resource<List<Detail>>>(Resource.Unspecified())
    val allDetail = _alldetail.asStateFlow()
    private val _addDetail = MutableStateFlow<Resource<Detail>>(Resource.Unspecified())
    val addDetail = _addDetail.asStateFlow()

    fun getAll() {
        viewModelScope.launch {
            _alldetail.emit(Resource.Loading())
            val response = detailApiService.getAll()
            try {
                _alldetail.emit(Resource.Success(response.body()!!.data.sortedBy { it.name }))
            } catch (e: ConnectException) {
                _alldetail.emit(Resource.Error("Không thể kết nối tới Server"))
            }
        }
    }

    fun add(name:String){
        viewModelScope.launch {
            val response = detailApiService.add(name)
            if(response.isSuccessful){
                    _addDetail.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _addDetail.emit(Resource.Error("Đã xảy ra lỗi !"))
            }
        }
    }
}