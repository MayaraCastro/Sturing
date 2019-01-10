package com.example.sturing.sturing

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewCompat
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
        p0.tvTitle?.text = items[p1].title
        p0.tvSubtitle?.text = items[p1].subTitle
        p0.tvDescription?.text = items[p1].description

        if (items[p1].image != null) {
            ViewCompat.setTransitionName(p0.ivImage, items[p1].title!!.hashCode().toString())
            p0.ivImage.visibility = View.VISIBLE
            GlideApp.with(context)
                    .load(items[p1].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(p0.ivImage)
        } else {
            p0.ivImage.visibility = View.GONE
        }

        p0.cvCard.setOnClickListener {
            var i = Intent(p0.cvCard.context, DetailCardActivity::class.java)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("image", items[p1].image)
            i.putExtra("code", items[p1].title!!.hashCode().toString())
            i.putExtra("title", items[p1].title)
            i.putExtra("subTitle", items[p1].subTitle)
            i.putExtra("description", items[p1].description)
            i.putExtra("author", items[p1].userAuthor)
            i.putExtra("state", items[p1].state)
            i.putExtra("key", items[p1].key)

            val options: ActivityOptionsCompat
            if (items[p1].image != null) {
                options = ActivityOptionsCompat.makeSceneTransitionAnimation(p0.cvCard.context as Activity, p0.ivImage, ViewCompat.getTransitionName(p0.ivImage)!!)
                p0.cvCard.context.startActivity(i, options.toBundle())
            } else {
                p0.cvCard.context.startActivity(i)
            }
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.ivImage
        val tvTitle = view.tvTitle
        val tvSubtitle = view.tvSubTitle
        val tvDescription = view.tvDescription
        val cvCard = view.cvCard
    }

}