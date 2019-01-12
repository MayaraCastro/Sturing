package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.content_create_group.*
import java.io.ByteArrayOutputStream


class CreateGroupActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap
    private var postValues = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        btnImageGroup.setOnClickListener { chooseGroupImage() }

        btnSaveGroup.setOnClickListener { saveGroup() }

        startup()
    }

    private fun saveGroup() {
        val user = FirebaseAuth.getInstance().currentUser

        edtGroupName.error = null

        var name = edtGroupName.text.toString()
        var description = edtDescription.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(name)) {
            edtGroupName.error = getString(R.string.error_field_required)
            focusView = edtGroupName
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)

            val groupUid = FirebaseDatabase.getInstance().getReference("groups").child(name).push().key
            val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupUid!!)
            val imageRef = FirebaseStorage.getInstance().getReference("groupImages").child(groupUid).child("groupImage")

            if (::bitmap.isInitialized) {
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val data = baos.toByteArray()
                imageRef.putBytes(data)
                        .addOnFailureListener {
                            Toast.makeText(this@CreateGroupActivity, "Image upload fail",
                                    Toast.LENGTH_SHORT).show()
                        }
                        .addOnSuccessListener {
                            imageRef.downloadUrl
                                    .onSuccessTask { it ->
                                        groupRef.child("image").setValue(it.toString())
                                    }
                        }
            }

            writeNewGroup(name, description, user!!.uid)

        }

    }

    private fun writeNewGroup(name: String, description: String, userId: String) {
        val database = FirebaseDatabase.getInstance().reference
        val key = database.child("groups").push().key
        val post = Group(name, description, userId)
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/groups/$key", postValues)
        //childUpdates.put("/user-posts/$userId/$key", postValues)

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    addGroupUser(key!!)
                    val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    showProgress(false)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this@CreateGroupActivity, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()
                    Log.w("create group", "User didn`t add.")
                }
    }

    private fun startup() {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)
        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var user1 = p0.getValue(User::class.java)
                if (user1 != null) {
                    if (user1.groups != null) {
                        postValues = user1.groups!!

                    }
                }

            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    private fun addGroupUser(groupId: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        var hash = mutableMapOf<String, Boolean>()
        hash.put(groupId, true)

        for ((gp, _) in postValues!!) {
            hash.put(gp, true)
        }

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/groups/", hash)

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener {
                    Toast.makeText(this@CreateGroupActivity, groupId,
                            Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@CreateGroupActivity, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()
                }
    }

    private fun chooseGroupImage() {

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
