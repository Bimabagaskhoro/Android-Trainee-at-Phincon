package com.bimabagaskhoro.phincon.features.notification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bimabagaskhoro.phincon.core.adapter.NotificationAdapter
import com.bimabagaskhoro.phincon.core.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.phincon.core.vm.FGAViewModel
import com.bimabagaskhoro.phincon.core.vm.LocalViewModel
import com.bimabagaskhoro.phincon.features.notification.databinding.ActivityNotificationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class NotificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private lateinit var adapter: NotificationAdapter
    private val roomViewModel: LocalViewModel by viewModels()
    private var isMultipleSelect = false
    private lateinit var menuNotification: Menu
    private val analyticViewModel: FGAViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        initObserver()
    }

    private fun initAdapter() {
        binding.apply {
            adapter = NotificationAdapter(
                context = this@NotificationActivity,
                isMultipleSelect = isMultipleSelect,
                onItemClicked = { onNotificationItemClicked(it) },
                onCheckboxChecked = { onCheckboxChecked(it) }
            )
            rvNotification.adapter = adapter
            rvNotification.setHasFixedSize(true)
        }
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
                lifecycleScope.launch {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        roomViewModel.getAllNotification().collectLatest { result ->
                            if (result.isEmpty()) {
                                isEmpty()
                            }
                        }
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    android.R.id.home -> {
                        onBackPressed()
                    }
                    R.id.menu_check_notification -> {
                        showMultipleSelect()
                        analyticViewModel.onClickMultipleSelectIconNotification()
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

    private fun isEmpty() {
        menuNotification.findItem(R.id.menu_read_notification)?.isVisible = false
        menuNotification.findItem(R.id.menu_delete_notification)?.isVisible = false
        menuNotification.findItem(R.id.menu_check_notification)?.isVisible = false
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
        val countRead = roomViewModel.countNotification()
        Log.d("countReadNotification", "$countRead")
        analyticViewModel.onClickReadIconNotification(countRead)
        onBackPressed()
    }

    private fun deleteNotification() {
        roomViewModel.deleteNotification(true)
        val countRead = roomViewModel.countNotification()
        Log.d("countReadNotification", "$countRead")
        analyticViewModel.onClickDeleteIconNotification(countRead)
        onBackPressed()
    }


    private fun initObserver() {
        initAdapter()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                roomViewModel.getAllNotification().collectLatest { result ->
                    if (result.isNotEmpty()) {
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
            .setPositiveButton("Ok") { _, _ ->
                data.notificationTitle?.let { dataTittle ->
                    data.notificationBody?.let { dataBody ->
                        analyticViewModel.onClickItemNotification(
                            dataTittle,
                            dataBody
                        )
                    }
                }
            }
            .show()
    }

    private fun onCheckboxChecked(data: NotificationEntity) {
        val productId = data.id
        val isChecked = !data.isChecked
        roomViewModel.updateCheckedNotification(isChecked, productId)
        data.notificationTitle?.let { dataTittle ->
            data.notificationBody?.let { dataBody ->
                analyticViewModel.onSelectCheckBoxNotification(
                    dataTittle,
                    dataBody
                )
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if (isMultipleSelect) {
            showMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
            analyticViewModel.onClickBackNotification()
        }
        roomViewModel.setAllUncheckedNotification()
        return true
    }

    override fun onBackPressed() {
        if (isMultipleSelect) {
            showMultipleSelect()
        } else {
            onBackPressedDispatcher.onBackPressed()
            analyticViewModel.onClickBackNotification()
        }
        roomViewModel.setAllUncheckedNotification()
    }

    override fun onResume() {
        super.onResume()
        val nameScreen = this.javaClass.simpleName
        analyticViewModel.onLoadScreenNotification(nameScreen)
    }
}