package com.example.tttn_electronicsstore_manager_admin_app.activity

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.databinding.ActivityAdminBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.CaNhanFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyDonHangFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyKhachHangFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyLoaiFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyPhieuNhapFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLySanPhamFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyThuongHieuFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.TinNhanFragment
import com.example.tttn_electronicsstore_manager_admin_app.models.chat.Message
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AdminActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityAdminBinding

    val chatsRef = FirebaseDatabase.getInstance().getReference("chats")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        val fragmentTag = intent.getStringExtra("fragment")
        if (fragmentTag == "chatFragment") {
            val tinNhanFragment = TinNhanFragment()
            replaceFragment(tinNhanFragment)
        }
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)


        initChat()
    }


    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        supportFragmentManager.popBackStackImmediate(
            null, FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        when (p0.itemId) {

            R.id.navPhieuNhap -> {


                val phieuNhapFragment = QuanLyPhieuNhapFragment()
                replaceFragment(phieuNhapFragment)


            }

            R.id.navCaNhan -> {


                val caNhanFragment = CaNhanFragment()
                replaceFragment(caNhanFragment)


            }

            R.id.navThuongHieu -> {

                val thuongHieuFragment = QuanLyThuongHieuFragment()
                replaceFragment(thuongHieuFragment)

            }

            R.id.navLoai -> {
                val quanLyLoaiFragment = QuanLyLoaiFragment()
                replaceFragment(quanLyLoaiFragment)


            }

            R.id.navSanPham -> {
                val quanLySanPhamFragment = QuanLySanPhamFragment()
                replaceFragment(quanLySanPhamFragment)

            }

            R.id.navDonHang -> {
                val quanLyDonHangFragment = QuanLyDonHangFragment()
                replaceFragment(quanLyDonHangFragment)

            }

            R.id.navTinNhan -> {
                val tinNhanFragment = TinNhanFragment()
                replaceFragment(tinNhanFragment)

            }

            R.id.navTaiKhoanKhachHang -> {

                val khachHangFragment = QuanLyKhachHangFragment()
                replaceFragment(khachHangFragment)


            }


            R.id.navDangXuat -> {

                val editor = sharedPref.edit()
                editor.remove("token")
                editor.remove("username")
                editor.apply()

                Toast.makeText(this, "Đã đăng xuất ", Toast.LENGTH_SHORT).show()
                val intent = Intent(
                    this, LoginActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }

            else -> return false
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)


        return true
    }

    fun replaceFragment(fragment: Fragment) {

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }


    private fun initChat() {


        chatsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                showNotification(
//                    this@AdminActivity,
//                    "Tin nhắn mới",
//                    "Tin nhăn mới từ user ${snapshot.key}"
//                )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {


                val latestChild = snapshot.children.lastOrNull()
                if (latestChild != null) {

                    val message = latestChild.getValue(Message::class.java)
                    if (message!!.customer == 1) {
                        showNotification(
                            this@AdminActivity,
                            "Tin nhắn mới",
                            "Tin nhắn mới từ khách hàng ${snapshot.key}"
                        )
                    }

                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // This method is called when a child is removed.
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // This method is called when a child is moved.
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if any
            }
        })
    }


    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_id"
            val channelName = "Channel Name"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Channel Description"
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, title: String, message: String) {
        val channelId = "channel_id"

        val intent = Intent(context, AdminActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fragment", "chatFragment")
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Xây dựng thông báo
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.baseline_chat_24)
            .setContentTitle(title) // tiêu đề
            .setContentText(message) // nội dung
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // mức độ ưu tiên
            .setContentIntent(pendingIntent) // gán Intent
            .setAutoCancel(true) // tự động đóng sau khi nhấn

        // Hiển thị thông báo
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    this@AdminActivity,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            notify(1, builder.build()) // 1 là ID của thông báo
        }
    }
}