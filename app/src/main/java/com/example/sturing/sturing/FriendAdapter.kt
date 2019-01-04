package com.example.sturing.sturing

import android.content.Context
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_user_item.view.*
import kotlinx.android.synthetic.main.group_list_item.view.*

class FriendAdapter (var items : ArrayList<User>, var context : Context, var funcao:Int) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {
    private var postValues = mutableMapOf<String, Boolean>()
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

            if(funcao == 1){

                addMember(items[p1].userKey)
            }
            else if(funcao == 2){
                addFriend(items[p1].userKey)

            }
        }
    }

    private fun addMember(userKey: String?) {
        //TODO
    }

    private fun addFriend(userKey: String?) {
        val user = FirebaseAuth.getInstance().currentUser

        addFriendToUser(userKey, user!!.uid)

        addFriendToUser(user!!.uid, userKey)


    }

    private fun startup(userKey: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userKey!!)
        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var user1 = p0.getValue(User::class.java)
                if (user1 != null) {
                    if (user1.friends != null) {
                        postValues = user1.friends!!
                    }
                }

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    private fun addFriendToUser(userId : String?, currentUser: String?){
        startup(currentUser)
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser!!)

        var hash = mutableMapOf<String, Boolean>()
        if(currentUser!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            hash.put(userId!!, true) //true para a pessoa que enviou o pedido
        }
        else{
            hash.put(userId!!, false)
        }


        for((gp, value) in postValues!!){
            hash.put(gp, value)
        }

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/friend_requests/", hash)

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener {

                }
                .addOnFailureListener {

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

