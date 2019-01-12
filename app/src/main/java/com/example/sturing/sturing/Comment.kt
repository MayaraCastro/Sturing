package com.example.sturing.sturing

data class Comment(var userAuthor: String? = null, var comment: String? = null) {

    var commentKey: String? = null


    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any>()
        result.put("userAuthor", userAuthor!!)
        result.put("comment", comment!!)
        //result.put("image", image!!)

        return result
    }
}