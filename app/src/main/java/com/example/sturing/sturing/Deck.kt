package com.example.sturing.sturing

data class Deck(var userAuthor: String? = null, var image: String? = null,
                var title: String? = null, var description: String? = null,
                val key: String? = null, val timestamp: String? = null)