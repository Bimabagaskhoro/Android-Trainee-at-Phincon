package com.bimabagaskhoro.taskappphincon.feature.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.phincon.core.data.source.local.model.NotificationEntity
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ItemNotificationBinding

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class NotificationAdapter(
    private val context: Context,
    private val isMultipleSelect: Boolean,
    private val onItemClicked: (NotificationEntity) -> Unit,
    private val onCheckboxChecked: (NotificationEntity) -> Unit
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

                tvNotificationTitle.text = data.notificationTitle
                tvNotificationBody.text = data.notificationBody
                tvNotificationDate.text = data.notificationDate

                if (isMultipleSelect) {
                    cbNotification.visibility = View.VISIBLE
                    cbNotification.isChecked = data.isChecked
                } else {
                    cbNotification.visibility = View.GONE
                }

                cvNotification.setCardBackgroundColor(
                    if (data.isRead) Color.WHITE
                    else ContextCompat.getColor(context, R.color.blue_card)
                )

                cvNotification.setOnClickListener {
                    if (!isMultipleSelect) {
                        onItemClicked.invoke(data)
                    }
                }

                cbNotification.setOnClickListener {
                    onCheckboxChecked.invoke(data)
                }
            }
        }
    }
}