package com.example.tttn_electronicsstore_customer_app.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {
    @Inject
    lateinit var sharedPref: SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private val userViewModel by viewModels<UserViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        handler.postDelayed({
            userViewModel.getUser()
            lifecycleScope.launch {
                userViewModel.user.collectLatest {
                    when (it) {

                        is Resource.Loading -> {}
                        is Resource.Success -> {
                                val toast = Toast.makeText(
                                    applicationContext,
                                    "Đã đăng nhập vào " + it.data!!.username,
                                    Toast.LENGTH_LONG
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                                val intent = Intent(
                                    this@SplashScreenActivity,
                                    MainActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            finish()

                        }

                        is Resource.Error -> {
                            val editor = sharedPref.edit()
                            editor.remove("token")
                            editor.remove("username")
                            editor.apply()
                            val intent = Intent(
                                this@SplashScreenActivity,
                                LoginActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            finish()

                        }

                        else -> {}
                    }
                }

            }

        }, 1500)
    }
}