package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentAddStaffBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AddStaffFragment : Fragment() {
    private lateinit var binding: FragmentAddStaffBinding

    val map: Map<String, String> = mapOf(
        "Nhân viên" to "admin",
        "Shipper" to "shipper"
    )
    private val roleKeysList: List<String> = map.keys.toList()
    private val userViewModel by viewModels<UserViewModel>()
    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddStaffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventManager()

        initRoleSpinner()

        binding.btnAdd.setOnClickListener {
            val fullName = binding.etFullname.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val pass = binding.etPass.text.toString()
            val rePass = binding.etRePass.text.toString()
            binding.etUsername.error = null
            binding.etEmail.error = null
            binding.etRePass.error = null
            if (isFullyForm(fullName, username, email, pass, rePass)) {
                if (isMatchPass(pass, rePass) && isEmailValid(email)) {
                    val user = User(true, "", email, fullName, pass, role, username)
                    userViewModel.addStaff(user)
                }
            } else {
                Toast.makeText(requireContext(), "Hãy điền đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initRoleSpinner() {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roleKeysList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRole.adapter = adapter

        binding.spRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedKey = roleKeysList[position]
                role = map[selectedKey].toString()


            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun eventManager() {
        lifecycleScope.launch {
            userViewModel.addStaff.collectLatest {
                when (it) {
                    is Resource.Loading -> {

                        binding.btnAdd.startAnimation()

                    }

                    is Resource.Success -> {

                        binding.btnAdd.revertAnimation()
                        when (it.data) {
                            "username exist" -> {
                                binding.tvUsername.error = "username đã tồn tại"
                            }

                            "email exist" -> {
                                binding.tvEmail.error = "email đã tồn tại"
                            }

                            "true" -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Thêm nhân viên thành công",
                                    Toast.LENGTH_SHORT
                                ).show()
                                (activity as ManagerActivity).supportFragmentManager.popBackStack()

                            }

                            else -> {}
                        }

                    }

                    is Resource.Error -> {
                        binding.btnAdd.revertAnimation()
                    }

                    else -> {}
                }
            }
        }
    }


    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        if (emailRegex.matches(email))
            return true
        else {
            binding.tvEmail.error = "Email không đúng định dạng"
            return false
        }
    }

    private fun isFullyForm(
        fullName: String,
        usernameEmail: String,
        email: String,
        password: String,
        rePassword: String
    ): Boolean {
        if (email.equals("") || usernameEmail.equals("") || fullName.equals("") || password.equals("") || rePassword.equals(
                ""
            )
        )
            return false
        return true
    }

    private fun isMatchPass(pass: String, rePass: String): Boolean {
        if (pass.equals(rePass))
            return true
        else {
            binding.tvRePass.error = "Nhập lại mật khẩu không khớp"
            return false
        }
    }
}