package com.example.sturing.sturing

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_group.*

class ManageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {onBackPressed()}
    }
}