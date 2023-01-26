package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
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
    private val onCheckedItem: (CartEntity) -> Unit,
    private val onUnCheckedItem: (Any) -> Unit,
    private val onAddQuantity: (CartEntity) -> Unit,
    private val onMinQuantity: (CartEntity) -> Unit
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
                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        checkBox.isChecked = true
                        onCheckedItem.invoke(listData[adapterPosition])
                    } else if (!isChecked) {
                        checkBox.isChecked = false
                        onUnCheckedItem(data)
                    }
                }
                addFragmentDialog.setOnClickListener {
                    onAddQuantity.invoke(listData[adapterPosition])
                }
                minFragmentDialog.setOnClickListener {
                    onMinQuantity.invoke(listData[adapterPosition])
                }
            }
        }
    }
}