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
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentForgetPassBinding
import com.example.tttn_electronicsstore_customer_app.request.ChangePassOTPRequest
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.LoginViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgetPassFragment : Fragment() {
    private lateinit var binding: FragmentForgetPassBinding
    private val loginViewModel by viewModels<LoginViewModel>()
    var email = ""
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgetPassBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.btnSubmit.setOnClickListener {
            email = ""
            username = ""
            binding.tvUsernameEmail.error = null
            val usernameEmail = binding.etUsernameEmail.text.toString()
            if (!usernameEmail.equals("")) {
                if (isEmailValid(usernameEmail)) {
                    email = usernameEmail
                } else {
                    username = usernameEmail
                }
                loginViewModel.sendOTP(username, email)


            } else {
                binding.tvUsernameEmail.error = "Hãy nhập username/email"
            }
        }



//        binding.btnGetOTP.setOnClickListener {
//            email = ""
//            username = ""
//            binding.tvUsernameEmail.error = null
//            val usernameEmail = binding.etUsernameEmail.text.toString()
//            if (!usernameEmail.equals("")) {
//                if (isEmailValid(usernameEmail)) {
//                    email = usernameEmail
//                } else {
//                    username = usernameEmail
//                }
//                loginViewModel.sendOTP(username, email)
//
//
//            } else {
//                binding.tvUsernameEmail.error = "Hãy nhập username/email"
//            }
//
//        }


//        binding.btnSubmit.setOnClickListener {
//            val usernameEmail= binding.etUsernameEmail.text.toString()
//            val otp= binding.etOTP.text.toString()
//            val newPass= binding.etPass.text.toString()
//            val rePassword= binding.etRePass.text.toString()
//            binding.tvOTP.error=null
//            binding.tvRePass.error=null
//
//            if(isFullyForm(usernameEmail,otp,newPass, rePassword)){
//                if(!newPass.equals(rePassword))
//                    binding.tvRePass.error="Nhập lại mật khẩu không đúng"
//                else{
//                    val changePassOTPRequest= ChangePassOTPRequest(username,newPass,email,otp)
//                    loginViewModel.changePassOTP(changePassOTPRequest)
//                }
//            }
//            else{
//                Toast.makeText(requireContext(), "Hãy nhập đủ thông tin", Toast.LENGTH_LONG).show()
//            }
//        }



        lifecycleScope.launch {
            loginViewModel.sendOTP.collectLatest {
                when(it)
                {
                    is Resource.Loading ->{
                        binding.btnSubmit.startAnimation()

                    }
                    is Resource.Success ->{
                        binding.btnSubmit.revertAnimation()
                        if(it.data==true) {
                            Toast.makeText(requireContext(), "Đã gửi OTP", Toast.LENGTH_LONG).show()

                            val b= Bundle()
                            b.putString("username",username)
                            b.putString("email",email)
                            findNavController().navigate(R.id.action_forgetPassFragment_to_forgetPassFragment2,b)


                        }
                        else{
                            binding.tvUsernameEmail.error="username/email không tồn tại"
                        }
                    }
                    is Resource.Error ->{
                        binding.btnSubmit.revertAnimation()
                        Toast.makeText(requireContext(), "Lỗi khi gửi OTP", Toast.LENGTH_LONG).show()
                    }
                    else->{}

                }
            }

        }


//        lifecycleScope.launch {
//            loginViewModel.changePassOTP.collectLatest {
//                when(it)
//                {
//                    is Resource.Loading ->{
//                        binding.btnSubmit.startAnimation()
//
//                    }
//                    is Resource.Success ->{
//                        binding.btnSubmit.revertAnimation()
//                        if(it.data==true) {
//                            Toast.makeText(requireContext(), "Cập nhật mật khẩu thành công", Toast.LENGTH_LONG).show()
//                           findNavController().navigateUp()
//                        }
//                        else{
//                            binding.tvOTP.error="OTP không khớp"
//                        }
//                    }
//                    is Resource.Error ->{
//                        binding.btnSubmit.revertAnimation()
//                        Toast.makeText(requireContext(), "Lỗi khi gửi OTP", Toast.LENGTH_LONG).show()
//                    }
//                    else->{}
//
//                }
//            }
//
//        }

    }


    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }


}