package com.example.sturing.sturing

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        FragmentGroupQuestions.OnFragmentInteractionListener, FragmentFlashCards.OnFragmentInteractionListener,
        FragmentDeck.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val TAG = "MainActivity"
    private var itemSelecionado: Int = 0
    private var menu: Menu? = null
    private lateinit var mChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            itemSelecionado = savedInstanceState!!.getInt("item")

        } else {
            nav_view.setCheckedItem(R.id.nav_home)
        }
        val item = intent.getIntExtra("item", 0)

        when (item) {
            0 -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            }
            1 -> {
                //Page of group
                val mFrag = GroupPage()
                val bundle = Bundle()
                bundle.putString("group", intent.getStringExtra("group"))   //parameters are (key, value).
                bundle.putInt("tab", intent.getIntExtra("tab", 0))
                bundle.putString("name", intent.getStringExtra("name"))
                bundle.putString("description", intent.getStringExtra("description"))
                mFrag.arguments = bundle  //set the group

                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mFrag).commit()
            }
            else -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupPage()).commit()
            }
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("item", itemSelecionado)
        Log.i("TelaPrincipal", "onSaveInstanceState")
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                this.itemSelecionado = 0
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            }
            R.id.nav_group -> {
                // TODO -> To create fragment of list of groups!
                // Removed fragment create group, changed to '+' icon (fab action)

                this.itemSelecionado = 1
                val i = Intent(this, GroupListActivity::class.java)
                startActivity(i)
            }
            R.id.nav_slideshow -> {
                val i = Intent(this, CreateGroupActivity::class.java)
                startActivity(i)
            }
            R.id.nav_manage -> {
                val i = Intent(this, ManageActivity::class.java)
                startActivity(i)
            }
            R.id.nav_share -> {
                val i = Intent(this, GroupListActivity::class.java)
                startActivity(i)
            }
            R.id.nav_friends -> {

                val i = Intent(this, FindAddUser::class.java)
                i.putExtra("group", intent.getStringExtra("group"))
                i.putExtra("funcao", 2)//mostrar todos os usuarios
                startActivity(i)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun updateUI() {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.i(TAG, "onChildAdded:" + p0!!.key)
                updateChild(p0)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                Log.i(TAG, "onChildChanged:" + p0!!.key)
                updateChild(p0)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                Log.i(TAG, "onChildMoved:" + p0!!.key)
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                Log.i(TAG, "onChildRemoved:" + p0!!.key)
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.i(TAG, "onChildRemoved:" + p0!!.toException())
            }
        }

        userRef.addChildEventListener(childEventListener)

        mChildEventListener = childEventListener

    }

    private fun updateChild(p0: DataSnapshot) {
        var nav = nav_view.getHeaderView(0)

        if (p0!!.key.equals("email")) {
            nav.txtOrganization.hint = p0!!.value as CharSequence
        }
        if (p0!!.key.equals("image")) {
            val imageUrl = p0!!.value as String

            if (imageUrl != null) {
                GlideApp.with(this@MainActivity)
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(withCrossFade())
                        .circleCrop()
                        .into(imgFlashCard)
            }
        }
        if (p0!!.key.equals("name")) {
            nav.txtName.hint = p0!!.value as CharSequence
        }
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onStop() {
        super.onStop()
        if (mChildEventListener != null) {
            val user = FirebaseAuth.getInstance().currentUser
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)
            userRef.removeEventListener(mChildEventListener)
        }
    }
}
