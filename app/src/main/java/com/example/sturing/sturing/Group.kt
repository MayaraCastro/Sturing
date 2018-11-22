package com.example.sturing.sturing

import android.widget.ImageView

class Group {

    private var name : String = ""
    private var description : String = ""
    private var timestamp : String = ""
    private var image : ImageView? = null

    fun getName() : String {
        return name
    }

    fun setName(name : String) {
        this.name = name
    }

    fun getDescription() : String {
        return description
    }

    fun setDescription(description : String) {
        this.description = description
    }

    fun getTimestamp() : String {
        return timestamp
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

}