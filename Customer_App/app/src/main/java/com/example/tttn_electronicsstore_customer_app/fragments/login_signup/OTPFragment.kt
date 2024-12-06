package com.example.tttn_electronicsstore_customer_app.fragments.login_signup

import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentOTPBinding
import com.example.tttn_electronicsstore_customer_app.request.SignUpRequest
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OTPFragment : Fragment() {
    private lateinit var binding: FragmentOTPBinding

    private val loginViewModel by viewModels<LoginViewModel>()
    private lateinit var signUpRequest: SignUpRequest
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOTPBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            signUpRequest = bundle.getSerializable("signUpRequest") as SignUpRequest
        }
        init()
        startCountdown()

        lifecycleScope.launch {
            loginViewModel.verify.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnConfirm.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnConfirm.revertAnimation()
                        if (it.data == true) {
                            Toast.makeText(
                                requireContext(),
                                "Đăng kí tài khoản thành công",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            view.findNavController()
                                .navigate(R.id.action_OTPFragment_to_loginFragment)

                        } else {
                            binding.tvNotCorrect.visibility = View.VISIBLE
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                            .show()
                        binding.btnConfirm.revertAnimation()
                    }

                    else -> {}
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            binding.tvNotCorrect.visibility = View.GONE
            val otp = binding.etOTP.text.toString()
            loginViewModel.userVerify(signUpRequest, otp)
        }

        binding.btnReOTP.setOnClickListener {

        }
    }

    fun init() {
        binding.tvEmail.text = signUpRequest.email



        view?.setFocusableInTouchMode(true)
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    view?.findNavController()?.navigateUp()
                }
                return true
            }
        })
    }

    private fun startCountdown() {
        binding.btnReOTP.isEnabled = false

        var countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.btnReOTP.text = "Có thể gửi lại mã sau $seconds giây"
                println("Seconds remaining: $seconds")
            }

            override fun onFinish() {
                binding.btnReOTP.isEnabled = true
                binding.btnReOTP.text = "Gửi lại mã "
            }
        }

        countDownTimer.start()
    }
}