package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
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

    private var postValues = mutableMapOf<String, Boolean>()
    private val TAG = "CreateGroupActivity"
    private var mCurrentPhotoPath: String = ""
    private val PERMISSION_CODE = 1000
    private val TAKE_PHOTO_REQUEST = 101
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        btnImageGroup.setOnClickListener { checkPermissions() }

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

            val groupUid = FirebaseDatabase.getInstance().getReference("groups").push().key
            val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupUid!!)
            val imageRef = FirebaseStorage.getInstance().getReference("groupImages").child(groupUid).child("groupImage")
            var group = Group(name, description, user!!.uid, null)

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
                                        group.image = it.toString()
                                        writeNewGroup(group, groupUid)
                                        groupRef.child("image").setValue(it.toString())
                                    }
                        }
            } else
                writeNewGroup(group, groupUid)
        }
    }

    private fun writeNewGroup(group: Group, key: String) {
        val database = FirebaseDatabase.getInstance().reference
        val postValues = group.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/groups/$key", postValues)

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    addGroupUser(key)
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
                .addOnFailureListener {
                    Toast.makeText(this@CreateGroupActivity, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()
                }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                val permission = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                takePhoto()
            }
        } else {
            takePhoto()
        }
    }

    private fun takePhoto() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg")
        val fileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            val bundle = data?.extras
            bitmap = bundle?.get("data") as Bitmap

            imgGroup.scaleType = ImageView.ScaleType.FIT_XY
            imgGroup.background = null
            GlideApp.with(this@CreateGroupActivity)
                    .load(bitmap)
                    .circleCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgGroup)
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
