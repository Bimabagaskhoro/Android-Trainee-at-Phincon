package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.databinding.ItemProductBinding
import com.bimabagaskhoro.taskappphincon.utils.formatDate
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
class ProductOtherAdapter : RecyclerView.Adapter<ProductOtherAdapter.ViewHolder>() {
    private var listData = ArrayList<DataItemProduct>()
    var onItemClick: ((DataItemProduct) -> Unit)? = null

    fun setData(newListData: List<DataItemProduct>?) {
        if (newListData == null) return
        listData.clear()
        listData.addAll(newListData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listData[position]
        holder.bind(data)
    }

    override fun getItemCount() = listData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductBinding.bind(itemView)
        fun bind(data: DataItemProduct) {
            binding.apply {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(data.image)
                    .placeholder(R.drawable.ic_broken_image)
                    .into(imgProduct)
                tvTittleProduct.text = data.name_product
                tvPriceProduct.text = data.harga.formatterIdr()
                tvDateProduct.text = formatDate(data.date)
                ratingBar.rating = data.rate.toFloat()
                imgBtnFavorite.visibility = View.VISIBLE
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }

}