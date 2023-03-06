package com.bimabagaskhoro.phincon.core.adapter.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.phincon.core.R
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.phincon.core.databinding.ItemProductBinding
import com.bimabagaskhoro.phincon.core.utils.formatterIdr
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
                getItem(bindingAdapterPosition)?.let { data -> onClick?.invoke(data) }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemProduct>() {
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