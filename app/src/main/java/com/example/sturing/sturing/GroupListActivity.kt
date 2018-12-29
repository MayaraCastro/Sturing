package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_group_list.*
import kotlinx.android.synthetic.main.content_group_list.*

class GroupListActivity : AppCompatActivity() {
    var groups : ArrayList<Group> = ArrayList()
    private val TAG = "GroupListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        var image = ImageView(this)
        image.setImageResource(R.drawable.book)

        var g1 = Group()
        g1.name = "John"
        g1.description = "John`s group"

        var g2 = Group()
        g2.name = "Bad"
        g2.description = "Bad`s group"

        var g3 = Group()
        g3.name = "Ira"
        g3.description = "Ira`s group"

        groups.add(g1)
        groups.add(g2)
        groups.add(g3)

        getUserGroups()

        rv_group_list.layoutManager = LinearLayoutManager(this)
        rv_group_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_group_list.adapter = GroupAdapter(groups, this)
    }

    private fun getUserGroups(){

        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var user1 = p0.getValue(User::class.java)
                Log.d(TAG, "userName: " + user1!!.name)
                Log.d(TAG, "userEmail: " + user1!!.email)
                addGroupsOnList(user1!!.groups)

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    fun addGroupsOnList(groupos : HashMap<String, Boolean>?){

        if (groupos == null) {

            return
        }
        for((groupID, _) in groupos){
            val userRef = FirebaseDatabase.getInstance().getReference("groups").child(groupID)

            val userListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var g1 = p0.getValue(Group::class.java)!!

                    Log.d(TAG, "groupName: " + g1.name)
                    Log.d(TAG, "groupDescription: " + g1.description)

                    groups.add(g1!!)
                    rv_group_list.adapter!!.notifyDataSetChanged()

                }
                override fun onCancelled(p0: DatabaseError) {
                }
            }

            userRef.addListenerForSingleValueEvent(userListener)
        }
    }

}
