package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bimabagaskhoro.taskappphincon.R
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ImageProductItem
import com.bumptech.glide.Glide
import java.util.*

class ImageSliderAdapter(
    val context: Context,
    private val imgList: List<ImageProductItem>,
    private val listener: OnPageClickListener
) : PagerAdapter(){

    override fun getCount(): Int {
        return imgList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemView: View = mLayoutInflater.inflate(R.layout.item_slide, container, false)
        val imageView: ImageView = itemView.findViewById<View>(R.id.tv_item_img_slider) as ImageView
        val tvItemImgTitle: TextView = itemView.findViewById(R.id.tv_item_img_title) as TextView

        Glide.with(itemView.context)
            .load(imgList[position].image_product)
            .into(imageView)
        Objects.requireNonNull(container).addView(itemView)
        tvItemImgTitle.text = imgList[position].title_product

        itemView.setOnClickListener {
            listener.onClick(imgList[position].image_product)
        }


        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    interface OnPageClickListener {
        fun onClick(image: String)
    }

}
