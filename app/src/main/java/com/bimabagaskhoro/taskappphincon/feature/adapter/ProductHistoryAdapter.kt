package com.bimabagaskhoro.taskappphincon.feature.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.databinding.ItemProductBinding
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class ProductHistoryAdapter : RecyclerView.Adapter<ProductHistoryAdapter.ViewHolder>() {
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
                tvPriceProduct.text = data.harga?.formatterIdr()
                tvDateProduct.text = data.date?.let {
                    com.bimabagaskhoro.phincon.core.utils.formatDate(
                        it
                    )
                }
                ratingBar.rating = data.rate?.toFloat()!!
                imgBtnFavorite.visibility = View.INVISIBLE
            }
        }

        init {
            binding.root.setOnClickListener {
                onItemClick?.invoke(listData[adapterPosition])
            }
        }
    }
}