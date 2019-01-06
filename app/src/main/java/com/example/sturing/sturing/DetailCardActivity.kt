package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail_card.*

class DetailCardActivity : AppCompatActivity() {

    private val TAG = "DetailCardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)
        Log.d(TAG, "Entered")

        val extra = intent.extras
        val author = extra.getString("author")
        val image = extra.getString("image")
        val code = extra.getString("code")
        val title = extra.getString("title")
        val subtitle = extra.getString("subTitle")
        val description = extra.getString("description")
        val state = extra.getInt("state")
        ivImage.transitionName = code

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(author)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user =  p0.getValue(User::class.java)
                txtAuthor.text = user!!.name
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(userListener)

        if (!image.isNullOrEmpty()) {
            GlideApp.with(this)
                    .load(image)
                    .dontAnimate()
                    .override(ivImage.maxWidth, ivImage.maxHeight)
                    .into(ivImage)
        }
        txtTitle.text = title
        txtSubtitle.text = subtitle
        txtDescription.text = description

        fabBack.setOnClickListener {
            onBackPressed()
        }

        updateUI(state)
    }

    private fun updateUI(state: Int) {
        when (state) {
            0 -> { //To Do
                imbPrevious.visibility = View.GONE
                txtPrevious.visibility = View.GONE
                txtNext.text = getString(R.string.set_doing)
            }
            1 -> { //Doing
                txtPrevious.text = getString(R.string.set_to_do)
                txtNext.text = getString(R.string.set_done)
            }
            2 -> { //Done
                imbNext.visibility = View.GONE
                txtNext.visibility = View.GONE
                txtPrevious.text = getString(R.string.set_doing)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }
}
