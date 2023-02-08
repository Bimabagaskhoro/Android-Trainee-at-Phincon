package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ActivityMainBinding
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val roomViewModel: LocalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_fav, R.id.navigation_user
            )
        )
        navView.setupWithNavController(navController)

        setupWindow()

        val countBadges = roomViewModel.countAllCart
        val countBadgesNotification = roomViewModel.countAllNotification

        binding.apply {
            if (countBadges == 0) {
                imgBadges.visibility = View.INVISIBLE
                tvBadgesMenu.visibility = View.INVISIBLE
            } else {
                imgBadges.visibility = View.VISIBLE
                tvBadgesMenu.visibility = View.VISIBLE
                tvBadgesMenu.text = countBadges.toString()
            }
            icCart.setOnClickListener {
                startActivity(Intent(this@MainActivity, CartActivity::class.java))
                finish()
            }

            /**
             *
             */
            if (countBadgesNotification == 0) {
                imgBadgesNotification.visibility = View.INVISIBLE
                tvBadgesNotification.visibility = View.INVISIBLE
            } else {
                imgBadgesNotification.visibility = View.VISIBLE
                tvBadgesNotification.visibility = View.VISIBLE
                tvBadgesNotification.text = countBadgesNotification.toString()
            }
            icNotification.setOnClickListener {
                startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
                finish()
            }
        }
    }

    private fun setupWindow() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}