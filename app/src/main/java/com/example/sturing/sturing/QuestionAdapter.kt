package com.example.sturing.sturing

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.group_list_item.view.*

class QuestionAdapter(var items: ArrayList<Question>, var context: Context, var group: String) : RecyclerView.Adapter<QuestionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.group_list_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {


        // p0.tvGroupName?.text = items[p1].userAuthor
        p0.tvGroupDescription?.text = items[p1].question

        getFromBase(p1, p0)
        //p0.ivGroupImage?.setImageResource(items[p1].getImage()!!.id) // TODO por a imagem do usuario
        p0.itemView.setOnClickListener {
            val i = Intent(context, QuestionActivity::class.java)
            i.putExtra("question", items[p1].questionKey)
            i.putExtra("group", group)
            // i.addFlags(FLAG_ACTIVITY_CLEAR_TASK)
            i.addFlags(FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
        setAnimation(p0.itemView)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.fade_in)
        viewToAnimate.animation = animation
    }

    fun getFromBase(p1: Int, holder: ViewHolder) {

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(items[p1].userAuthor!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user = p0.getValue(User::class.java)

                if (user!!.image != null) {
                    GlideApp.with(context)
                            .load(user!!.image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .circleCrop()
                            .into(holder.ivGroupImage)
                }
                holder.tvGroupName.text = user!!.name
            }

            override fun onCancelled(error: DatabaseError) {}

        }

        userRef.addListenerForSingleValueEvent(user1Listener)

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvGroupName = view.tvGroupName
        val tvGroupDescription = view.tvGroupDescription
        val ivGroupImage = view.ivGroupImage
    }

}

