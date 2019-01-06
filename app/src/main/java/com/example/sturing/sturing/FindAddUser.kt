package com.example.sturing.sturing

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.widget.SearchView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_find_add_user.*
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.add_user_item.*
import kotlinx.android.synthetic.main.add_user_item.view.*

class FindAddUser : AppCompatActivity() {

    private var groupSelecionado: String? = null
    var friendList : ArrayList<User> = ArrayList()
    var searchView: SearchView? =null
    private var funcao: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_add_user)

        groupSelecionado = intent!!.getStringExtra("group")
        funcao = intent!!.getIntExtra("funcao", 2)

        if(funcao == 1){
            getFriendsFromBase()
        }
        else if(funcao == 2){
            addAllUsers()
        }

        setSupportActionBar(toolbar3)

        getData("")

        rvFriends.layoutManager = LinearLayoutManager(this)
        rvFriends.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        var item = menu!!.findItem(R.id.app_bar_search)

        searchView =  item.actionView as SearchView
        searchView!!.queryHint = getString(R.string.abc_search_hint)
        searchView!!.setIconifiedByDefault(false)

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                getData(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                getData(query)
                // task HERE
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    fun getData(query: String){
        var filteredData : ArrayList<User> = ArrayList()


        if(searchView!=null && query.isNotEmpty()){
            for(friend in friendList){
                if(friend.name !=null){
                    if(friend.name!!.toLowerCase().startsWith(query.toLowerCase())){
                        filteredData.add(friend)
                    }
                }

            }
        }
        else{
            filteredData = friendList
        }

        rvFriends.adapter = FriendAdapter(filteredData, this!!.applicationContext, funcao, groupSelecionado)
    }
    //LIST
    fun getFriendsFromBase(){
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var use = p0.getValue(User::class.java)!!

                addFriendsOnList(use.friends)

            }
            override fun onCancelled(error: DatabaseError) { }


        }

        userRef.addListenerForSingleValueEvent(userListener)
    }


    fun addFriendsOnList(friends : HashMap<String, Boolean>?){

        if (friends == null) {

            return
        }
        for((userID, friend) in friends){
            if(friend){
                val userRef = FirebaseDatabase.getInstance().getReference("users").child(userID)

                val userListener = object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        var g1 = p0.getValue(User::class.java)!!
                        g1!!.userKey = userID
                        friendList.add(g1!!)
                        rvFriends.adapter!!.notifyDataSetChanged()

                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                }

                userRef.addListenerForSingleValueEvent(userListener)
            }

        }
    }

    fun addAllUsers(){

        val userRef = FirebaseDatabase.getInstance().getReference("users")
        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for ( dsp in p0.children) {
                   var user =  dsp.getValue(User::class.java)

                    user!!.userKey = dsp.key
                    friendList.add(user!!)
                    rvFriends.adapter!!.notifyDataSetChanged()
                }


            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }
        userRef.addListenerForSingleValueEvent(userListener)


    }
}


