package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ImageProductItem
import com.bimabagaskhoro.taskappphincon.databinding.ItemSlideBinding
import com.bumptech.glide.Glide

class ImageSliderAdapter(private val items : List<ImageProductItem>
) : RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ItemSlideBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ImageViewHolder(itemView: ItemSlideBinding) : RecyclerView.ViewHolder(itemView.root) {
        private val binding = itemView
        fun bind(data: ImageProductItem) {
            binding.apply {
                Glide.with(itemView)
                    .load(data.image_product)
                    .into(imgSlider)
                tvHelpersCard.text = data.title_product
            }
        }
    }

}