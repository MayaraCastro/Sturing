package com.example.sturing.sturing

data class FlashCard(var userAuthor: String? = null, var image: String? = null,
                     var title: String? = null, var subTitle: String? = null,
                     var description: String? = null, var state: Int? = null) {

    fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any>()
        result.put("userAuthor", userAuthor!!)
        result.put("image", image!!)
        result.put("title", title!!)
        result.put("subTitle", subTitle!!)
        result.put("description", description!!)
        result.put("state", state!!)

        return result
    }

}