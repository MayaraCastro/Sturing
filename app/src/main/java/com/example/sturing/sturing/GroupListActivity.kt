package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_group_list.*
import kotlinx.android.synthetic.main.content_group_list.*

class GroupListActivity : AppCompatActivity() {
    var groups : ArrayList<Group> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        var image = ImageView(this)
        image.setImageResource(R.drawable.book)

        var g1 = Group()
        g1.setName("John")
        g1.setDescription("John`s group")
        g1.setTimestamp("7:20")
        g1.setImage(image)

        var g2 = Group()
        g2.setName("Bad")
        g2.setDescription("Bad`s group")
        g2.setTimestamp("8:20")
        g2.setImage(image)

        var g3 = Group()
        g3.setName("Ira")
        g3.setDescription("Ira`s group")
        g3.setTimestamp("9:20")
        g3.setImage(image)

        groups.add(g1)
        groups.add(g2)
        groups.add(g3)

        val user = FirebaseAuth.getInstance().currentUser
        getUserGroups(user!!.uid)

        rv_group_list.layoutManager = LinearLayoutManager(this)
        rv_group_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_group_list.adapter = GroupAdapter(groups, this)
    }

    private fun getUserGroups(userId :String){


        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)


        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var user1 = p0.getValue(User::class.java)
                addGroupsOnList(user1!!.getGroups())


            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)

    }

    fun addGroupsOnList(groupos : ArrayList<String>?){


        if (groupos == null) {

            return
        }
        for(groupID in groupos){
            val userRef = FirebaseDatabase.getInstance().getReference("groups").child(groupID)

            val userListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var g1 = p0.getValue(Group::class.java)!!

                    groups.add(g1!!)

                }
                override fun onCancelled(p0: DatabaseError) {
                }
            }

            userRef.addListenerForSingleValueEvent(userListener)
        }
    }

    fun addOnList(group : Group){
        groups.add(group)
    }
}
