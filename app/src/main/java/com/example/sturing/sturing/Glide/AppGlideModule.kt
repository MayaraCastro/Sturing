package com.example.sturing.sturing.Glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import kotlin.reflect.KProperty1

@GlideModule
class AppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(StorageReference::class.java, InputStream::javaClass, FirebaseImageLoader.Factory())
    }


}

private fun Registry.append(java: Class<StorageReference>, kProperty1: KProperty1<InputStream, Class<InputStream>>, factory: FirebaseImageLoader.Factory) {

}
