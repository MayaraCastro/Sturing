package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.content_manage.*
import java.io.ByteArrayOutputStream
import java.io.File

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

        btnImage.setOnClickListener {
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
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                    Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                    null, null, null)
            cursor.moveToFirst()
            val photoPath = cursor.getString(0)
            cursor.close()
            val file = File(photoPath)
            val uri = Uri.fromFile(file)

            val targetW = imgProfile.width
            val targetH = imgProfile.height

            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeFile(photoPath, bmOptions)

            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            val scaleFactor = Math.min(photoW/targetW, photoH/targetH)

            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true

            bitmap = BitmapFactory.decodeFile(photoPath, bmOptions)

            imgProfile.setImageBitmap(bitmap)
        }
    }

    private fun saveData() {

        val name = txtName.text.toString()

        showProgress(true)
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        if (imgProfile != null) {
            //TODO: Code to upload image to Storage database (not the real-time one)
        }

        Log.i(TAG, "Entering nameisnotnull")
        if (!name.isNullOrEmpty()) {
            Log.i(TAG, "Entered nameisnotnull")
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
