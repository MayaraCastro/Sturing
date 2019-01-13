package com.example.sturing.sturing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_create_card.*
import kotlinx.android.synthetic.main.content_create_card.*
import java.time.LocalDateTime

class CreateCardActivity : AppCompatActivity() {

    private val TAG = "CreateFlashCardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_card)
        setSupportActionBar(toolbar)

        btnSave.setOnClickListener { checkError() }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun checkError() {
        val question = txtQuestion.text.toString()
        val answer = txtAnswer.text.toString()

        txtQuestion.error = null
        txtAnswer.error = null

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(answer)) {
            txtAnswer.error = getString(R.string.error_field_required)
            focusView = txtAnswer
            cancel = true
        }

        if (TextUtils.isEmpty(question)) {
            txtQuestion.error = getString(R.string.error_field_required)
            focusView = txtQuestion
            cancel = true
        }

        if (!isQuestionValid(question)) {
            txtQuestion.error = getString(R.string.error_must_end_with_interrogation)
            focusView = txtQuestion
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            showProgress(true)
            saveData()
        }
    }

    private fun isQuestionValid(question: String): Boolean {
        return question.last() == '?'
    }

    private fun saveData() {
        val key = FirebaseDatabase.getInstance().reference.child("cards").push().key
        val cardRef = FirebaseDatabase.getInstance().getReference("cards").child(key!!)

        val question = txtQuestion.text.toString()
        val answer = txtAnswer.text.toString()
        val userAuthor = FirebaseAuth.getInstance().currentUser!!.uid
        val timestamp = LocalDateTime.now().toString()
        val card = Card(userAuthor, question, answer, key, timestamp)


        cardRef.setValue(card)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showProgress(false)
                        val result = Intent()
                        result.putExtra("key", key)
                        setResult(Activity.RESULT_OK, result)
                        finish()
                    } else {
                        Toast.makeText(this@CreateCardActivity, "Card creation fail",
                                Toast.LENGTH_SHORT).show()
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
