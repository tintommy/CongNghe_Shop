package com.example.tttn_electronicsstore_customer_app.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.tttn_electronicsstore_customer_app.R
import com.example.tttn_electronicsstore_customer_app.models.Image

class ImageAdapter(
    private val mContext: Context,
    private val imageList: List<Image>
) : PagerAdapter() {


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(R.layout.item_photo, container,false)
        val imageView: ImageView = view.findViewById(R.id.ivPhoto)

        val image = imageList.get(position)
        Glide.with(mContext).load(image.link).into(imageView)

        container.addView(view)
        return view
    }

    override fun getCount(): Int {
        if (!imageList.isEmpty())
            return imageList.size
        return 0
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}