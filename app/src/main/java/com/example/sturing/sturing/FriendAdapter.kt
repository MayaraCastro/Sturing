package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_user_item.view.*
import kotlinx.android.synthetic.main.group_list_item.view.*

class FriendAdapter (var items : ArrayList<User>, var context : Context) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.add_user_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        getFromBase(p1, p0)

        p0.itemView.setOnClickListener {
            /*val i = Intent(context, QuestionActivity::class.java)
            i.putExtra("question", items[p1].commentKey)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)*/
        }
        p0.tvbtAddMember.setOnClickListener {
            items[p1].userKey //TODO add user no grupo
        }
    }

    fun getFromBase(p1: Int, holder: ViewHolder){

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(items[p1].userKey!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user = p0.getValue(User::class.java)

                if (user!!.image != null) {
                    GlideApp.with(context)
                            .load(user!!.image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .circleCrop()
                            .into(holder.tvimgFriend)
                }
                holder.tvNameFriend.text = user!!.name


            }
            override fun onCancelled(error: DatabaseError) { }


        }

        userRef.addListenerForSingleValueEvent(user1Listener)


    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvimgFriend = view.tvimgFriend
        val tvNameFriend = view.tvNameFriend
        val tvbtAddMember = view.tvbtAddMember
    }

}

