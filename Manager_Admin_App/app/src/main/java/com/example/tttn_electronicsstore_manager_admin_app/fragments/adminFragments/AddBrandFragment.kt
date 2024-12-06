package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentAddBrandBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.BrandViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddBrandFragment : Fragment() {
    private lateinit var binding: FragmentAddBrandBinding
    private var selectedImage: Uri = "".toUri()
    private val brandList: MutableList<Brand> = mutableListOf()
    private val brandViewModel by viewModels<BrandViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBrandBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pickBrandImage()
        eventManager()

        brandViewModel.getAllBrand()

        binding.btnSave.setOnClickListener {
            binding.etBrandName.error = null

            if (isFullyForm(selectedImage, binding.etBrandName.text.toString())) {
                if (checkBrandName(binding.etBrandName.text.toString()))
                    brandViewModel.addBrand(
                        selectedImage,
                        binding.etBrandName.text.toString().trim(),
                        requireContext()
                    )
                else {
                    binding.etBrandName.error = "Đã có thương hiệu này"
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Hãy nhập đủ tên và logo thương hiệu",
                    Toast.LENGTH_LONG
                ).show()

            }
        }

    }

    fun isFullyForm(uri: Uri, name: String): Boolean {
        if (uri.toString().equals("") || name.equals("")) {

            return false
        }
        return true
    }

    private fun pickBrandImage() {
        val selectImageActivityResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    //Mutiple images selected
                    if (intent?.clipData != null) {
                        selectedImage = intent.clipData?.getItemAt(0)?.uri!!

                    } else {
                        val imageUri = intent?.data
                        selectedImage = imageUri!!
                    }


                    Glide.with(requireContext()).load(selectedImage).into(binding.ivBrand)
                }

            }

        //chọn ảnh trong máy
        binding.ivBrand.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }

    }

    private fun eventManager() {
        lifecycleScope.launch {
            brandViewModel.addBrand.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()

                    }

                    is Resource.Success -> {
                        binding.btnSave.revertAnimation()

                        if (it.data == true) {

                            val toast = Toast.makeText(
                                requireContext(), "Thêm thành công",
                                Toast.LENGTH_LONG
                            )
                            toast.setGravity(Gravity.CENTER, 0, 0)
                            toast.show()
                            (activity as AdminActivity).supportFragmentManager.popBackStack()
                        } else {
                            binding.etBrandName.error = "Đã có thương hiệu này"
                        }
                    }


                    is Resource.Error -> {
                        binding.btnSave.revertAnimation()
                        val toast = Toast.makeText(
                            requireContext(), it.message,
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                    else -> {

                    }

                }
            }
        }


        lifecycleScope.launch {
            brandViewModel.getAll.collectLatest {
                when (it) {
                    is Resource.Loading -> {


                    }

                    is Resource.Success -> {
                        brandList.addAll(it.data!!)
                        Log.e("List", brandList.toString())
                    }


                    is Resource.Error -> {
                        binding.btnSave.revertAnimation()
                        val toast = Toast.makeText(
                            requireContext(), it.message,
                            Toast.LENGTH_SHORT
                        )
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }

                    else -> {

                    }

                }
            }
        }
    }

    fun checkBrandName(brandName: String): Boolean {

        for (b in brandList) {
            if (b.name.lowercase().equals(brandName.lowercase()))
                return false
        }

        return true
    }


}