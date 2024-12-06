package com.example.tttn_electronicsstore_customer_app.viewModels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Priority
import com.example.tttn_electronicsstore_customer_app.api.API_Instance
import com.example.tttn_electronicsstore_customer_app.api.ProductApiService
import com.example.tttn_electronicsstore_customer_app.models.Product
import com.example.tttn_electronicsstore_customer_app.models.Review
import com.example.tttn_electronicsstore_customer_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    private val _getProduct =
        MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val getProduct = _getProduct.asStateFlow()
    private val _getPageProduct =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val getPageProduct = _getPageProduct.asStateFlow()
    private val _topNewProduct =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val topNewProduct = _topNewProduct.asStateFlow()
    private val _topBoughtProduct =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val topBoughtProduct = _topBoughtProduct.asStateFlow()

    private val _getSearchProduct =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val getSearchProduct = _getSearchProduct.asStateFlow()

    private val _getFilterProduct =
        MutableSharedFlow<Resource<List<Product>>>()
    val getFilterProduct = _getFilterProduct.asSharedFlow()

    private val _getFilterProductPriority =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val getFilterProductPriority = _getFilterProductPriority.asStateFlow()

    private val _getPageReview =
        MutableSharedFlow<Resource<List<Review>>>()
    val getPageReview = _getPageReview.asSharedFlow()

    fun getProduct(id: Int) {
        viewModelScope.launch {
            val response = productApiService.getProduct(id)
            if (response.isSuccessful) {
                _getProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _getProduct.emit(Resource.Error("Xảy ra lỗi khi tải sản phẩm"))
            }
        }
    }


    fun getPageProduct(
        categoryId: Int,
        type: String,
        orderType: String,
        offset: Int,
        pageSize: Int
    ) {
        viewModelScope.launch {
            val response =
                productApiService.getPageProduct(categoryId, type, orderType, offset, pageSize)
            if (response.isSuccessful) {
                _getPageProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _getPageProduct.emit(Resource.Error("Xảy ra lỗi khi tải sản phẩm"))
            }
        }
    }

    fun getTopNewProduct(num: Int) {
        viewModelScope.launch {
            val response = productApiService.getTopNewProduct(num)
            if (response.isSuccessful) {
                _topNewProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _topNewProduct.emit(Resource.Error("Xảy ra lỗi khi load top new"))
            }
        }
    }

    fun getTopBoughtProduct(num: Int) {
        viewModelScope.launch {
            val response = productApiService.getTopBoughtProduct(num)
            if (response.isSuccessful) {
                _topBoughtProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _topBoughtProduct.emit(Resource.Error("Xảy ra lỗi khi load top bought"))
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


    fun filterProduct(
        categoryId: Int,
        brandId: Int,
        productDetail: String,
        fromPrice: Long,
        toPrice: Long
    ) {
        viewModelScope.launch {
            _getFilterProduct.emit(Resource.Loading())
            val response = productApiService.filterProduct(
                categoryId,
                brandId,
                productDetail,
                fromPrice,
                toPrice
            )
            if (response.isSuccessful) {
                _getFilterProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _getFilterProduct.emit(Resource.Error(""))
            }
        }
    }

    fun filterProductPriority(
        productDetail: String,
        productPriority: String,
        categoryId: Int,
        brandId: Int,
        fromPrice: Long,
        toPrice: Long
    ) {
        viewModelScope.launch {
            _getFilterProductPriority.emit(Resource.Loading())
            val response = productApiService.filterProductPriority(
                productDetail,
                productPriority,
                categoryId,
                brandId,
                fromPrice,
                toPrice
            )
            if (response.isSuccessful) {
                _getFilterProductPriority.emit(Resource.Success(response.body()!!.data))
            } else {
                _getFilterProductPriority.emit(Resource.Error(""))
                _getFilterProductPriority.emit(Resource.Unspecified())
            }


        }
    }


    fun getPageReview(productId: Int, offset: Int, pageSize: Int) {
        viewModelScope.launch {
            _getPageReview.emit(Resource.Loading())
            val response = productApiService.getReview(productId, offset, pageSize)
            if (response.isSuccessful) {
                _getPageReview.emit(Resource.Success(response.body()!!.data))
            } else {
                _getPageReview.emit(Resource.Error(""))
            }
        }
    }

//    fun getPage(offSet: Int) {
//        viewModelScope.launch {
//            _getPageProduct.emit(Resource.Loading())
//            val response = productApiService.getPageProduct(offSet, 7)
//            if (response.isSuccessful) {
//                _getPageProduct.emit(Resource.Success(response.body()!!.data))
//            } else {
//                _getPageProduct.emit(Resource.Error("Đã xảy ra lỗi khi tải danh sách sản phẩm trang ${offSet + 1}"))
//            }
//        }
//    }
//
//    fun searchProduct(searchContent: String) {
//        viewModelScope.launch {
//            _getSearchProduct.emit(Resource.Loading())
//            val response = productApiService.searchProduct(searchContent)
//            if (response.isSuccessful) {
//                _getSearchProduct.emit(Resource.Success(response.body()!!.data))
//            } else {
//                _getSearchProduct.emit(Resource.Error(""))
//            }
//        }
//    }
}