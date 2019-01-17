package com.example.sturing.sturing

data class Home(var userAuthor: String? = null, var title: String? = null,
                var description: String? = null, var brief: String? = null,
                var content: String? = null, var type: Int = 0,
                val timestamp: String? = null) {
    var userName: String? = null
    var userImage: String? = null
}