package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.R
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

        binding.apply {
            btnBack.setOnClickListener {
                val intent = Intent(this@NotificationActivity, MainActivity::class.java)
                startActivity(intent)
            }
            icCheckedFirst.setOnClickListener {
                appBarLayout.visibility = View.GONE
                appBarLayout2.visibility = View.VISIBLE
                roomViewModel.viewCheckBoxAnimation(1)
            }

            btnBack2.setOnClickListener {
                appBarLayout.visibility = View.VISIBLE
                appBarLayout2.visibility = View.GONE
                roomViewModel.viewCheckBoxAnimation(0)
            }
            checkBoxNotification.setOnCheckedChangeListener { _, isChecked ->
                if (!isChecked) {
//                    roomViewModel.checkAllNotification(0)
                    btnDelete.isClickable = false
                } else if (isChecked) {
                    initCheckBox(isChecked)
                }
            }
        }

    }

    private fun initCheckBox(checked: Boolean) {
        if (checked) {
            roomViewModel.apply {
                checkAllNotification(1)
//                updateCheckNotification(0, 1)
            }
//            binding.btnDelete.setOnClickListener {
//                AlertDialog.Builder(this)
//                    .setTitle("Delete")
//                    .setMessage("Are U Sure Wanna Clear All Notification")
//                    .setPositiveButton(R.string.ok) { _, _ ->
//                        roomViewModel.deleteNotification()
//                        val intent = Intent(this@NotificationActivity, MainActivity::class.java)
//                        startActivity(intent)
//                    }
//                    .show()
//                Log.d("afterdelete", "${roomViewModel.deleteNotification()}")
//            }
        }
    }

    private fun doActionAdapter() {
        adapter = NotificationAdapter(
            { data ->
                if (data.isRead == 0) {
                    roomViewModel.isRead(1, data.id)
                }
                val tittleMsg = data.tittle_notification
                val bodyMsg = data.body_notification
                val count1 = roomViewModel.getTotalIsReadNotification()
                val math2 = count1 - count1
                initDialog(math2, data.id, tittleMsg, bodyMsg)
                roomViewModel

            },
            { data ->
                val id = data.id
                roomViewModel.updateCheckNotification(id, 1)
                binding.btnDelete.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are U Sure Wanna Delete This Notification")
                        .setPositiveButton(R.string.ok) { _, _ ->
//                            roomViewModel.deleteNotifications(id)
                            roomViewModel.deleteNotification()
                        }
                        .show()
                }
            },
            { data ->
                val id = data.id
                roomViewModel.updateCheckNotification(id, 0)
            })
    }

    private fun initDialog(math2: Int, id: Int, tittleMsg: String, bodyMsg: String) {
        AlertDialog.Builder(this)
            .setTitle(tittleMsg)
            .setMessage(bodyMsg)
            .setPositiveButton(R.string.ok) { _, _ ->
                roomViewModel.updateTotalNotification(math2, id)
            }
            .show()
    }

    private fun initData() {
        roomViewModel.getAllNotification.observe(this@NotificationActivity) { data ->
            if (data.isNotEmpty()) {
                adapter.setData(data.sortedBy { it.isRead })
                Log.d("datadatdtadtadta", "$data")
                binding.apply {
                    rvNotification.adapter = adapter
                    rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
                    rvNotification.setHasFixedSize(true)
                    viewEmptyData.root.visibility = View.GONE
                }
            } else if (data.isEmpty()) {
                binding.viewEmptyData.root.visibility = View.VISIBLE
                Log.d("emptydatanotif", "Empty Data")
            }
        }
    }
}