package com.example.sturing.sturing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import kotlinx.android.synthetic.main.deck_item.view.*

class DeckAdapter(var items: ArrayList<Deck>, var context: Context, var group: String) : RecyclerView.Adapter<DeckAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.deck_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tvDeckTitle?.text = items[p1].title
        p0.tvDeckDescription?.text = items[p1].description

        if (items[p1].image != null) {
            ViewCompat.setTransitionName(p0.ivDeck, items[p1].title!!.hashCode().toString())
            GlideApp.with(context)
                    .load(items[p1].image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(p0.ivDeck)
        } else {
            p0.ivDeck.setBackgroundColor(Color.WHITE)
            GlideApp.with(context)
                    .load(R.drawable.deck_of_cards)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(p0.ivDeck)
        }

        p0.deckItem.setOnClickListener {
            var i = Intent(p0.deckItem.context, TODO())
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.putExtra("image", items[p1].image)
            i.putExtra("code", items[p1].title!!.hashCode().toString())
            i.putExtra("title", items[p1].title)
            i.putExtra("description", items[p1].description)
            i.putExtra("author", items[p1].userAuthor)
            i.putExtra("key", items[p1].key)

            val options: ActivityOptionsCompat
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(p0.deckItem.context as Activity, p0.ivDeck, ViewCompat.getTransitionName(p0.ivDeck)!!)
            p0.deckItem.context.startActivity(i, options.toBundle())
        }
        setAnimation(p0.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.fade_in)
        viewToAnimate.animation = animation
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivDeck = view.ivDeck
        val tvDeckTitle = view.tvDeckTitle
        val tvDeckDescription = view.tvDeckDescription
        val deckItem = view.deckItem
    }

}