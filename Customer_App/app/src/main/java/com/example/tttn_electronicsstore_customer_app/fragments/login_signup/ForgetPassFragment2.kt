package com.example.tttn_electronicsstore_customer_app.fragments.login_signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentForgetPass2Binding
import com.example.tttn_electronicsstore_customer_app.request.ChangePassOTPRequest
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPassFragment2 : Fragment() {
    private lateinit var binding: FragmentForgetPass2Binding
    private val loginViewModel by viewModels<LoginViewModel>()
    private var username = ""
    private var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPass2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        username = b?.getString("username") as String
        email = b?.getString("email") as String



        binding.btnSubmit.setOnClickListener {
            val otp = binding.etOTP.text.toString()
            val newPass = binding.etPass.text.toString()
            val rePassword = binding.etRePass.text.toString()
            binding.tvOTP.error = null
            binding.tvRePass.error = null

            if (isFullyForm(otp, newPass, rePassword)) {
                if (!newPass.equals(rePassword))
                    binding.tvRePass.error = "Nhập lại mật khẩu không đúng"
                else {
                    val changePassOTPRequest = ChangePassOTPRequest(username, newPass, email, otp)
                    loginViewModel.changePassOTP(changePassOTPRequest)
                }
            } else {
                Toast.makeText(requireContext(), "Hãy nhập đủ thông tin", Toast.LENGTH_LONG).show()
            }
        }





        lifecycleScope.launch {
            loginViewModel.changePassOTP.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSubmit.startAnimation()

                    }

                    is Resource.Success -> {
                        binding.btnSubmit.revertAnimation()
                        if (it.data == true) {
                            Toast.makeText(
                                requireContext(),
                                "Cập nhật mật khẩu thành công",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_forgetPassFragment2_to_loginFragment)
                        } else {
                            binding.tvOTP.error = "OTP không khớp"
                        }
                    }

                    is Resource.Error -> {
                        binding.btnSubmit.revertAnimation()
                        Toast.makeText(requireContext(), "Lỗi khi gửi OTP", Toast.LENGTH_LONG)
                            .show()
                    }

                    else -> {}

                }
            }

        }
    }

    private fun isFullyForm(
        otp: String,
        password: String,
        rePassword: String
    ): Boolean {
        if (otp.equals("") || password.equals("") || rePassword.equals(""))
            return false
        return true
    }
}