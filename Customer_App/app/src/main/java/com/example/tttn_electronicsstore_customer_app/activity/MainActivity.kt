package com.example.tttn_electronicsstore_customer_app.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.adapters.chatBotAdapter.ChatAdapter
import com.example.tttn_electronicsstore_customer_app.databinding.ActivityMainBinding
import com.example.tttn_electronicsstore_customer_app.databinding.OrderSuccessLayoutBinding
import com.example.tttn_electronicsstore_customer_app.helper.Convert
import com.example.tttn_electronicsstore_customer_app.models.User
import com.example.tttn_electronicsstore_customer_app.models.chatBot.ChatRequest
import com.example.tttn_electronicsstore_customer_app.models.chatBot.Content
import com.example.tttn_electronicsstore_customer_app.models.chatBot.History
import com.example.tttn_electronicsstore_customer_app.models.chatBot.Message
import com.example.tttn_electronicsstore_customer_app.models.chatBot.Part
import com.example.tttn_electronicsstore_customer_app.util.Resource
import com.example.tttn_electronicsstore_customer_app.viewModels.CartViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.UserViewModel
import com.example.tttn_electronicsstore_customer_app.viewModels.chatBot.ChatBotViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val cartViewModel by viewModels<CartViewModel>()
    private val chatBotAdapter: ChatAdapter = ChatAdapter()
    private val chatSupportAdapter: ChatAdapter = ChatAdapter()
    private val chatList: MutableList<Message> = mutableListOf()
    private val chatBotViewModel by viewModels<ChatBotViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private lateinit var user: User
    private var isExpand = false
    private val temp =
        ", trình bày ngắn gọn bằng tiếng việt, dễ hiểu, kết quả chỉ cần đoạn text, không lặp lại câu hỏi, không có kí tự đặc biệt"


    private val database = FirebaseDatabase.getInstance()
    private lateinit var chatRef: DatabaseReference
    private lateinit var historyRef: DatabaseReference
    private val chatSupportList: MutableList<Message> = mutableListOf()

    private lateinit var calendar: Calendar
    private var nam: Int = 0
    private var thang: Int = 0
    private var ngay: Int = 0
    private var hour: Int = 0
    private var minute: Int = 0
    private val fromBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_fab)
    }
    private val toBottomFabAnim: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_fab)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainHost) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomMenu.setupWithNavController(navController)
        updateBadge()


        userViewModel.getUser()
        initChatBot()
        initChatSupport()



        binding.btnChatExpand.setOnClickListener {
            if (isExpand) {
                shrinkFab()
            } else {
                expandFab()
            }



            lifecycleScope.launch {
                userViewModel.user.collectLatest {
                    when (it) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            user = it.data!!
                            chatRef = database.getReference("chats").child(it.data!!.username)
                            historyRef = database.getReference("history")
                        }

                        is Resource.Error -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Có lỗi khi tải thông tin người dùng",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {

                        }
                    }
                }
            }

            chatRef.orderByKey().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val newMessageList = mutableListOf<Message>() // Tạo danh sách mới

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)

                        message?.let {
                            newMessageList.add(it)
                        }
                    }

                    if (newMessageList != chatSupportList) {
                        chatSupportList.clear()
                        chatSupportList.addAll(newMessageList)
                        chatSupportAdapter.submitList(ArrayList(chatSupportList))
                        chatSupportAdapter.notifyDataSetChanged()
                        binding.rvChatSupport.scrollToPosition(chatSupportAdapter.itemCount - 1)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Xử lý lỗi
                }
            })
        }
    }

    fun updateBadge() {
        cartViewModel.countCart()
        val badge: BadgeDrawable = binding.bottomMenu.getOrCreateBadge(R.id.cartFragment)
        var number = 0
        lifecycleScope.launch {
            cartViewModel.countCart.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        number = it.data!!
                        if (number == 0) {
                            badge.isVisible = false
                        } else {
                            badge.isVisible = true
                            badge.number = number
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun showDialogOrderSuccess() {
        val dialogBuyMovieBinding: OrderSuccessLayoutBinding =
            OrderSuccessLayoutBinding.inflate(layoutInflater)

        val mDialog = AlertDialog.Builder(this).setView(dialogBuyMovieBinding.root).create()
        mDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog.show()
    }


    companion object {
        var onlinePaySuccess = false
    }


    private fun expandFab() {
        binding.btnChatBot.startAnimation(fromBottomFabAnim)
        binding.btnChatSupport.startAnimation(fromBottomFabAnim)
        binding.tvChatBot.startAnimation(fromBottomFabAnim)
        binding.tvChatSupport.startAnimation(fromBottomFabAnim)
        binding.btnChatBot.visibility = View.VISIBLE
        binding.btnChatSupport.visibility = View.VISIBLE
        binding.tvChatBot.visibility = View.VISIBLE
        binding.tvChatSupport.visibility = View.VISIBLE
        isExpand = !isExpand
    }

    private fun shrinkFab() {
        binding.btnChatBot.startAnimation(toBottomFabAnim)
        binding.btnChatSupport.startAnimation(toBottomFabAnim)
        binding.tvChatBot.startAnimation(toBottomFabAnim)
        binding.tvChatSupport.startAnimation(toBottomFabAnim)
        binding.btnChatBot.visibility = View.GONE
        binding.btnChatSupport.visibility = View.GONE
        binding.tvChatBot.visibility = View.GONE
        binding.tvChatSupport.visibility = View.GONE

        isExpand = !isExpand
    }


    private fun initChatBot() {

        var textSent: String

        initChatBotRv()

        binding.btnChatBot.setOnClickListener {
            if (binding.layoutChatBot.visibility == View.GONE) {
                binding.layoutChatBot.visibility = View.VISIBLE
            }
        }

        binding.btnHide.setOnClickListener {
            if (binding.layoutChatBot.visibility == View.VISIBLE) {
                binding.layoutChatBot.visibility = View.GONE
            }
        }

        binding.btnSendChatBot.setOnClickListener {
            textSent = binding.etChatBot.text.toString().trim()
            if (textSent.isNotEmpty()) {
                val message = Message(textSent, 1)
                chatList.add(message)
                chatBotAdapter.submitList(chatList)
                chatBotAdapter.notifyDataSetChanged()

                val part = Part(textSent + temp)
                val content = Content(mutableListOf(part))
                val chatRequest = ChatRequest(mutableListOf(content))
                chatBotViewModel.getResponse(chatRequest)

                binding.etChatBot.setText("")
                binding.tvResponseChatBot.visibility = View.VISIBLE
            }

        }

        lifecycleScope.launch {
            chatBotViewModel.getResponse.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        binding.tvResponseChatBot.visibility = View.GONE
                        if (it.data!!.candidates[0].content.parts.isNotEmpty()) {
                            val message =
                                Message(it.data!!.candidates[0].content.parts[0].text, 0)
                            chatList.add(message)
                            chatBotAdapter.submitList(chatList)
                            chatBotAdapter.notifyDataSetChanged()

                        } else {
                            binding.tvResponseChatBot.visibility = View.VISIBLE
                            binding.tvResponseChatBot.text = "Hãy thử nhập lại"
                        }
                    }

                    is Resource.Loading -> {}
                    is Resource.Error -> {
                        binding.tvResponseChatBot.visibility = View.VISIBLE
                        binding.tvResponseChatBot.text = "Đã xảy ra lỗi"
                    }

                    else -> {}
                }
            }
        }
    }

    private fun initChatBotRv() {
        binding.rvChatChatBot.adapter = chatBotAdapter
        binding.rvChatChatBot.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


    private fun initChatSupport() {
        var textSent: String
        var time = ""
        initChatSupportRv()


//        val newMessage = Message("Hello from admin",false )
//        chatRef.push().setValue(newMessage)


        binding.btnChatSupport.setOnClickListener {
            if (binding.layoutChatSupport.visibility == View.GONE) {
                binding.layoutChatSupport.visibility = View.VISIBLE
            }
        }

        binding.btnHideChatSupport.setOnClickListener {
            if (binding.layoutChatSupport.visibility == View.VISIBLE) {
                binding.layoutChatSupport.visibility = View.GONE
            }
        }

        binding.btnSendChatSupport.setOnClickListener {
            textSent = binding.etChatSupport.text.toString().trim()
            if (textSent.isNotEmpty()) {
                chatRef.push().setValue(Message(textSent, 1))
                calendar = Calendar.getInstance()
                hour = calendar.get(Calendar.HOUR_OF_DAY) // Giờ (24h format)
                minute = calendar.get(Calendar.MINUTE)    // Phút
                nam = calendar[Calendar.YEAR]
                thang = calendar[Calendar.MONTH] // Tháng bắt đầu từ 0
                ngay = calendar[Calendar.DAY_OF_MONTH]

                if (minute < 10) {
                    time = "$hour:0$minute"
                } else {
                    time = "$hour:$minute"
                }
                historyRef.child(user.username).setValue(
                    History(
                        user.username, 0, time,
                        Convert.dinhDangNgayChat(ngay, thang, nam)
                    )
                )
                binding.etChatSupport.setText("")
            }
        }
    }

    private fun initChatSupportRv() {
        binding.rvChatSupport.adapter = chatSupportAdapter
        binding.rvChatSupport.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }


}