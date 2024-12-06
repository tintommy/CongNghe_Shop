package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentEditStaffBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditStaffFragment : Fragment() {
    private lateinit var binding: FragmentEditStaffBinding
private val userViewModel by viewModels<UserViewModel>()
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditStaffBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            user = b.getSerializable("user") as User
            setUp()
        }


        binding.btnSubmit.setOnClickListener {
            val fullName = binding.etFullname.text.toString()
            val email = binding.etEmail.text.toString()
            binding.etEmail.error = null
            if (isFullyForm(fullName, email)) {
                if (isEmailValid(email)) {
                    val userTemp = User(true, "", email, fullName, user.password, "admin", user.username)
                    userViewModel.updateStaff(userTemp)
                }
            } else {
                Toast.makeText(requireContext(), "Hãy điền đủ thông tin", Toast.LENGTH_SHORT).show()
            }
        }



        lifecycleScope.launch {
            userViewModel.updateStaff.collectLatest {
                when(it){
                    is Resource.Loading ->{
                        binding.btnSubmit.startAnimation()

                    }
                    is Resource.Success ->{
                        binding.btnSubmit.revertAnimation()
                        Toast.makeText(requireContext(), "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show()

                    }
                    is Resource.Error ->{
                        binding.btnSubmit.revertAnimation()
                    }
                    else ->{
                        Toast.makeText(requireContext(), "Đã xảy ra lỗi ", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setUp() {
        binding.apply {
            etFullname.setText(user.fullName)
            etEmail.setText(user.email)
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
        email: String
    ): Boolean {
        if (email.equals("") ||  fullName.equals(""))
            return false
        return true
    }
}