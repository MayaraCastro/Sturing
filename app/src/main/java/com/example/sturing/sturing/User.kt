package com.example.sturing.sturing

import android.util.Log

data class User(val name: String? = null, val email: String? = null, val image: String? = null, var groups: HashMap<String, Boolean>? = null, var friends: HashMap<String, Boolean>? = null, var friend_requests: HashMap<String, Boolean>? = null) {
    var userKey: String? = null
    //private var groups : ArrayList<String> = ArrayList()
    //fun getGroups() : ArrayList<String>?{
    //    return groups
    //}

    //fun setGroups(mGroups : ArrayList<String>) {
    //    groups = mGroups
    //}

    override fun equals(other: Any?): Boolean {
        var use = other as User
        Log.d("EQUALS", use.userKey)
        if(use.userKey.equals( this.userKey)){
            return true
        }
        return false
        return super.equals(other)
    }
}
