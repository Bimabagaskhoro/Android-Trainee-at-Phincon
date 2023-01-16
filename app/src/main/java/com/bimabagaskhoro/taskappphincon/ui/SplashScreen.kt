package com.bimabagaskhoro.taskappphincon.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.databinding.ActivitySplashScreenBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.MainActivity
import com.bimabagaskhoro.taskappphincon.vm.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var authPreferences: AuthPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authPreferences = AuthPreferences(this)

        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        binding.imgSplash.startAnimation(animation)
        initCredential()
    }

    private fun initCredential() {
        val isLoggedIn = authPreferences.isLoggedIn.asLiveData()
        Handler(Looper.getMainLooper()).postDelayed({
            isLoggedIn.observe(this) {
                if (it) {
                    startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this@SplashScreen, AuthActivity::class.java))
                    finish()
                }
            }
        }, 3500)
    }
}