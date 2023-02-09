package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.databinding.ItemNotificationBinding

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class NotificationAdapter(
    private val onClicked: (NotificationEntity) -> Unit,
    private val onCheckedItem: (NotificationEntity) -> Unit,
    private val onUnCheckedItem: (NotificationEntity) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var listData = ArrayList<NotificationEntity>()

    fun setData(newListData: List<NotificationEntity>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemNotificationBinding.bind(itemView)
        fun bind(data: NotificationEntity) {
            binding.apply {
                tvTittleNotif.isSelected = true
                tvTittleNotif.text = data.tittle_notification
                tvBodyNotif.text = data.body_notification
                tvCalendarNotif.text = data.timestamp_notification

                // item 2
                tvTittleNotif2.isSelected = true
                tvTittleNotif2.text = data.tittle_notification
                tvBodyNotif2.text = data.body_notification
                tvCalendarNotif2.text = data.timestamp_notification

                if (data.isRead == 1) {
                    cardNotif.setBackgroundColor(
                        Color.parseColor("#FFFFFF")
                    )
                    cardNotif2.setBackgroundColor(
                        Color.parseColor("#FFFFFF")
                    )
                } else if (data.isRead == 0) {
                    cardNotif.setBackgroundColor(
                        Color.parseColor("#A7CFFF")
                    )
                    cardNotif2.setBackgroundColor(
                        Color.parseColor("#A7CFFF")
                    )
                }

                if (data.isCheck == 1) {
                    binding.checkBox.isChecked = true
                } else if (data.isCheck == 0) {
                    binding.checkBox.isChecked = false
                }

                if (data.isState == 1) {
                    cardHelper1.visibility = View.VISIBLE
                    cardHelper2.visibility = View.GONE
                } else if (data.isState == 0) {
                    cardHelper1.visibility = View.GONE
                    cardHelper2.visibility = View.VISIBLE
                }

                cardNotif.setOnClickListener {
                    onClicked.invoke(data)
                }
                cardNotif2.setOnClickListener {
                    onClicked.invoke(data)
                }

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        onCheckedItem.invoke(listData[adapterPosition])
                    } else if (!isChecked) {
                        onUnCheckedItem.invoke(listData[adapterPosition])
                    }
                }
            }
        }
    }
}