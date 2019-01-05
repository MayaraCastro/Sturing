package com.example.sturing.sturing

import android.widget.ImageView
import android.R.attr.author



data class Group(var name: String? = null, var description: String? = null,
                 var userAdm: String? = null, var users: HashMap<String, Boolean>? = null,
                 var questions: HashMap<String, Boolean>? = null,
                 var flashcards: HashMap<String, Boolean>? = null) {

    var groupKey: String? =null

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("name", name!!)
        result.put("description", description!!)
        //result.put("image", image!!)
        result.put("userAdm", userAdm!!)

        return result
    }
}