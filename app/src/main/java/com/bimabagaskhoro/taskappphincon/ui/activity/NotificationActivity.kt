package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.databinding.ActivityNotificationBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.NotificationAdapter
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private val roomViewModel: LocalViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NotificationAdapter()
        initData()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initData() {
        roomViewModel.getAllNotification.observe(this@NotificationActivity) {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                binding.apply {
                    rvNotification.adapter = adapter
                    rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
                    rvNotification.setHasFixedSize(true)
                    adapter.onItemClick = {
                    }
                }
            }
        }
    }
}