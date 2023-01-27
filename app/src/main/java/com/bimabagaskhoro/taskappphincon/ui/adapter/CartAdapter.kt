package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.databinding.ItemCartBinding
import com.bimabagaskhoro.taskappphincon.ui.activity.CartActivity
import com.bimabagaskhoro.taskappphincon.ui.detail.BuyDialogViewModel
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.vm.LocalViewModel
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
@Suppress("DEPRECATION")
class CartAdapter(
    private val onDeleteItem: (Int) -> Unit,
    private val onCheckedItem: (Any, String, Int) -> Unit,
    private val onUnCheckedItem: (Any, String, Int) -> Unit,
    private val onAddQuantity: (Int, Int) -> Unit,
    private val onMinQuantity: (Int, Int) -> Unit,
) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private var listData = ArrayList<CartEntity>()
    var onItemClick: ((CartEntity) -> Unit)? = null
    var totalValue: Int = 0
    var isCheckedAll: Boolean = false

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
                checkBox.isChecked = isCheckedAll

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        val priceValue = data.harga.toInt()
                        val quantityValue = data.quantity
                        val result = (priceValue * quantityValue)
                        totalValue += result
                        onCheckedItem(totalValue, data.id.toString(), data.quantity)
                    } else if (!isChecked) {
                        val priceValue = data.harga.toInt()
                        val quantityValue = data.quantity
                        val result = (priceValue * quantityValue)
                        totalValue -= result
                        onUnCheckedItem(totalValue, data.id.toString(), data.quantity)
                    }
                }
                addFragmentDialog.setOnClickListener {
                    val stock = data.stock
                    val quantity = (data.quantity)
                    if (quantity == stock) {
                        addFragmentDialog.isClickable = false
                        Toast.makeText(itemView.context, R.string.out_stock, Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        onAddQuantity(data.id, data.quantity)
                        binding.addFragmentDialog.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.bg_rounded_lightgrey
                            )
                    }
                }
                minFragmentDialog.setOnClickListener {
                    val ids = data.id
                    val quantity = (data.quantity)
                    if (quantity != 1) {
                        onMinQuantity(ids, quantity)
                    } else {
                        minFragmentDialog.isClickable = false
                        binding.minFragmentDialog.background =
                            ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.bg_rounded_lightgrey
                            )
                    }
                }
            }
        }
    }

    fun selectAll(isChecked: Boolean) {
        isCheckedAll = isChecked
        notifyDataSetChanged()
    }

}