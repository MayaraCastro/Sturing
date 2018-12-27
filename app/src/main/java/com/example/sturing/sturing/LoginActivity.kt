package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if(savedInstanceState != null){
            email = savedInstanceState!!.getString("email")
            password = savedInstanceState!!.getString("password")

            txtuser.text = Editable.Factory.getInstance().newEditable(email)
            txtpass.text = Editable.Factory.getInstance().newEditable(password)
        }

        btLogin.setOnClickListener {
            attemptLogin()
        }

        txtRegister.setOnClickListener {
            val i = Intent(this,CreateUser::class.java)
            startActivity(i)
        }
    }

    private fun attemptLogin() {
        txtuser.error = null
        txtpass.error = null

        email = txtuser.text.toString()
        password = txtpass.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            txtpass.error = getString(R.string.error_invalid_password)
            focusView = txtpass
            cancel = true
        }

        if (TextUtils.isEmpty(email)) {
            txtuser.error = getString(R.string.error_field_required)
            focusView = txtuser
            cancel = true
        } else if (!isEmailValid(email)) {
            txtuser.error = getString(R.string.error_invalid_email)
            focusView = txtuser
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        showProgress(false)
                        if (task.isSuccessful) {
                            val i = Intent(this, MainActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.authentication_failed),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            login_progress.visibility = if (show) View.VISIBLE else View.GONE
            login_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            login_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            login_progress.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("email", txtuser.text.toString()!!)
        outState.putString("password", txtpass.text.toString()!!)
        Log.i("TelaPrincipal", "onSaveInstanceState")
    }
}
