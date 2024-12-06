package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentEditBrandBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Brand
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.adminViewmodels.BrandViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditBrandFragment : Fragment() {

    private lateinit var binding: FragmentEditBrandBinding
    private lateinit var brand: Brand
    private var selectedImage: Uri = "".toUri()
    private val brandViewModel by viewModels<BrandViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditBrandBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pickBrandImage()
        val b = arguments
        if (b != null) {
            brand = b.getSerializable("brand") as Brand
            setUp()
        }

    }

    private fun setUp() {


        binding.apply {
            Glide.with(requireContext()).load(brand.image).into(ivBrand)
            etBrandName.setText(brand.name)


            btnSave.setOnClickListener {
                brand.name = etBrandName.text.toString()
                if (selectedImage.toString().equals("")) {
                    brandViewModel.updateBrand(brand)
                } else {

                    brandViewModel.updateAvatarBrand(brand, selectedImage, requireContext())
                }
            }
        }


        lifecycleScope.launch {
            brandViewModel.updateBrand.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.btnSave.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.btnSave.revertAnimation()
                        Toast.makeText(requireContext(), "Lưu thành công", Toast.LENGTH_SHORT)
                            .show()
                    }

                    else -> {


                    }
                }

            }
        }

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
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImageActivityResult.launch(intent)
        }

    }
}