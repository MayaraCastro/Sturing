package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.add_user_item.view.*

class FriendAdapter(var items: ArrayList<User>, var context: Context, var funcao: Int, var groupSelecionado: String? = null) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {
    private var postValues = kotlin.collections.mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.add_user_item, p0, false)

        if(funcao != 3 && funcao != 4){
            view.remove_user.visibility = View.INVISIBLE
            view.remove_user.isClickable = false
        }
        if(funcao == 4){
            view.add_user.visibility = View.INVISIBLE
            view.add_user.isClickable = false
        }
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
        p0.add_user.setOnClickListener {
            p0.add_user.isClickable = false
            p0.add_user.speed = 2F
            p0.add_user.playAnimation()

            if (funcao == 1) {
                addMember(items[p1].userKey)
            } else if (funcao == 2) {
                addFriend(items[p1].userKey) //send friend request
            }else if (funcao == 3){
                acceptRequest(items[p1].userKey)

                p0.remove_user.visibility = View.INVISIBLE
                p0.remove_user.isClickable = false
            }
        }

        p0.remove_user.setOnClickListener {
            p0.remove_user.isClickable = false
            p0.remove_user.speed = 2F
            p0.remove_user.playAnimation()

            val user = FirebaseAuth.getInstance().currentUser
            val userKey = items[p1].userKey

            removeFriend(userKey, user!!.uid) //remove the friends requests or delete as friends
            removeFriend(user!!.uid, userKey)

            p0.add_user.visibility = View.INVISIBLE
            p0.add_user.isClickable = false

        }
    }

    private fun acceptRequest(userKey: String?) {
        val user = FirebaseAuth.getInstance().currentUser

        addFriendToUser(userKey, user!!.uid, 2)
        addFriendToUser(user!!.uid, userKey, 2)

        removeFriend(userKey, user!!.uid)//remove the friends requests
        removeFriend(user!!.uid, userKey)//remove the friends requests
    }

    private fun addMember(userKey: String?) {
        //TODO
        addUserToGroup(userKey, groupSelecionado)
        addGroupToUser(userKey, groupSelecionado)
    }

    private fun addFriend(userKey: String?) {
        val user = FirebaseAuth.getInstance().currentUser

        addFriendToUser(userKey, user!!.uid, 1)
        addFriendToUser(user!!.uid, userKey, 1)
    }

    fun addUserToGroup(userKey: String?, group: String?) {
        postValues = mutableMapOf<String, Boolean>()

        val userRef = FirebaseDatabase.getInstance().getReference("groups").child(group!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var group1 = p0.getValue(Group::class.java)

                if (group1 != null) {
                    if (group1.users != null) {
                        postValues = group1.users!!

                    } else {
                        postValues = mutableMapOf<String, Boolean>()
                    }
                    var hash = mutableMapOf<String, Boolean>()
                    hash.put(userKey!!, true) //true para a pessoa que enviou o pedido
                    if (postValues != null) {
                        for ((gp, value) in postValues!!) {
                            hash.put(gp, value)
                        }
                    }

                    val childUpdates = HashMap<String, Any>()
                    childUpdates.put("/users/", hash)

                    userRef.updateChildren(childUpdates)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {

                            }

                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        userRef.addListenerForSingleValueEvent(user1Listener)

    }

    fun addGroupToUser(userKey: String?, group: String?) {
        postValues = mutableMapOf<String, Boolean>()

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userKey!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user1 = p0.getValue(User::class.java)

                if (user1 != null) {
                    if (user1.groups != null) {
                        postValues = user1.groups!!


                    } else {
                        postValues = mutableMapOf<String, Boolean>()
                    }

                    var hash = mutableMapOf<String, Boolean>()
                    hash.put(group!!, true) //true para a pessoa que enviou o pedido
                    if (postValues != null) {
                        for ((gp, value) in postValues!!) {
                            hash.put(gp, value)
                        }
                    }

                    val childUpdates = HashMap<String, Any>()
                    childUpdates.put("/groups/", hash)

                    userRef.updateChildren(childUpdates)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener {

                            }
                }

                Log.d("POSTVALUES", postValues.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)


    }

    private fun addFriendToUser(userId: String?, currentUser: String?, func: Int) {
        postValues = mutableMapOf<String, Boolean>()

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser!!)
        userRef.keepSynced(true)


        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user1 = p0.getValue(User::class.java)
                Log.d("POSTVALUES", postValues.toString())
                if (user1 != null) {

                    if(func == 2){
                        if (user1.friends != null) {

                            postValues = user1.friends!!
                        }else {
                            postValues = mutableMapOf()
                        }
                    }else if(func == 1){
                        if (user1.friend_requests != null) {

                            postValues = user1.friend_requests!!
                        }else {
                            postValues = mutableMapOf()
                        }
                    } else {
                        postValues = mutableMapOf()
                    }

                    var hash = mutableMapOf<String, Boolean>()

                    if(func == 1){
                        if (currentUser!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                            hash.put(userId!!, true) //true para a pessoa que enviou o pedido
                        } else {
                            hash.put(userId!!, false)
                        }
                    }else{
                        hash.put(userId!!, true)
                    }



                    for ((gp, bol) in postValues) {
                        // Log.d("ENTRA", "CHAMOU O POSTVALUES")
                        hash.put(gp, bol)
                    }
                    Log.d("HASH", hash.toString())


                    val childUpdates = HashMap<String, Any>()
                    if(func == 1){
                        childUpdates.put("/friend_requests/", hash)
                    }else if(func == 2){
                        childUpdates.put("/friends/", hash)
                    }

                    userRef.updateChildren(childUpdates)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                            }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }
        userRef.addListenerForSingleValueEvent(user1Listener)
    }
    private fun removeFriend(userId: String?, currentUser: String?) {
        postValues = mutableMapOf<String, Boolean>()

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser!!)
        userRef.keepSynced(true)
        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user1 = p0.getValue(User::class.java)
                Log.d("POSTVALUES", postValues.toString())
                if (user1 != null) {
                    if(funcao == 3){
                        if (user1.friend_requests != null) {
                            postValues = user1.friend_requests!!

                        } else {
                            postValues = mutableMapOf()
                        }
                    }else if(funcao == 4){
                        if (user1.friends != null) {
                            postValues = user1.friends!!

                        } else {
                            postValues = mutableMapOf()
                        }
                    }


                    var hash = mutableMapOf<String, Boolean>()

                    for ((gp, bol) in postValues) {
                        // Log.d("ENTRA", "CHAMOU O POSTVALUES")
                        if(!gp.equals(userId!!)){
                            hash.put(gp, bol)
                        }
                    }
                    Log.d("HASH", hash.toString())


                    val childUpdates = HashMap<String, Any>()
                    if(funcao == 3){
                        childUpdates.put("/friend_requests/", hash)
                    }
                    else if( funcao == 4){
                        childUpdates.put("/friends/", hash)
                    }

                    userRef.updateChildren(childUpdates)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener {
                            }
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }
        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    fun getFromBase(p1: Int, holder: ViewHolder) {

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

            override fun onCancelled(error: DatabaseError) {}


        }

        userRef.addListenerForSingleValueEvent(user1Listener)


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to
        val tvimgFriend = view.tvimgFriend
        val tvNameFriend = view.tvNameFriend
        val add_user = view.add_user
        val remove_user = view.remove_user


    }

}

