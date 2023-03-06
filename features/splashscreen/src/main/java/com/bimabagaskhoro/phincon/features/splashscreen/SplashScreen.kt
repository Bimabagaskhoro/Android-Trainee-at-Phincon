package com.bimabagaskhoro.phincon.features.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.bimabagaskhoro.phincon.core.data.pref.AuthPreferences
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.features.splashscreen.databinding.ActivitySplashScreenBinding
import com.bimabagaskhoro.phincon.router.ActivityRouter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var router: ActivityRouter

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
                    startActivity(router.toHomeActivity(this).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                } else {
                    startActivity(router.toAuthActivity(this).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
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