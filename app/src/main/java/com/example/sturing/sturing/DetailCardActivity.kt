package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sturing.sturing.Glide.GlideApp
import kotlinx.android.synthetic.main.activity_detail_card.*

class DetailCardActivity : AppCompatActivity() {

    private val TAG = "DetailCardActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_card)
        Log.d(TAG, "Entered")

        val extra = intent.extras
        val image = extra.getString("image")
        val code = extra.getString("code")
        ivImage.transitionName = code

        if (!image.isNullOrEmpty()) {
            GlideApp.with(this)
                    .load(image)
                    .dontAnimate()
                    .override(ivImage.maxWidth, ivImage.maxHeight)
                    .into(ivImage)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFinishAfterTransition()
    }
}
