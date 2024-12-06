package com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.UserApiService
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.BrandApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow


import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.ConnectException
import java.util.UUID
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

    private val _updateBrand = MutableSharedFlow<Resource<Brand>>()
    val updateBrand = _updateBrand.asSharedFlow()

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

    fun addBrand(imageUri: Uri, name: String, context: Context) {
        viewModelScope.launch {
            _addBrand.emit(Resource.Loading())

            val storageRef = FirebaseStorage.getInstance().getReference()
            val imageName =
                name + "_" + UUID.randomUUID().toString()
                    .substring(0, 3) // Tên duy nhất cho mỗi ảnh
            val imageRef = storageRef.child("images_brand/$imageName")
            val stream = context.contentResolver.openInputStream(imageUri!!)
            val uploadTask = imageRef.putStream(stream!!)
            uploadTask.addOnFailureListener {
                viewModelScope.launch {
                    _addBrand.emit(Resource.Error("Lỗi khi up ảnh, hãy thử lại"))
                }
            }.addOnSuccessListener {
                imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    viewModelScope.launch {
                        val brand = Brand(0, imageUrl, name, true);
                        try {
                            val respone = brandApiService.addBrand(brand)
                            if (respone.isSuccessful) {
                                if (respone.body()!!.success == true)
                                    _addBrand.emit(Resource.Success(respone.body()!!.success!!))
                                else {
                                    _addBrand.emit(Resource.Error("Không thể thêm thương hiệu này"))
                                }
                            }
                        } catch (e: ConnectException) {
                            _addBrand.emit(Resource.Error("Không thể kết nối tới Server"))
                        }
                    }

                }


            }


        }

    }

    fun updateBrand(brand: Brand) {
        viewModelScope.launch {
            val response = brandApiService.updateBrand(brand)
            if(response.isSuccessful){
                _updateBrand.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _updateBrand.emit(Resource.Error(response.body()?.desc!!))
            }
        }
    }

    fun updateAvatarBrand(brand: Brand,imageUri: Uri, context: Context ) {

        viewModelScope.launch {
            _updateBrand.emit(Resource.Loading())
            val storageRef = FirebaseStorage.getInstance().getReference()
            val imageName =
                brand.name + "_" + UUID.randomUUID().toString()
                    .substring(0, 3) // Tên duy nhất cho mỗi ảnh
            val imageRef = storageRef.child("images_brand/$imageName")
            val stream = context.contentResolver.openInputStream(imageUri!!)
            val uploadTask = imageRef.putStream(stream!!)
            uploadTask.addOnFailureListener {
                viewModelScope.launch {
                    _updateBrand.emit(Resource.Error("Lỗi khi up ảnh, hãy thử lại"))

                }
            }.addOnSuccessListener {
                imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()

                    viewModelScope.launch {
                        val brandUpdate = Brand(brand.id, imageUrl, brand.name, brand.status);
                        try {
                            val respone = brandApiService.updateBrand(brandUpdate)
                            if (respone.isSuccessful) {
                                if (respone.body()!!.success == true)
                                    _updateBrand.emit(Resource.Success(respone.body()!!.data))
                                else {
                                    _updateBrand.emit(Resource.Error("Không thể cập nhật thương hiệu này"))
                                }
                            }
                        } catch (e: ConnectException) {
                            _updateBrand.emit(Resource.Error("Không thể kết nối tới Server"))
                        }
                    }

                }


            }


        }

    }

    fun deleteBrand(brand: Brand) {
        viewModelScope.launch {
            _deleteBrand.emit(Resource.Loading())
            val response = brandApiService.deleteBrand(brand.id)
            if(response.isSuccessful){
                _deleteBrand.emit(Resource.Success(response.body()!!.data))
            }
            else{
                _deleteBrand.emit(Resource.Error(response.body()?.desc!!))
            }
        }
    }
}