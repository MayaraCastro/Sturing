package com.example.sturing.sturing

import android.widget.ImageView

data class User(val name: String? = null, val email: String? = null, val image: String? = null, var groups: HashMap<String, Boolean>? = null,
                var friends: HashMap<String, Boolean>? = null) {
    var userKey: String? = null
    //private var groups : ArrayList<String> = ArrayList()

   //fun getGroups() : ArrayList<String>?{
   //    return groups
   //}

    //fun setGroups(mGroups : ArrayList<String>) {
    //    groups = mGroups
    //}


}
