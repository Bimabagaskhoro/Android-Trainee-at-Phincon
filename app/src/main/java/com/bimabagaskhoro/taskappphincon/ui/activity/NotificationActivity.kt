package com.bimabagaskhoro.taskappphincon.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.databinding.ActivityNotificationBinding
import com.bimabagaskhoro.taskappphincon.ui.adapter.NotificationAdapter
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private var isMultipleSelect = false
    private lateinit var menuNotification: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        initObserver()
        initAdapter()
    }

    private fun initAdapter() {
        adapter = NotificationAdapter(
            context = this@NotificationActivity,
            isMultipleSelect = isMultipleSelect,
            onItemClicked = { onNotificationItemClicked(it) },
            onCheckboxChecked = { onCheckboxChecked(it) }
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarNotification)
        supportActionBar?.title = getString(R.string.notification)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.notification_menu, menu)
                menuNotification = menu
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        onBackPressed()
                    }
                    R.id.menu_check_notification -> {
                        showMultipleSelect()
                    }
                    R.id.menu_read_notification -> {
                        readNotification()
                    }
                    R.id.menu_delete_notification -> {
                        deleteNotification()
                    }
                }
                return true
            }
        })
    }

    private fun showMultipleSelect() {
        isMultipleSelect = !isMultipleSelect
        initObserver()

        if (isMultipleSelect) {
            menuNotification.findItem(R.id.menu_read_notification)?.isVisible = true
            menuNotification.findItem(R.id.menu_delete_notification)?.isVisible = true
            menuNotification.findItem(R.id.menu_check_notification)?.isVisible = false

            supportActionBar?.title = getString(R.string.multiple_select)
        } else {
            menuNotification.findItem(R.id.menu_read_notification)?.isVisible = false
            menuNotification.findItem(R.id.menu_delete_notification)?.isVisible = false
            menuNotification.findItem(R.id.menu_check_notification)?.isVisible = true

            supportActionBar?.title = getString(R.string.notification)
        }
    }

    private fun readNotification() {
        roomViewModel.setAllReadNotification(true)
        onBackPressed()
    }

    private fun deleteNotification() {
        roomViewModel.deleteNotification(true)
        onBackPressed()
    }


    private fun initObserver() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomViewModel.getAllNotification().collectLatest { result ->
                    if (result.isNotEmpty()) {
                        initAdapter()
                        binding.rvNotification.adapter = adapter
                        binding.rvNotification.setHasFixedSize(true)
                        adapter.setData(result)

                        binding.rvNotification.visibility = View.VISIBLE
                        binding.emptyData.visibility = View.GONE
                    } else {
                        binding.rvNotification.visibility = View.GONE
                        binding.emptyData.visibility = View.VISIBLE
                    }
                }
            }
        }
    }



    private fun onNotificationItemClicked(data: NotificationEntity) {
        roomViewModel.updateReadNotification(true, data.id)
        androidx.appcompat.app.AlertDialog.Builder(this@NotificationActivity)
            .setTitle(data.notificationTitle)
            .setMessage(data.notificationBody)
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    private fun onCheckboxChecked(data: NotificationEntity) {
        val productId = data.id
        val isChecked = !data.isChecked
        roomViewModel.updateCheckedNotification(isChecked, productId)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isMultipleSelect) {
            showMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        roomViewModel.setAllUncheckedNotification()
        return true
    }

    override fun onBackPressed() {
        if (isMultipleSelect) {
            showMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
        roomViewModel.setAllUncheckedNotification()
    }
}