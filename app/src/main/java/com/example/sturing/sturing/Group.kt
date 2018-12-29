package com.example.sturing.sturing

import android.widget.ImageView
import android.R.attr.author



data class Group(private var name: String? = null, private var description: String? =null, private var timestamp: String? = null,  private var userAdm : String? = null ) {
    private var image: ImageView? = null

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("description", description!!)
        result.put("timestamp", timestamp!!)
       // result.put("image", image!!)
        result.put("userAdm", userAdm!!)

        return result
    }
    fun getName() : String {
        return name!!
    }

    fun setName(name : String) {
        this.name = name
    }

    fun getDescription() : String {
        return description!!
    }

    fun setDescription(description : String) {
        this.description = description
    }

    fun getTimestamp() : String {
        return timestamp!!
    }

    fun setTimestamp(timestamp : String) {
        this.timestamp = timestamp
    }

    fun getImage() : ImageView? {
        return image
    }

    fun setImage(image : ImageView) {
        this.image = image
    }

    fun getUserAdm() : String {
        return userAdm!!
    }

    fun setUserAdm(userAdm : String) {
        this.userAdm = userAdm
    }
}