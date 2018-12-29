package com.example.sturing.sturing

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_create_group.*
import kotlinx.android.synthetic.main.content_create_group.*
import java.io.ByteArrayOutputStream

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)
        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener { onBackPressed() }

        btnImageGroup.setOnClickListener { chooseGroupImage() }

        btnSaveGroup.setOnClickListener { saveGroup() }
    }

    private fun chooseGroupImage() {

    }

    private fun saveGroup() {

        val groupName = edtGroupName.text.toString()

        edtGroupName.error = null

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(groupName)) {
            edtGroupName.error = getString(R.string.error_field_required)
            focusView = edtGroupName
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            val groupUid = FirebaseDatabase.getInstance().getReference("groups").child(groupName).push().key
            val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupUid!!)
            val imageRef = FirebaseStorage.getInstance().getReference("groupImages").child(groupUid!!).child("groupImage")

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

            groupRef.child("name").setValue(groupName)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@CreateGroupActivity, getString(R.string.registration_failed),
                                Toast.LENGTH_SHORT).show()
                    }

        }

    }

}
