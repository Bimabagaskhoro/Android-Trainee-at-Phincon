package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.databinding.ItemCartBinding
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
@Suppress("DEPRECATION")
class CartAdapter(
    private val onDeleteItem: (Int) -> Unit,
    private val onAddQuantity: (CartEntity) -> Unit,
    private val onMinQuantity: (CartEntity) -> Unit,
    private val onCheckedItem: (CartEntity) -> Unit,
    private val onUnCheckedItem: (CartEntity) -> Unit,
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
                tvTittleProduct.text = data.name_product
                tvPriceProduct.text = data.harga.formatterIdr()
                tvTotalNumber.text = data.quantity.toString()

                btnDelete.setOnClickListener {
                    onDeleteItem(data.id)
                }

                addFragmentDialog.setOnClickListener {
                    val stock = data.stock
                    val quantity = (data.quantity)
                    if (quantity == stock) {
                        addFragmentDialog.isClickable = false
                        Toast.makeText(itemView.context, R.string.out_stock, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        onAddQuantity.invoke(listData[adapterPosition])
                        binding.addFragmentDialog.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.bg_rounded_lightgrey
                            )
                    }
                }
                minFragmentDialog.setOnClickListener {
                    val quantity = (data.quantity)
                    if (quantity != 1) {
                        onMinQuantity.invoke(listData[adapterPosition])
                    } else {
                        minFragmentDialog.isClickable = false
                        binding.minFragmentDialog.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.bg_rounded_lightgrey
                            )
                    }
                }
                if (data.is_check == 1) {
                    binding.checkBox.isChecked = true
                } else if (data.is_check == 0) {
                    binding.checkBox.isChecked = false
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