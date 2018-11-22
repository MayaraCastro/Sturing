package com.example.sturing.sturing

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var user :String?=null
    private var senha :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(savedInstanceState !=null){
            user = savedInstanceState!!.getString("user")
            senha =savedInstanceState!!.getString("senha")

            txtuser.text = Editable.Factory.getInstance().newEditable(user)
            txtpass.text = Editable.Factory.getInstance().newEditable(senha)
        }



        btLogin.setOnClickListener {
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
        }

        btSignup.setOnClickListener {
            val i = Intent(this,CreateUser::class.java)
            startActivity(i)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("user", txtuser.text.toString()!!)
        outState.putString("senha", txtpass.text.toString()!!)
        Log.i("TelaPrincipal", "onSaveInstanceState")
    }
}
