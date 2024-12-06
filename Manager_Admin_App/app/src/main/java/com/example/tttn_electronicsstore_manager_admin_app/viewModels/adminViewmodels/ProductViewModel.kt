package com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.BrandApiService
import com.example.tttn_electronicsstore_manager_admin_app.api.adminApiService.ProductApiService
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val sharedPref: SharedPreferences) :
    ViewModel() {
    private lateinit var token: String
    private lateinit var username: String
    private lateinit var productApiService: ProductApiService

    init {
        initApiService()
    }

    fun initApiService() {
        this.token = sharedPref.getString("token", "").toString()
        this.username = sharedPref.getString("username", "").toString()

        var retrofit = API_Instance.getClient(token)
        productApiService = retrofit.create(ProductApiService::class.java)

    }

    private val _addProduct = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val addProduct = _addProduct.asStateFlow()

    private val _updateProduct = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val updateProduct = _updateProduct.asStateFlow()

    private val _deleteProduct = MutableSharedFlow<Resource<Boolean>>()
    val deleteProduct = _deleteProduct.asSharedFlow()

    private val _getPageProduct = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val getPageProduct = _getPageProduct.asStateFlow()
    private val _getSearchProduct =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val getSearchProduct = _getSearchProduct.asStateFlow()

    fun addProduct(
        product: Product,
        avatar: Uri,
        images: List<Uri>,
        detail: Map<Int, String>, context: Context
    ) {

        var avatarLink = ""
        val imagesList: MutableList<String> = mutableListOf()
        viewModelScope.launch {
            _addProduct.emit(Resource.Loading())


            val storageRef = FirebaseStorage.getInstance().getReference()
            val imageName =
                "avatar_" + UUID.randomUUID().toString()
                    .substring(0, 9) // Tên duy nhất cho mỗi ảnh
            val imageRef = storageRef.child("images_products/$imageName")
            val stream = context.contentResolver.openInputStream(avatar)
            val uploadTask = imageRef.putStream(stream!!)
            uploadTask.addOnFailureListener {
                viewModelScope.launch {
                    _addProduct.emit(Resource.Error("Lỗi khi up ảnh, hãy thử lại"))
                }
            }.addOnSuccessListener {
                imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                    avatarLink = uri.toString()


                    val soAnh = images.size

                    for (imageUri in images) {
                        val imageName =
                            "image_product_" + UUID.randomUUID().toString()
                                .substring(0, 9) // Tên duy nhất cho mỗi ảnh
                        val imageRef = storageRef.child("images_products/$imageName")
                        val stream = context.contentResolver.openInputStream(imageUri!!)
                        val uploadTask = imageRef.putStream(stream!!)
                        uploadTask.addOnFailureListener {
                            viewModelScope.launch {
                                _addProduct.emit(Resource.Error("Lỗi khi up ảnh"))
                            }
                        }.addOnSuccessListener {
                            imageRef.getDownloadUrl().addOnSuccessListener { uri ->
                                val imageUrl = uri.toString()
                                imagesList.add(imageUrl)
                                if (imagesList.size == soAnh) {

                                    viewModelScope.launch {
                                        val response =
                                            productApiService.addProduct(
                                                product,
                                                avatarLink,
                                                imagesList,
                                                convertMapToJson(detail)
                                            )
                                        if (response.isSuccessful) {
                                            _addProduct.emit(Resource.Success(response.body()!!.data))
                                        } else {
                                            _addProduct.emit(Resource.Error("Lỗi khi thêm sản phẩm"))
                                        }
                                    }

                                }

                            }
                        }
                    }

                }


            }


        }
    }


    fun updateProduct(
        product: Product,
        detail: Map<Int, String>
    ) {

        viewModelScope.launch {
            _updateProduct.emit(Resource.Loading())
            val response =
                productApiService.updateProduct(
                    product,
                    convertMapToJson(detail)
                )
            if (response.isSuccessful) {
                _updateProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _updateProduct.emit(Resource.Error("Lỗi khi sửa sản phẩm"))
            }
        }
    }

    fun updateStatusProduct(id: Int) {
        viewModelScope.launch {
            val response = productApiService.updateStatusProduct(id)
        }
    }

    fun deleteProduct(
        product: Product
    ) {

        viewModelScope.launch {
            _deleteProduct.emit(Resource.Loading())
            val response =
                productApiService.deleteProduct(
                    product.id
                )
            if (response.isSuccessful) {
                _deleteProduct.emit(Resource.Success(response.body()!!.data))
                if (response.body()!!.data == true) {
                    val storage = Firebase.storage

                    for (image in product.imageList) {
                        val storageReference = storage.getReferenceFromUrl(image.link)
                        storageReference.delete()
                    }
                }
            } else {
                _deleteProduct.emit(Resource.Error("Không thể xoá"))
            }
        }
    }

    fun convertMapToJson(map: Map<Int, String>): String {
        return Gson().toJson(map)
    }


    fun getPage(offSet: Int) {
        viewModelScope.launch {
            _getPageProduct.emit(Resource.Loading())
            val response = productApiService.getPageProduct(offSet, 7)
            if (response.isSuccessful) {
                _getPageProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _getPageProduct.emit(Resource.Error("Đã xảy ra lỗi khi tải danh sách sản phẩm trang ${offSet + 1}"))
            }
        }
    }

    fun searchProduct(searchContent: String) {
        viewModelScope.launch {
            _getSearchProduct.emit(Resource.Loading())
            val response = productApiService.searchProduct(searchContent)
            if (response.isSuccessful) {
                _getSearchProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _getSearchProduct.emit(Resource.Error(""))
            }
        }
    }
}