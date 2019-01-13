package com.example.sturing.sturing

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import kotlinx.android.synthetic.main.group_list_item.view.*

class GroupAdapter(var items: ArrayList<Group>, var context: Context) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.group_list_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tvGroupName?.text = items[p1].name
        p0.tvGroupDescription?.text = items[p1].description
        if (items[p1].image != null) {
            GlideApp.with(context)
                    .load(items[p1].image)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(p0.ivGroupImage)
        }
        p0.itemView.setOnClickListener {
            val i = Intent(context, MainActivity::class.java)
            i.putExtra("group", items[p1].groupKey)
            i.putExtra("name", items[p1].name)
            i.putExtra("description", items[p1].description)
            i.putExtra("item", 1)
            i.putExtra("image", items[p1].image)
            context.startActivity(i)
        }
        setAnimation(p0.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.fade_in)
        viewToAnimate.animation = animation
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGroupName = view.tvGroupName
        val tvGroupDescription = view.tvGroupDescription
        val ivGroupImage = view.ivGroupImage
    }

}

