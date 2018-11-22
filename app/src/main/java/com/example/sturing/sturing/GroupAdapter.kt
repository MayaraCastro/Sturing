package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.group_list_item.view.*

class GroupAdapter(var items : ArrayList<Group>, var context : Context) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.group_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tvGroupName?.text = items[p1].getName()
        p0.tvGroupDescription?.text = items[p1].getDescription()
        p0.tvGroupTimestamp?.text = items[p1].getTimestamp()
        //p0.ivGroupImage?.setImageResource(items[p1].getImage()!!.id)
    }

}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvGroupName = view.tvGroupName
    val tvGroupDescription = view.tvGroupDescription
    val tvGroupTimestamp = view.tvGroupTimestamp
    val ivGroupImage = view.ivGroupImage
}