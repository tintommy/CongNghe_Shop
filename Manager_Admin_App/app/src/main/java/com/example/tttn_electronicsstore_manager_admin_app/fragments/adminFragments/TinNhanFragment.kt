package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.activity.AdminActivity
import com.example.tttn_electronicsstore_manager_admin_app.adapters.chat.ChatListAdapter
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentTinNhanBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.History
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TinNhanFragment : Fragment() {
    private lateinit var binding: FragmentTinNhanBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private val historyList: MutableList<History> = mutableListOf()
    private val historyRef = FirebaseDatabase.getInstance().getReference("history")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTinNhanBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initChatRv()

        historyRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val newHistory = snapshot.getValue(History::class.java)
                Log.e("history", newHistory.toString())
                if (!historyList.contains(newHistory))
                    historyList.add(newHistory!!)

                chatListAdapter.submitList(historyList.sortedWith(compareBy<History> { it.seen }.thenByDescending { it.date }.thenByDescending{it.time}))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                historyList[historyList.indexOfFirst { it.username == snapshot.key }] =
                    snapshot.getValue(History::class.java)!!
                chatListAdapter.submitList(historyList.sortedWith(compareBy<History> { it.seen }.thenByDescending{ it.date }.thenByDescending{it.time})
                  )
                chatListAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun initChatRv() {
        chatListAdapter = ChatListAdapter()
        binding.rvMessage.adapter = chatListAdapter
        binding.rvMessage.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        chatListAdapter.setOnItemClickListener(object :ChatListAdapter.OnItemClickListener{
            override fun onItemClick(history: History) {
                val b = Bundle()
                b.putString("username", history.username)
                val fragment = ChatFragment()
                fragment.arguments = b
                (activity as AdminActivity).replaceFragment(fragment)
            }
        })
    }

}