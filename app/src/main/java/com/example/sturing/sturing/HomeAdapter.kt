package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import kotlinx.android.synthetic.main.home_item.view.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class HomeAdapter(var items: ArrayList<Home>, var context: Context) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.home_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.txtName.text = items[p1].userName
        p0.txtTitle.text = items[p1].title
        p0.txtBrief.text = items[p1].brief
        p0.txtContent.text = items[p1].content

        if (items[p1].userImage != null) {
            GlideApp.with(context)
                    .load(items[p1].userImage)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(p0.imgProfile)
        } else {
            GlideApp.with(context)
                    .load(R.drawable.ic_person_black_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(p0.imgProfile)
        }

        when (items[p1].type) {
            0 -> { // Update
                GlideApp.with(context)
                        .load(R.mipmap.ic_update)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(p0.imgType)
            }
            1 -> { // Release notes
                GlideApp.with(context)
                        .load(R.mipmap.ic_resource)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(p0.imgType)
            }
            2 -> { // News
                GlideApp.with(context)
                        .load(R.mipmap.ic_news)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(p0.imgType)
            }
        }

        val sourceTime = LocalDateTime.parse(items[p1].timestamp)
        val actualTime = LocalDateTime.now()
        val diffDays = ChronoUnit.DAYS.between(sourceTime, actualTime)
        var resultTime = ""

        if (diffDays >= 1 && diffDays <= 30) {
            resultTime = diffDays.toString() + "d"
        } else {
            val diffHours = ChronoUnit.HOURS.between(sourceTime, actualTime)
            val diffMin = ChronoUnit.MINUTES.between(sourceTime, actualTime)
            if (diffHours > 1) {
                resultTime = diffHours.toString() + "h " + diffMin.toString() + "m"
            } else {
                resultTime = diffMin.toString() + "m"
            }
        }

        p0.txtTime.text = resultTime

        setAnimation(p0.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.fade_in)
        viewToAnimate.animation = animation
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile = view.imgProfile
        val txtName = view.txtName
        val txtTitle = view.txtTitle
        val imgType = view.imgType
        val txtBrief = view.txtBrief
        val txtContent = view.txtContent
        val txtTime = view.txtTime
    }

}