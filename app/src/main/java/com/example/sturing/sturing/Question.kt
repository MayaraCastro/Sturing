package com.example.sturing.sturing

data class Question(var userAuthor: String? = null, var question: String? = null, var comments: HashMap<String, Boolean>? = null) {

    var questionKey: String? = null

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any>()
        result.put("userAuthor", userAuthor!!)
        result.put("question", question!!)
        //result.put("image", image!!)
        if (comments != null) {
            result.put("comments", comments!!)
        }

        return result
    }
}