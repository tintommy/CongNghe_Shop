package com.example.tttn_electronicsstore_manager_admin_app.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.tttn_electronicsstore_manager_admin_app.R
import com.example.tttn_electronicsstore_manager_admin_app.databinding.ActivityAdminBinding
import com.example.tttn_electronicsstore_manager_admin_app.databinding.ActivityShipperBinding
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.CaNhanFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyDonHangFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyKhachHangFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyLoaiFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyPhieuNhapFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLySanPhamFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.QuanLyThuongHieuFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.adminFragments.TinNhanFragment
import com.example.tttn_electronicsstore_manager_admin_app.fragments.shipperFragments.DanhSachDonHangFragment
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
@AndroidEntryPoint
class ShipperActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener{
    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: ActivityShipperBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityShipperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        supportFragmentManager.popBackStackImmediate(
            null, FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        when (p0.itemId) {

            R.id.navDonHangShip -> {
                val danhSachDonHangFragment = DanhSachDonHangFragment()
                replaceFragment(danhSachDonHangFragment)


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
}