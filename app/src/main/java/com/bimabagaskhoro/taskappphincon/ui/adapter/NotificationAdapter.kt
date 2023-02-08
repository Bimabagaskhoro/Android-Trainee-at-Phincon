package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.databinding.ItemNotificationBinding

@SuppressLint("NotifyDataSetChanged")
class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    private var listData = ArrayList<NotificationEntity>()
    var onItemClick: ((NotificationEntity) -> Unit)? = null

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
            }
        }

        init {
            binding.root.setOnClickListener {
                itemView.setBackgroundColor(Color.BLUE)
//                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }
}