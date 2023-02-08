package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.databinding.ItemNotificationBinding

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class NotificationAdapter(
    private val onClicked: (NotificationEntity) -> Unit
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
                if (data.isRead == 1) {
                    cardNotif.setBackgroundColor(
                        Color.parseColor("#FFFFFF")
                    )
                } else if (data.isRead == 0) {
                    cardNotif.setBackgroundColor(
                        Color.parseColor("#A7CFFF")
                    )
                }

                cardNotif.setOnClickListener {
                    onClicked.invoke(listData[adapterPosition])
                }
            }
        }
    }
}