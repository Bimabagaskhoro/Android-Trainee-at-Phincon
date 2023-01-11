package com.bimabagaskhoro.taskappphincon.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.lifecycle.asLiveData
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreference
import com.bimabagaskhoro.taskappphincon.databinding.ActivitySplashScreenBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var authPreference: AuthPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        binding.imgSplash.startAnimation(animation)
        authPreference = AuthPreference(baseContext)

        val isLoggedIn = authPreference.isLoggedIn.asLiveData()
        Handler(Looper.getMainLooper()).postDelayed({
            isLoggedIn.observe(this) {
                if (it) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, AuthActivity::class.java))
                    finish()
                }
            }
        }, 3000)
    }
}