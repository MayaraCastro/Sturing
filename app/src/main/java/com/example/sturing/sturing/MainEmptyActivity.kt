package com.example.sturing.sturing

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainEmptyActivity : AppCompatActivity() {

    companion object {
        var mDatabase = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!mDatabase) {
            Log.d("test", "entered")
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            mDatabase = true
        }
        val i: Intent

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null)
            i = Intent(this, MainActivity::class.java)
        else
            i = Intent(this, LoginActivity::class.java)

        startActivity(i)
        finish()
    }
}
