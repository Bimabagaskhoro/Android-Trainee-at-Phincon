package com.bimabagaskhoro.taskappphincon.ui.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ActivityNotificationBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.NotificationAdapter
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val roomViewModel: LocalViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doActionAdapter()
        initData()

        val countBadgesNotification = roomViewModel.getTotalNotification()
        if (countBadgesNotification == 0) {
            binding.icCheckedFirst.visibility = View.GONE
        }

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

            binding.checkBoxNotification.setOnClickListener {
                roomViewModel.checkAllNotification(1)
//                roomViewModel.isRead(1, data.id)
                val countBadgesNotifications = roomViewModel.getTotalNotification()
                if (countBadgesNotifications != 0) {
                    binding.appBarLayout.visibility = View.VISIBLE
                    binding.appBarLayout2.visibility = View.GONE
                    roomViewModel.viewCheckBoxAnimation(0)
                }
            }

        }
    }

    private fun doActionAdapter() {
        adapter = NotificationAdapter(
            { data ->
                if (data.isRead == 0) {
                    data.id?.let { roomViewModel.isRead(1, it) }
                }
                val tittleMsg = data.tittle_notification
                val bodyMsg = data.body_notification
                val count1 = roomViewModel.getTotalIsReadNotification()
                val math2 = count1 - count1
                data.id?.let { tittleMsg?.let { it1 -> bodyMsg?.let { it2 ->
                    initDialog(math2, it, it1,
                        it2
                    )
                } } }

            },
            { data ->
                val id = data.id
                id?.let { roomViewModel.updateCheckNotification(it, 1) }
                binding.btnDelete.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("Delete")
                        .setMessage("Are U Sure Wanna Delete This Notification")
                        .setPositiveButton(R.string.ok) { _, _ ->
//                            roomViewModel.deleteNotifications(id)
                            roomViewModel.deleteNotification()
                            val countBadgesNotification = roomViewModel.getTotalNotification()
                            if (countBadgesNotification == 0) {
                                val intent =
                                    Intent(this@NotificationActivity, MainActivity::class.java)
                                startActivity(intent)
                            } else if (countBadgesNotification != 0) {
                                binding.appBarLayout.visibility = View.VISIBLE
                                binding.appBarLayout2.visibility = View.GONE
                                roomViewModel.viewCheckBoxAnimation(0)
                            }
                        }
                        .show()
                }
            },
            { data ->
                val id = data.id
                id?.let { roomViewModel.updateCheckNotification(it, 0) }
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
                binding.apply {
                    rvNotification.adapter = adapter
                    rvNotification.layoutManager = LinearLayoutManager(this@NotificationActivity)
                    rvNotification.setHasFixedSize(true)
                    viewEmptyData.root.visibility = View.GONE
                }
            } else if (data.isEmpty()) {
                binding.viewEmptyData.root.visibility = View.VISIBLE
            }
        }
    }
}