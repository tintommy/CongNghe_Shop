package com.example.tttn_electronicsstore_customer_app.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentPersonalInforBinding
import com.example.tttn_electronicsstore_customer_app.models.User
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonalInforFragment : Fragment() {
    private lateinit var binding: FragmentPersonalInforBinding
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPersonalInforBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.getUser()

        lifecycleScope.launch {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        setUp(it.data!!)

                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Có lỗi khi tải thông tin người dùng",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                    }
                }
            }
        }



        lifecycleScope.launch {
            userViewModel.updateUser.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSubmit.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnSubmit.revertAnimation()
                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show()

                    }

                    is Resource.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "Xảy ra lỗi khi cập nhật dữ liệu",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {

                    }
                }
            }
        }

    }

    private fun setUp(user: User) {
        binding.apply {
            etName.setText(user.fullName)
            etUsername.setText(user.username)
            etEmail.setText(user.email)
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.etName.text.toString().trim().equals("")) {
                Toast.makeText(requireContext(), "Họ tên không được để trống", Toast.LENGTH_LONG)
                    .show()
            } else {

                user.fullName = binding.etName.text.toString()
                userViewModel.updateUser(user)

            }
        }
    }

}