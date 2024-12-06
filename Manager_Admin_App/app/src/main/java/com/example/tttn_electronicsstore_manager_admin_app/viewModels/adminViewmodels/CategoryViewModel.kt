package com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.CategoryApiService
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.DetailApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Category
import com.example.tttn_electronicsstore_manager_admin_app.models.Detail
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _addCategory = MutableStateFlow<Resource<Category>>(Resource.Unspecified())
    val addCategory = _addCategory.asStateFlow()

    private val _deleteCategory = MutableSharedFlow<Resource<Boolean>>()
    val deleteCategory = _deleteCategory.asSharedFlow()

    private val _allCategoty = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    val allCategoty = _allCategoty.asStateFlow()

    private val _updateCategory = MutableStateFlow<Resource<Category>>(Resource.Unspecified())
    val updateCategory = _updateCategory.asStateFlow()

    fun add(categoryName: String, idDetailList: MutableList<Int>) {
        viewModelScope.launch {
            _addCategory.emit(Resource.Loading())
            val response = categoryApiService.addCategory(categoryName, idDetailList)
            if (response.isSuccessful) {
                if (response.body()!!.success == true)
                    _addCategory.emit(Resource.Success(response.body()!!.data))
                else
                    _addCategory.emit(Resource.Error("Loại đã tồn tại"))
            } else {
                _addCategory.emit(Resource.Error("Đã xảy ra lỗi"))
            }

        }
    }

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

    fun update(category: Category, addList: List<Int>, deleteList: List<Int>) {
        viewModelScope.launch {
            _updateCategory.emit(Resource.Loading())
            val response =
                categoryApiService.updateCategory(
                    category,
                    addList,
                    deleteList
                )
            if (response.isSuccessful) {
                _updateCategory.emit(Resource.Success(response.body()!!.data))
            } else {
                _updateCategory.emit(Resource.Error("Đã xảy ra lỗi"))
            }
        }
    }


    fun delete(categoryId: Int) {
        viewModelScope.launch {
            _deleteCategory.emit(Resource.Loading())
            val response =
                categoryApiService.deleteCategory(categoryId)
            if (response.isSuccessful) {
                _deleteCategory.emit(Resource.Success(response.body()!!.data))
            } else {
                _deleteCategory.emit(Resource.Error("Đã xảy ra lỗi"))
            }
        }
    }
}