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
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.content_manage.*
import java.io.ByteArrayOutputStream

class ManageActivity : AppCompatActivity() {

    private val TAG = "ManageActivity"
    private var mCurrentPhotoPath: String = ""
    private val PERMISSION_CODE = 1000
    private val TAKE_PHOTO_REQUEST = 101
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        setSupportActionBar(toolbar)

        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                txtName.hint = p0.child("name").getValue(String::class.java)
                txtEmail.hint = p0.child("email").getValue(String::class.java)
                val imageUrl = p0.child("image").getValue(String::class.java)

                if (imageUrl != null) {
                    GlideApp.with(this@ManageActivity)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(withCrossFade())
                            .circleCrop()
                            .into(imgFlashCard)
                }
                Log.i(TAG, "Data changed")
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(userListener)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        btnSave.setOnClickListener { saveData() }

        btnImage.setOnClickListener { checkPermissions() }

        btnLogout.setOnClickListener { signOut() }
    }

    private fun signOut() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        FirebaseAuth.getInstance().signOut()
        finish()
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun takePhoto() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpg")
        val fileUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
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

            GlideApp.with(this@ManageActivity)
                    .load(bitmap)
                    .transition(withCrossFade())
                    .circleCrop()
                    .into(imgFlashCard)
        }
    }

    private fun saveData() {

        val name = txtName.text.toString()

        showProgress(true)
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)
        val imageRef = FirebaseStorage.getInstance().getReference("images").child(user!!.uid).child("profile")

        if (::bitmap.isInitialized) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data)
                    .addOnFailureListener {
                        Toast.makeText(this@ManageActivity, "Image upload fail",
                                Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        imageRef.downloadUrl
                                .onSuccessTask { it ->
                                    userRef.child("image").setValue(it.toString())
                                }
                    }
        }

        if (!name.isNullOrEmpty()) {
            userRef.child("name").setValue(name)
                    .addOnSuccessListener {
                        showProgress(false)
                        finish()
                    }
                    .addOnFailureListener {
                        showProgress(false)
                        Toast.makeText(this@ManageActivity, getString(R.string.registration_failed),
                                Toast.LENGTH_SHORT).show()
                    }
        } else {
            showProgress(false)
            finish()
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
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
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

}
