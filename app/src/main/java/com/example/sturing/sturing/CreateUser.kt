package com.example.sturing.sturing

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.content_create_user.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class CreateUser : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        setSupportActionBar(toolbar)

        btSignup.setOnClickListener {
            email = txtEmail.text.toString()
            password = txtPass.text.toString()
            mAuth.createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val i = Intent(this,LoginActivity::class.java)
                            startActivity(i)
                        } else {
                            Toast.makeText(this@CreateUser, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }

        btSignin.setOnClickListener {onBackPressed()}
    }
}
