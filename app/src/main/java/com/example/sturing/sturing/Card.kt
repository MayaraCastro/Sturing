package com.example.sturing.sturing

data class Card(var userAuthor: String? = null, var question: String? = null,
                var answer: String? = null, val key: String? = null,
                val timestamp: String? = null) {
    var questionMoment = true
}