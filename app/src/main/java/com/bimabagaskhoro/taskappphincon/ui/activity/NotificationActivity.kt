package com.bimabagaskhoro.taskappphincon.ui.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.databinding.ActivityCartBinding
import com.bimabagaskhoro.taskappphincon.databinding.ActivityNotificationBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.NotificationAdapter
import com.bimabagaskhoro.taskappphincon.vm.DataStoreViewModel
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private val dataStoreViewModel: DataStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doActionAdapter()
        initData()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this@NotificationActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun doActionAdapter() {
        adapter = NotificationAdapter{ data ->
            if (data.isRead == 0) {
                roomViewModel.isRead(1, data.id)
            }

            Log.d("isRead", "Done isRead")
            val count1 = roomViewModel.getTotalIsReadNotification()
            val count2 = data.total_notification
            val math = count1 - count2
            val math2 = count1 - count1
            data.total_notification
            roomViewModel.updateTotalNotification(math2, data.id)

            Log.d("isMath", "$count1  $count2 $math ${data.total_notification} $math2")

        }
    }

    private fun initData() {
        roomViewModel.getAllNotification.observe(this@NotificationActivity) {
            if (it.isNotEmpty()) {
                adapter.setData(it)
                Log.d("datadatdtadtadta", "$it")
                binding.apply {
                    rvNotification.adapter = adapter
                    rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
                    rvNotification.setHasFixedSize(true)
                }
            }
        }
    }
}