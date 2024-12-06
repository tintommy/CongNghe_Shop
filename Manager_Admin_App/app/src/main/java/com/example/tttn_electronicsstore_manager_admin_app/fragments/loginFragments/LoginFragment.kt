package com.example.tttn_electronicsstore_manager_admin_app.fragments.loginFragments

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.LoginActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.ShipperActivity
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentLoginBinding
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            loginViewModel.login.collectLatest {

                when (it) {

                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        if (it.data!!.success == true) {
                            if (it.data?.role.equals("manager")) {

                                val intent = Intent(
                                    requireContext(),
                                    ManagerActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                requireActivity().finish()
                            } else if (it.data?.role.equals("admin")) {

                                val intent = Intent(
                                    requireContext(),
                                    AdminActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                requireActivity().finish()
                            } else if (it.data?.role.equals("shipper")) {

                                val intent = Intent(
                                    requireContext(),
                                    ShipperActivity::class.java
                                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                requireActivity().finish()
                            } else {
                                val toast = Toast.makeText(
                                    requireContext(), "Tài khoản này không có quyền đăng nhập",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }


                        } else {
                            if (it.data!!.desc.equals("Account not exist")) {
                                val toast = Toast.makeText(
                                    requireContext(), "Username/email không tồn tại",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            } else if (it.data.desc.equals("Wrong password")) {
                                val toast = Toast.makeText(
                                    requireContext(), "Sai mật khẩu",
                                    Toast.LENGTH_SHORT
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            } else if (it.data.desc.equals("Locked")) {
                                val toast = Toast.makeText(
                                    requireContext(), "Tài khoản bị khoá",
                                    Toast.LENGTH_LONG
                                )
                                toast.setGravity(Gravity.CENTER, 0, 0)
                                toast.show()
                            }

                        }
                    }

                    is Resource.Error -> {
                        binding.btnLogin.revertAnimation()
                        val toast = Toast.makeText(
                            requireContext(), it.message,
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()

                    }


                    else -> {}
                }
            }
        }
        binding.btnLogin.setOnClickListener {
            var userNameEmail = binding.etUsernameEmail.text.toString()
            var pass = binding.etPass.text.toString()
            if (isFullyForm(
                    userNameEmail, pass
                )
            ) {
                if (isEmailValid(userNameEmail)) {
                    loginViewModel.userLogin("", userNameEmail, pass)
                } else {
                    loginViewModel.userLogin(userNameEmail, "", pass)
                }
            } else {
                val toast = Toast.makeText(
                    requireContext(), "Hãy nhập đủ thông tin đăng nhập",
                    Toast.LENGTH_SHORT
                )
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun isFullyForm(usernameEmail: String, password: String): Boolean {
        if (usernameEmail.equals("") || password.equals(""))
            return false
        return true
    }
}