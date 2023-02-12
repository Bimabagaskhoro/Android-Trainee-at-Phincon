package com.bimabagaskhoro.taskappphincon.ui.adapter.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.databinding.ItemProductBinding
import com.bimabagaskhoro.taskappphincon.utils.formatDate
import com.bimabagaskhoro.taskappphincon.utils.formatterIdr
import com.bumptech.glide.Glide

class ProductAdapter :
    PagingDataAdapter<DataItemProduct, ProductAdapter.MyViewHolder>(DIFF_CALLBACK) {

    var onClick: ((DataItemProduct) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (getItem(position) != null) {
            holder.bind(getItem(position)!!)
        }
    }

    inner class MyViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataItemProduct) {
            binding.apply {
                Glide.with(itemView.context)
                    .asBitmap()
                    .load(data.image)
                    .placeholder(R.drawable.ic_broken_image)
                    .into(imgProduct)
                tvTittleProduct.text = data.name_product
                tvPriceProduct.text = data.harga?.formatterIdr()
                tvDateProduct.text = data.date?.let { formatDate(it) }
                ratingBar.rating = data.rate?.toFloat()!!
                imgBtnFavorite.visibility = View.INVISIBLE
            }
        }

        init {
            binding.root.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { data -> onClick?.invoke(data) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemProduct>() {
            override fun areItemsTheSame(
                oldItem: DataItemProduct,
                newItem: DataItemProduct
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItemProduct,
                newItem: DataItemProduct
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}