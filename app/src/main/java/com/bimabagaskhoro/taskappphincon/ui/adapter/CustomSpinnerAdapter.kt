package com.bimabagaskhoro.taskappphincon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bimabagaskhoro.taskappphincon.R

@SuppressLint("ViewHolder", "InflateParams")
class CustomSpinnerAdapter(
    internal var context: Context,
    internal var images: IntArray,
    internal var languages: Array<String>
) : BaseAdapter() {

    private var inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return images.size
    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(i: Int, p1: View?, p2: ViewGroup?): View {
        val view = inflater.inflate(R.layout.spinner_custom, null)
        val icon = view.findViewById<View>(R.id.img_language) as ImageView?
        val names = view.findViewById<View>(R.id.tv_language) as TextView?
        icon!!.setImageResource(images[i])
        names!!.text = languages[i]
        view.setPadding(0, view.paddingTop, 0, view.paddingBottom)
        return view
    }

}