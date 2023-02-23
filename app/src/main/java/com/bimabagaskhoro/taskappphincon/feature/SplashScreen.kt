package com.bimabagaskhoro.taskappphincon.feature

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.bimabagaskhoro.phincon.core.data.pref.AuthPreferences
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ActivitySplashScreenBinding
import com.bimabagaskhoro.taskappphincon.feature.activity.AuthActivity
import com.bimabagaskhoro.taskappphincon.feature.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var authPreferences: AuthPreferences
    private val analyticViewModel: FGAViewModel by viewModels()

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

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadSplashScreen(nameScreen)
    }
}