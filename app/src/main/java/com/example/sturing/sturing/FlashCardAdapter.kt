package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import kotlinx.android.synthetic.main.flash_card_item.view.*

class FlashCardAdapter(var items: ArrayList<FlashCard>, var context: Context, var group: String): RecyclerView.Adapter<FlashCardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.flash_card_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        if (items[p1].image != null) {
            GlideApp.with(context)
                    .load(items[p1].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(p0.ivImage)
        }
        p0.tvTitle?.text = items[p1].title
        p0.tvSubtitle?.text = items[p1].subTitle
        p0.tvDescription?.text = items[p1].description
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.ivImage
        val tvTitle = view.tvTitle
        val tvSubtitle = view.tvSubTitle
        val tvDescription = view.tvDescription
    }

}