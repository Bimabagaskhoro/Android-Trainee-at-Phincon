package com.bimabagaskhoro.taskappphincon.firebase

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ItemPaymentBinding
import com.bumptech.glide.Glide

class PaymentBodyAdapter(listDataBody: List<DataItem>) : RecyclerView.Adapter<PaymentBodyAdapter.ViewHolder>() {
    private var listData: List<DataItem> = ArrayList()
    init {
        this.listData = listDataBody
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPaymentBinding.bind(itemView)
        fun bind(data: DataItem) {
            binding.apply {
                tvTittle.text = data.name
                when (data.status) {
                    true -> {
                        consPayment.setBackgroundColor(
                            Color.parseColor("#FFFFFF")
                        )
                    }
                    false -> {
                        consPayment.setBackgroundColor(
                            Color.parseColor("#A5A5A5")
                        )
                    }
                    else -> {
                        Log.d("TodoColor", "status payment available")
                    }
                }


                when (data.id) {
                    "va_bca" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.bca)
                            .into(imgPayment)
                    }
                    "va_mandiri" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.mandiri)
                            .into(imgPayment)
                    }
                    "va_bri" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.bri)
                            .into(imgPayment)
                    }
                    "va_bni" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.bni)
                            .into(imgPayment)
                    }
                    "va_btn" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.btn)
                            .into(imgPayment)
                    }
                    "va_danamon" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.danamon)
                            .into(imgPayment)
                    }
                    "ewallet_gopay" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.gopay)
                            .into(imgPayment)
                    }
                    "ewallet_ovo" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.ovo)
                            .into(imgPayment)
                    }
                    "ewallet_dana" -> {
                        Glide.with(itemView.context)
                            .asBitmap()
                            .load(R.drawable.dana)
                            .into(imgPayment)
                    }
                }
            }
        }

    }
}