package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.CategoryApiService
import com.example.tttn_electronicsstore_customer_app.models.Category
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var categoryApiService: CategoryApiService

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        categoryApiService = retrofit.create(CategoryApiService::class.java)

    }



    private val _allCategoty = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    val allCategoty = _allCategoty.asStateFlow()




    fun getAll() {
        viewModelScope.launch {
            _allCategoty.emit(Resource.Loading())
            val response = categoryApiService.getAllCategory()
            if (response.isSuccessful) {
                _allCategoty.emit(Resource.Success(response.body()!!.data))
            } else {
                _allCategoty.emit(Resource.Error("Đã xảy ra lỗi khi tải danh sách loại"))
            }
        }
    }


}