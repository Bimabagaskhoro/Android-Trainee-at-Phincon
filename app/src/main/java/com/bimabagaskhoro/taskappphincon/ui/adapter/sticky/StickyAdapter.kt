package com.bimabagaskhoro.taskappphincon.ui.adapter.sticky

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.databinding.ItemProductBinding
import com.bimabagaskhoro.taskappphincon.databinding.ItemStickyBinding
import com.bimabagaskhoro.taskappphincon.utils.StickyModel
import com.bimabagaskhoro.taskappphincon.utils.formatDate
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class StickyAdapter(var listData: List<StickyModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NormalViewHolder) {
            val data = listData[position]
            holder.bind(data)
            return
        }

        if (holder is StickyViewHolder) {
            val data = listData[position]
            holder.bind(data)
            return
        }
    }

    override fun getItemCount() = listData.size

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].isSticky) {
            STICKY_TYPE
        } else {
            NORMAL_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return if (viewType == STICKY_TYPE) {
            StickyViewHolder(inflater.inflate(R.layout.item_sticky, parent, false))
        } else {
            NormalViewHolder(inflater.inflate(R.layout.item_product, parent, false))
        }
    }


    inner class StickyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), StickyHeader {
        private val binding = ItemStickyBinding.bind(itemView)
        fun bind(data: StickyModel) {
            binding.apply {
                tvTittleSticky.text = data.toolbar
            }
        }
        override val stickyId: String
            get() = listData[adapterPosition].toolbar
    }

    inner class NormalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemProductBinding.bind(itemView)
        fun bind(data: StickyModel) {
            binding.apply {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(data.itemSticky.date)
                    .placeholder(R.drawable.ic_broken_image)
                    .into(imgProduct)
                tvTittleProduct.text = data.itemSticky.name_product
//                tvPriceProduct.text = data.itemSticky.harga.formatterIdr()
//                tvDateProduct.text = formatDate(data.itemSticky.date)
//                ratingBar.rating = data.itemSticky.rate.toFloat()
                imgBtnFavorite.visibility = View.INVISIBLE
            }
        }
    }

    companion object {
        const val STICKY_TYPE = 0
        const val NORMAL_TYPE = 1
    }
}