package com.bimabagaskhoro.taskappphincon.firebase

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ItemPaymentHeaderBinding

class PaymentHeaderAdapter : RecyclerView.Adapter<PaymentHeaderAdapter.ViewHolder>() {
    private val listDataHeader = ArrayList<PaymentModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(users: List<PaymentModel>) {
        listDataHeader.clear()
        listDataHeader.addAll(users)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_payment_header, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listDataHeader[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = listDataHeader.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemPaymentHeaderBinding.bind(itemView)
        fun bind(data: PaymentModel) {

            binding.apply {
                tvPaymentItemHeader.text = data.type.toString()
                val paymentAdapter = PaymentBodyAdapter(data.data)
                rvItemBodyPayment.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
                rvItemBodyPayment.adapter = paymentAdapter

            }
        }
    }
}