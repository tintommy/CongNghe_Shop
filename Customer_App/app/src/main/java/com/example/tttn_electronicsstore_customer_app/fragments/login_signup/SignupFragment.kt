package com.example.tttn_electronicsstore_customer_app.fragments.login_signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentSignupBinding
import com.example.tttn_electronicsstore_customer_app.request.SignUpRequest
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignupFragment : Fragment() {
    private lateinit var binding: FragmentSignupBinding
    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var signUpRequest:SignUpRequest
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btnSignup.setOnClickListener {
            val fullName = binding.etFullname.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPass.text.toString()
            val rePass = binding.etRePass.text.toString()
            if (checkInput(username, fullName, email, pass, rePass)) {
                signUpRequest=SignUpRequest(username,fullName,pass,email)
                loginViewModel.userSignUp(username, email, pass, fullName)
            }
        }


        lifecycleScope.launch {

            loginViewModel.signup.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSignup.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnSignup.revertAnimation()
                        when (it.data) {
                            "username exist" -> {
                                binding.etUsername.error="Username đã tồn tại"
                            }
                            "email exist" -> {
                                binding.etEmail.error="Email đã tồn tại"
                            }
                            "false" ->{

                                Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                            }
                            "true" ->{
                                val b= Bundle()
                                b.putSerializable("signUpRequest",signUpRequest)
                                view.findNavController().navigate(R.id.action_signupFragment_to_OTPFragment,b)
                            }
                            else ->{}
                        }

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi", Toast.LENGTH_SHORT).show()
                        binding.btnSignup.revertAnimation()
                    }
                    else -> {}


                }
            }
        }
    }

    private fun checkInput(
        username: String, fullName: String, email: String, pass: String, rePass: String
    ): Boolean {
        binding.etUsername.error = null
        binding.etFullname.error = null
        binding.etEmail.error = null
        binding.layoutPass.error = null
        binding.layoutRePass.error = null
        var check = 0
        binding.apply {

            if (username.equals("")) {
                etUsername.setError("Hãy nhập username")
                check = 1
            }
            if (email.equals("")) {
                etEmail.setError("Hãy nhập email")
                check = 1
            }
            if(!isEmailValid(email)){
                etEmail.setError("Email không đúng định dạng")
                check=1
            }
            if (fullName.equals("")) {
                etFullname.setError("Hãy nhập họ tên")
                check = 1
            }
            if (pass.equals("")) {
                layoutPass.setError("Hãy nhập mật khẩu")
                check = 1
            }
            if (!rePass.equals(pass)) {
                layoutRePass.setError("Xác nhận mật khẩu không đúng")
                check = 1
            }
        }

        if (check != 0)
            return false
        return true
    }
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

}