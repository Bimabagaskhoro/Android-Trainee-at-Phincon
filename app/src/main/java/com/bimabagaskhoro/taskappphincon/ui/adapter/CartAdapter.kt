package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.phincon.core.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ItemCartBinding
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
@Suppress("DEPRECATION")
class CartAdapter(
    private val onDeleteItem: (CartEntity) -> Unit,
    private val onAddQuantity: (CartEntity) -> Unit,
    private val onMinQuantity: (CartEntity) -> Unit,
    private val onCheckedItem: (CartEntity) -> Unit,
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private var listData = ArrayList<CartEntity>()
    var onItemClick: ((CartEntity) -> Unit)? = null

    fun setData(newListData: List<CartEntity>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)

    }

    override fun getItemCount(): Int = listData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemCartBinding.bind(itemView)
        fun bind(data: CartEntity) {
            binding.apply {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(data.image)
                    .placeholder(R.drawable.ic_broken_image)
                    .into(imgProduct)
                tvTittleProduct.isSelected = true
                tvTittleProduct.text = data.nameProduct
                tvPriceProduct.text = data.price?.formatterIdr()
                tvTotalNumber.text = data.quantity.toString()

                checkBox.isChecked = data.isChecked
                checkBox.setOnClickListener { onCheckedItem.invoke(data) }
                btnDelete.setOnClickListener {
                    onDeleteItem.invoke(data)
                }

                addQty.setOnClickListener {
                    data.stock?.let {
                        if (tvTotalNumber.text.toString().toInt() < it) {
                            onAddQuantity.invoke(data)
                        }
                    }
                }

                minQty.setOnClickListener {
                    if (tvTotalNumber.text.toString().toInt() > 1) {
                        onMinQuantity.invoke(data)
                    }
                }

                data.stock?.let {
                    when (tvTotalNumber.text.toString().toInt()) {
                        it -> {
                            binding.addQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_lightgrey)
                            binding.minQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_black)
                        }
                        1 -> {
                            binding.addQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_black)
                            binding.minQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_lightgrey)
                        }
                        else -> {
                            binding.addQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_black)
                            binding.minQty.background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_rounded_black)
                        }
                    }
                }
            }
        }
    }
}