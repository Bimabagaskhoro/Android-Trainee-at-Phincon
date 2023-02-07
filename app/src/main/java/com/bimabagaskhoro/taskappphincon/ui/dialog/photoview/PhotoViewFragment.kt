package com.bimabagaskhoro.taskappphincon.ui.dialog.photoview

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import com.bimabagaskhoro.taskappphincon.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

class PhotoViewFragment(private val mActivity: Activity) {
    fun showPhoto(photoURL: String){
        val view = View.inflate(mActivity, R.layout.fragment_photo_view, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(mActivity)
        builder.setView(view)

        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        val ivPhoto = dialog.findViewById<ImageView>(R.id.photo_view_touch)

        Glide.with(mActivity)
            .load(photoURL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivPhoto!!)
    }

}