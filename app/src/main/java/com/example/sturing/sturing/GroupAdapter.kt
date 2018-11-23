package com.example.sturing.sturing

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.group_list_item.view.*

class GroupAdapter(var items : ArrayList<Group>, var context : Context) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.group_list_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.tvGroupName?.text = items[p1].getName()
        p0.tvGroupDescription?.text = items[p1].getDescription()
        //p0.ivGroupImage?.setImageResource(items[p1].getImage()!!.id)
        p0.itemView.setOnClickListener {
            Snackbar.make(it, items[p1].getName(), Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
            val i = Intent(context, MainActivity::class.java)
            context.startActivity(i)
        }
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvGroupName = view.tvGroupName
        val tvGroupDescription = view.tvGroupDescription
        val ivGroupImage = view.ivGroupImage
        val cltGroupItem = view.groupItem

    }

}

