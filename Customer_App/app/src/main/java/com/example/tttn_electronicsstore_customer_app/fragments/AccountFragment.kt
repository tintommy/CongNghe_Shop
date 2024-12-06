package com.example.tttn_electronicsstore_customer_app.fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.example.tttn_electronicsstore_customer_app.activity.LoginActivity
import com.example.tttn_electronicsstore_customer_app.databinding.AddReceiveAddressLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.ChangePassLayoutBinding
import com.example.tttn_electronicsstore_customer_app.databinding.FragmentAccountBinding
import com.example.tttn_electronicsstore_customer_app.helper.showBottomNavigation
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : Fragment() {
    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentAccountBinding
    private val userViewModel by viewModels<UserViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showBottomNavigation()







        binding.btnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_personalInforFragment)
        }

        binding.btnAddress.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_receiveAddressManagerFragment)
        }



        binding.btnChangePass.setOnClickListener {
            openChangePassDialog()
        }


        binding.btnLogout.setOnClickListener {
            val editor = sharedPref.edit()
            editor.remove("token")
            editor.remove("username")
            editor.apply()

            Toast.makeText(requireContext(), "Đã đăng xuất ", Toast.LENGTH_SHORT).show()
            val intent = Intent(
                requireContext(), LoginActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun openChangePassDialog() {
        val dialogBinding: ChangePassLayoutBinding =
            ChangePassLayoutBinding.inflate(layoutInflater)

        val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDialog.show()



        dialogBinding.btnSave.setOnClickListener {

            val pass = dialogBinding.etPass.text.toString().trim()
            val newPass = dialogBinding.etNewPass.text.toString().trim()
            val rePass = dialogBinding.etRePass.text.toString().trim()


            if (isFullyDialog(dialogBinding)) {

                if (check(dialogBinding)) {
                    userViewModel.changePass(pass, newPass)
                }


            } else {
                Toast.makeText(requireContext(), "Hãy điền đủ các thông tin", Toast.LENGTH_LONG)
                    .show()
            }
        }

        lifecycleScope.launch {
            userViewModel.changePass.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        dialogBinding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        dialogBinding.btnSave.revertAnimation()
                        if (it.data == false) {
                            dialogBinding.tvPass.error = "Mật khẩu sai"
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Cập nhật thành công",
                                Toast.LENGTH_SHORT
                            ).show()
                            mDialog.dismiss()
                        }

                    }

                    is Resource.Error -> {
                        dialogBinding.btnSave.revertAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun check(dialogBinding: ChangePassLayoutBinding): Boolean {

        dialogBinding.etPass.error = null
        dialogBinding.tvRePass.error = null

        val pass = dialogBinding.etPass.text.toString().trim()
        val newPass = dialogBinding.etNewPass.text.toString().trim()
        val rePass = dialogBinding.etRePass.text.toString().trim()
        if (!rePass.equals(newPass)) {
            dialogBinding.tvRePass.error = "Xác nhận mật khẩu không đúng"
            return false

        }
        return true

    }

    private fun isFullyDialog(dialogBinding: ChangePassLayoutBinding): Boolean {
        dialogBinding.apply {
            val pass = dialogBinding.etPass.text.toString().trim()
            val newPass = dialogBinding.etNewPass.text.toString().trim()
            val rePass = dialogBinding.etRePass.text.toString().trim()
            if (pass.isEmpty() || newPass.isEmpty() || rePass.isEmpty()) {
                return false
            }

        }
        return true
    }

}