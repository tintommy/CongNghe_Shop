package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.adapters.ReviewAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentTopReviewBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Review
import com.example.tttn_electronicsstore_manager_admin_app.util.Resource
import com.example.tttn_electronicsstore_manager_admin_app.viewModels.managerViewmodels.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TopReviewFragment : Fragment() {
    private lateinit var binding: FragmentTopReviewBinding
    private var productId: Int = 0
    private lateinit var fromDay: String
    private lateinit var toDay: String
    private val productViewModel by viewModels<ProductViewModel>()
    private val reviewAdapter = ReviewAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopReviewBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            productId = b.getInt("productId")
            fromDay = b.getString("fromDay", "").toString()
            toDay = b.getString("toDay", "").toString()
            setUp()
        }
    }

    private fun setUp() {
        productViewModel.getReviewProduct(productId, fromDay, toDay)

        lifecycleScope.launch {
            productViewModel.reviewOfProduct.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        initRv(it.data!!)
                    }

                    is Resource.Error -> {}
                    else -> {}
                }
            }
        }


    }

    private fun initRv(reviewList: List<Review>) {
        reviewAdapter.differ.submitList(reviewList)
        binding.rvReview.apply {
            adapter = reviewAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }
}