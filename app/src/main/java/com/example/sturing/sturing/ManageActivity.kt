package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.content_manage.*

class ManageActivity : AppCompatActivity() {

    private val TAG = "ManageActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {onBackPressed()}

        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                txtName.hint = p0.child("name").getValue(String::class.java)
                txtEmail.hint = p0.child("email").getValue(String::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(userListener)

        btnSave.setOnClickListener { saveData() }
    }

    private fun saveData() {
        showProgress(true)
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        userRef.child("name").setValue(txtName.text.toString())
                .addOnSuccessListener {
                    showProgress(false)
                    finish()
                }
                .addOnFailureListener {
                    showProgress(false)
                    Toast.makeText(this@ManageActivity, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()
                }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            progressBar.animate()
                    .setDuration(shortAnimTime)
                    .alpha((if (show) 1 else 0).toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            progressBar.visibility = if (show) View.VISIBLE else View.GONE
                        }
                    })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
        }
    }
}
