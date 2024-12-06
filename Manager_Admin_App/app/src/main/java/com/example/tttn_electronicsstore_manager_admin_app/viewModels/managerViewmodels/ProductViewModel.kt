package com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductReview
import com.example.tttn_electronicsstore_manager_admin_app.analystModel.ProductSale
import com.example.tttn_electronicsstore_manager_admin_app.api.API_Instance
import com.example.tttn_electronicsstore_manager_admin_app.api.managerApiService.ProductApiService

import com.example.tttn_electronicsstore_manager_admin_app.models.Product
import com.example.tttn_electronicsstore_manager_admin_app.models.Review
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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


    private val _mostBoughtProduct =
        MutableStateFlow<Resource<List<ProductSale>>>(Resource.Unspecified())
    val mostBoughtProduct = _mostBoughtProduct.asStateFlow()


    private val _mostReviewProduct =
        MutableStateFlow<Resource<List<ProductReview>>>(Resource.Unspecified())
    val mostReviewProduct = _mostReviewProduct.asStateFlow()

     private val _reviewOfProduct =
        MutableStateFlow<Resource<List<Review>>>(Resource.Unspecified())
    val reviewOfProduct = _reviewOfProduct.asStateFlow()



    fun getMostBoughtProduct(fromDate: String, toDate: String, limit: Int) {
        viewModelScope.launch {
            _mostBoughtProduct.emit(Resource.Loading())
            val response = productApiService.getMostBoughtProduct(fromDate, toDate, limit)
            if (response.isSuccessful) {
                _mostBoughtProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _mostBoughtProduct.emit(Resource.Error("Lỗi khi lấy sản phẩm"))
            }
        }
    }


    fun getMostReviewProduct(fromDate: String, toDate: String, limit: Int) {
        viewModelScope.launch {
            _mostReviewProduct.emit(Resource.Loading())
            val response = productApiService.getMostReviewProduct(fromDate, toDate, limit)
            if (response.isSuccessful) {
                _mostReviewProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _mostReviewProduct.emit(Resource.Error("Lỗi khi lấy sản phẩm"))
            }
        }
    }

    fun getReviewProduct(productId:Int, fromDate: String, toDate: String) {
        viewModelScope.launch {
            _reviewOfProduct.emit(Resource.Loading())
            val response = productApiService.getReviewProduct(productId,fromDate, toDate)
            if (response.isSuccessful) {
                _reviewOfProduct.emit(Resource.Success(response.body()!!.data))
            } else {
                _reviewOfProduct.emit(Resource.Error("Lỗi khi lấy sản phẩm"))
            }
        }
    }
}