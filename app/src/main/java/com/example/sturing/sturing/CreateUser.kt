package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.content_create_user.*
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class CreateUser : AppCompatActivity() {

    private val TAG = "CreateUser"

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var storage: FirebaseStorage
    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        setSupportActionBar(toolbar)

        btSignup.setOnClickListener {
            signup()
        }

        btSignin.setOnClickListener {onBackPressed()}
    }

    private fun signup() {
        val name = txtName.text.toString()
        val username = txtUsername.text.toString()

        txtName.error = null
        txtPass.error = null
        txtUsername.error = null
        txtEmail.error = null

        email = txtEmail.text.toString()
        password = txtPass.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            txtPass.error = getString(R.string.error_invalid_password)
            focusView = txtPass
            cancel = true
        }

        if (TextUtils.isEmpty(username)) {
            txtUsername.error = getString(R.string.error_field_required)
            focusView = txtUsername
            cancel = true
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.error = getString(R.string.error_field_required)
            focusView = txtEmail
            cancel = true
        } else if (!isEmailValid(email)) {
            txtEmail.error = getString(R.string.error_invalid_email)
            focusView = txtEmail
            cancel = true
        }

        if (TextUtils.isEmpty(name)) {
            txtName.error = getString(R.string.error_field_required)
            focusView = txtName
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            writeNewUser(mAuth.currentUser!!.uid, name)
                            //saveProfileName(name)
                        } else {
                            showProgress(false)
                            Toast.makeText(this@CreateUser, getString(R.string.registration_failed),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }
    }

    private fun writeNewUser(userId: String, name: String) {
        val user = User(name, email)
        val database = FirebaseDatabase.getInstance().reference

        database.child("users").child(userId).setValue(user)
                .addOnSuccessListener {
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    showProgress(false)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@CreateUser, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()
                    Log.w(TAG, "User didn`t add.")
                }
    }

    /*private fun saveProfileName(name: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
        user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showProgress(false)
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    else
                        Log.w(TAG, "User didn`t add.")
                }
    }*/

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            register_progress.visibility = if (show) View.VISIBLE else View.GONE
            register_progress.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            register_progress.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            register_progress.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 4
    }
}
