package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail_card.*

class DetailCardActivity : AppCompatActivity() {

    private val TAG = "DetailCardActivity"
    private var state = 0
    private var flashCardKey: String? = null

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
        state = extra.getInt("state")
        flashCardKey = extra.getString("key")
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

        imbNext.setOnClickListener {
            next()
        }

        imbPrevious.setOnClickListener {
            previous()
        }

        updateUI(state)
    }

    private fun updateUI(state: Int) {
        when (state) {
            0 -> { //To Do
                imbPrevious.animate().alpha(0F).setDuration(200).start()
                txtPrevious.animate().alpha(0F).setDuration(200).start()

                imbPrevious.isClickable = false

                txtNext.text = getString(R.string.set_doing)
                txtState.text = getString(R.string.to_do)
            }
            1 -> { //Doing
                imbPrevious.animate().alpha(1.0F).setDuration(200).start()
                txtPrevious.animate().alpha(1.0F).setDuration(200).start()
                imbNext.animate().alpha(1.0F).setDuration(200).start()
                txtNext.animate().alpha(1.0F).setDuration(200).start()

                imbPrevious.isClickable = true
                imbNext.isClickable = true

                txtPrevious.text = getString(R.string.set_to_do)
                txtNext.text = getString(R.string.set_done)
                txtState.text = getString(R.string.doing)
            }
            2 -> { //Done
                imbNext.animate().alpha(0F).setDuration(200).start()
                txtNext.animate().alpha(0F).setDuration(200).start()

                imbNext.isClickable = false

                txtPrevious.text = getString(R.string.set_doing)
                txtState.text = getString(R.string.done)
            }
        }
    }

    private fun next() {
        state++
        updateUI(state)
    }

    private fun previous() {
        state--
        updateUI(state)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        uploadChanges()
    }

    private fun uploadChanges() {
        val cardRef = FirebaseDatabase.getInstance().getReference("flashcards").child(flashCardKey!!).child("state")
        cardRef.setValue(state).addOnCompleteListener {
            if (it.isComplete)
                supportFinishAfterTransition()
        }
    }
}
