package com.example.tttn_electronicsstore_manager_admin_app.fragments.managerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.activity.ManagerActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.CancelReasonAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentCancelReasonOrderManagerBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.Order
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList

@AndroidEntryPoint
class CancelReasonOrderManagerFragment : Fragment() {

    private lateinit var binding: FragmentCancelReasonOrderManagerBinding
    private var orderList: MutableList<Order> = mutableListOf()
    private lateinit var reasonMap: MutableMap<String, Int>
    private var beforeShipping = 0
    private var afterShipping = 0
    private var cancelReasonAdapter = CancelReasonAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCancelReasonOrderManagerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        if (b != null) {
            orderList = b.getSerializable("orderList") as MutableList<Order>
        }

        setUp()
    }

    private fun setUp() {




        beforeShipping = 0
        afterShipping = 0
        reasonMap= mutableMapOf(
                "Khách từ chối nhận hàng" to 0,
        "Không liên lạc được với khách hàng" to 0,
        "Địa chỉ không chính xác" to 0,
        "Sản phẩm bị hư hỏng" to 0,
        "Khác" to 0
        )
        for (order in orderList) {
            if (order.shipperUsername == null) {
                beforeShipping += 1

            } else {
                afterShipping += 1
                if (reasonMap.containsKey(order.cancelReason)) {
                    reasonMap[order.cancelReason!!] = reasonMap[order.cancelReason]!! + 1
                } else {
                    reasonMap["Khác"] = reasonMap["Khác"]!! + 1
                }
            }


        }



        binding.apply {


            rvCancelReason.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            rvCancelReason.adapter = cancelReasonAdapter
            cancelReasonAdapter.differ.submitList(reasonMap.toList())


            cancelReasonAdapter.setOnItemClickListener(object :
                CancelReasonAdapter.OnItemClickListener {
                override fun onBtnClick(reason: String) {
                    val b = Bundle()
                    b.putSerializable(
                        "orderList",
                        ArrayList(orderList.filter { it.cancelReason.equals(reason) })
                    )
                    val fragment = ListOrderFragment()
                    fragment.arguments = b
                    (activity as ManagerActivity).replaceFragment(fragment)
                }
            })
        }
    }
}