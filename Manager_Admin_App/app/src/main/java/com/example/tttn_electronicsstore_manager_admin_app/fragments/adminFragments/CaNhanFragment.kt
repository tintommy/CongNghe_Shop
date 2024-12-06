package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.AlertDialog
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
import com.example.tttn_electronicsstore_manager_admin_app.databinding.DialogChangePassBinding

import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentCaNhanBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.User
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CaNhanFragment : Fragment() {
    private lateinit var binding: FragmentCaNhanBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCaNhanBinding.inflate(layoutInflater)
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

                    is Resource.Error -> {}
                    else -> {

                    }

                }
            }
        }



    }

    private fun setUp(data: User) {




        binding.apply {
            tvFullName.text = data.fullName
            tvRole.text = data.role
            tvEmail.text = data.email
        }
        binding.btnChangePass.setOnClickListener {
            val dialogBinding: DialogChangePassBinding =
                DialogChangePassBinding.inflate(layoutInflater)

            val mDialog = AlertDialog.Builder(activity).setView(dialogBinding.root).create()
            mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            mDialog.show()



            dialogBinding.btnConfirm.setOnClickListener {

                val pass = dialogBinding.etPass.text.toString().trim()
                val newPass = dialogBinding.etNewPass.text.toString().trim()
                val rePass = dialogBinding.etNewPass.text.toString().trim()


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
                            dialogBinding.btnConfirm.startAnimation()
                        }
                        is Resource.Success -> {
                            dialogBinding.btnConfirm.revertAnimation()
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
                            dialogBinding.btnConfirm.revertAnimation()
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }

                        else -> {}
                    }
                }
            }

        }
    }

    private fun isFullyDialog(dialogBinding: DialogChangePassBinding): Boolean {
        dialogBinding.apply {
            val pass = dialogBinding.etPass.text.toString().trim()
            val newPass = dialogBinding.etNewPass.text.toString().trim()
            val rePass = dialogBinding.etNewPass.text.toString().trim()
            if (pass.isEmpty() || newPass.isEmpty() || rePass.isEmpty()) {
                return false
            }

        }
        return true
    }

    private fun check(dialogBinding: DialogChangePassBinding): Boolean {

        dialogBinding.tvNewPass.error=null
        dialogBinding.tvRePass.error=null

        val pass = dialogBinding.etPass.text.toString().trim()
        val newPass = dialogBinding.etNewPass.text.toString().trim()
        val rePass = dialogBinding.etRePass.text.toString().trim()
        if (!rePass.equals(newPass)) {
            dialogBinding.tvRePass.error = "Xác nhận mật khẩu không đúng"
            return false

        }
        return true

    }
}