package com.example.tttn_electronicsstore_customer_app.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tttn_electronicsstore_customer_app.zalopay.CreateOrder
import com.example.tttn_electronicsstore_customer_app.databinding.ActivityPaymentBinding
import com.example.tttn_electronicsstore_customer_app.models.ReceiveAddress
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener


@AndroidEntryPoint
class PaymentActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPaymentBinding
    private val orderViewModel by viewModels<OrderViewModel>()

    private lateinit var selectedAddress: ReceiveAddress
    private var productPrice = 0
    private var ship = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //zalo pay
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ZaloPaySDK.init(2554, Environment.SANDBOX)
        //
        selectedAddress = intent.getSerializableExtra("selectedAddress") as ReceiveAddress
        Log.e("Order", selectedAddress.toString())
        productPrice = intent.getIntExtra("productPrice", 0)
        ship = intent.getIntExtra("ship", 0)

        lifecycleScope.launch {
            orderViewModel.addOrder.collectLatest {
                when (it) {
                    is Resource.Success -> {

                        MainActivity.onlinePaySuccess = true
                        finish()
                    }

                    is Resource.Error -> {

                    }

                    else -> {}
                }
            }

        }


        requestZalo()


    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
    }

    private fun requestZalo() {

        val orderApi = CreateOrder()

        try {
            val data = orderApi.createOrder((productPrice+ship).toString())


            val code = data!!.getString("return_code")
            Toast.makeText(this, "return_code: $code", Toast.LENGTH_LONG).show()
            if (code == "1") {

                val token: String = data.getString("zp_trans_token")
                ZaloPaySDK.getInstance().payOrder(
                    this,
                    token,
                    "demozpdk://app",
                    object : PayOrderListener {
                        override fun onPaymentSucceeded(
                            transactionId: String,
                            transToken: String,
                            appTransID: String?
                        ) {
                            orderViewModel.addOrder(selectedAddress, productPrice , ship , true)
                        }

                        override fun onPaymentCanceled(zpTransToken: String?, appTransID: String?) {
                            Toast.makeText(
                                this@PaymentActivity,
                                "Huỷ giao dịch",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }

                        override fun onPaymentError(
                            zaloPayErrorCode: ZaloPayError?,
                            zpTransToken: String?,
                            appTransID: String?
                        ) {
                            Toast.makeText(
                                this@PaymentActivity,
                                "Xảy ra lỗi khi thanh toán",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }


                    })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}