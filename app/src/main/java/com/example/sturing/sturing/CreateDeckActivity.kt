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
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_deck.*
import kotlinx.android.synthetic.main.content_create_deck.*
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

class CreateDeckActivity : AppCompatActivity() {

    private val TAG = "CreateFlashCardActivity"
    private var mCurrentPhotoPath: String = ""
    private val PERMISSION_CODE = 1000
    private val TAKE_PHOTO_REQUEST = 101
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_deck)
        setSupportActionBar(toolbar)

        btnImage.setOnClickListener { checkPermissions() }

        btnSave.setOnClickListener { checkError() }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun checkError() {
        val title = txtTitle.text.toString()
        val description = txtDescription.text.toString()

        txtTitle.error = null
        txtDescription.error = null

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(description)) {
            txtDescription.error = getString(R.string.error_field_required)
            focusView = txtDescription
            cancel = true
        }

        if (TextUtils.isEmpty(title)) {
            txtTitle.error = getString(R.string.error_field_required)
            focusView = txtTitle
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            saveData()
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

            imgDeck.scaleType = ImageView.ScaleType.FIT_XY
            imgDeck.background = null
            GlideApp.with(this@CreateDeckActivity)
                    .load(bitmap)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imgDeck)
        }
    }

    private fun saveData() {

        val key = FirebaseDatabase.getInstance().reference.child("decks").push().key
        val cardRef = FirebaseDatabase.getInstance().getReference("decks").child(key!!)
        val imageRef = FirebaseStorage.getInstance().getReference("deckImage").child(key!!).child("deckImage")

        val title = txtTitle.text.toString()
        val description = txtDescription.text.toString()
        val userAuthor = FirebaseAuth.getInstance().currentUser!!.uid
        val timestamp = LocalDateTime.now().toString()
        val deck = Deck(userAuthor, null, title, description, key, timestamp)

        if (::bitmap.isInitialized) {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            imageRef.putBytes(data)
                    .addOnFailureListener {
                        Toast.makeText(this@CreateDeckActivity, "Image upload fail",
                                Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener {
                        imageRef.downloadUrl
                                .onSuccessTask { it ->
                                    deck.image = it.toString()
                                    showProgress(false)
                                    val result = Intent()
                                    result.putExtra("key", key)
                                    setResult(Activity.RESULT_OK, result)
                                    finish()
                                    cardRef.setValue(deck)
                                }
                    }
        } else {
            cardRef.setValue(deck)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            showProgress(false)
                            val result = Intent()
                            result.putExtra("key", key)
                            setResult(Activity.RESULT_OK, result)
                            finish()
                        } else {
                            Toast.makeText(this@CreateDeckActivity, "Deck creation fail",
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
        }

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private fun showProgress(show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            btnSave.isClickable = !show
            btnSave.animate().alpha(0F).setDuration(200).start()
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
            btnSave.isClickable = !show
            btnSave.animate().alpha(0F).setDuration(200).start()
        }
    }
}
