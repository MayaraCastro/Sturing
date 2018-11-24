package com.example.sturing.sturing

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_group_list.*
import kotlinx.android.synthetic.main.content_group_list.*

class GroupListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        var groups : ArrayList<Group> = ArrayList()
        var g1 = Group()
        g1.setName("John")
        g1.setDescription("John`s group")
        g1.setTimestamp("7:20")
        var image = ImageView(this)
        image.setImageResource(R.drawable.book)
        g1.setImage(image)
        groups.add(g1)
        var g2 = Group()
        g2.setName("Bad")
        g2.setDescription("Bad`s group")
        g2.setTimestamp("8:20")
        g2.setImage(image)
        groups.add(g2)
        var g3 = Group()
        g3.setName("Ira")
        g3.setDescription("Ira`s group")
        g3.setTimestamp("9:20")
        g3.setImage(image)
        groups.add(g3)

        rv_group_list.layoutManager = LinearLayoutManager(this)
        rv_group_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        rv_group_list.adapter = GroupAdapter(groups, this)
    }
}
