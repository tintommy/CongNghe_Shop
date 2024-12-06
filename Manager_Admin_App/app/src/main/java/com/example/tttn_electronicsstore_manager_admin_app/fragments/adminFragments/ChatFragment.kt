package com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_manager_admin_app.adapters.chat.ChatAdapter
import com.example.tttn_electronicsstore_manager_admin_app.convert.Convert
import com.example.tttn_electronicsstore_manager_admin_app.databinding.FragmentChatBinding
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.History
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding

    private lateinit var chatRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference
    private lateinit var chatAdapter: ChatAdapter

    private val chatList: MutableList<Message> = mutableListOf()
    private lateinit var calendar: Calendar
    private var hour: Int = 0
    private var minute: Int = 0
    private var nam: Int = 0
    private var thang: Int = 0
    private var ngay: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val b = arguments
        val username = b?.getString("username")

        setUp(username)
        initChatRv()


    }

    private fun setUp(username: String?) {


        chatRef = FirebaseDatabase.getInstance().getReference("chats").child(username!!)
        historyRef = FirebaseDatabase.getInstance().getReference("history")

        chatRef.orderByKey().addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newMessageList = mutableListOf<Message>() // Tạo danh sách mới

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)

                    message?.let {
                        newMessageList.add(it)
                    }
                }

                if (newMessageList != chatList) {
                    chatList.clear()
                    chatList.addAll(newMessageList)
                    chatAdapter.submitList(ArrayList(chatList))
                    chatAdapter.notifyDataSetChanged()
                    binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        binding.tvUsername.text = username

        var textSent: String
        var time:String
        binding.btnSendChat.setOnClickListener {
            textSent = binding.etChat.text.toString().trim()
            if (textSent.isNotEmpty()) {
                chatRef.push().setValue(Message(textSent, 0))
                calendar = Calendar.getInstance()
                hour = calendar.get(Calendar.HOUR_OF_DAY) // Giờ (24h format)
                minute = calendar.get(Calendar.MINUTE)    // Phút

                nam = calendar[Calendar.YEAR]
                thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
                ngay = calendar[Calendar.DAY_OF_MONTH]

                if(minute<10){
                    time="$hour:0$minute"
                }
                else {
                    time="$hour:$minute"
                }
                historyRef.child(username).setValue(
                    History(
                        username,
                        1,
                        time,
                        Convert.dinhDangNgayChat(ngay, thang, nam)
                    )
                )
                binding.etChat.setText("")
            }
        }
    }

    private fun initChatRv() {
        chatAdapter = ChatAdapter()
        binding.rvChat.adapter = chatAdapter
        binding.rvChat.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

}