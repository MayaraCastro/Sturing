package com.example.sturing.sturing

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "MainActivity"

    private var itemSelecionado : Int = 0
    private var menu : Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (savedInstanceState != null) {
            itemSelecionado = savedInstanceState!!.getInt("item")
        } else {
            nav_view.setCheckedItem(R.id.nav_home)
        }


        when(itemSelecionado){
            0->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
            }
            1->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupPage()).commit()
            }
            else->{
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupPage()).commit()
            }
        }

        fab.setOnClickListener {
            //Snackbar.make(view, R.string.app_name, Snackbar.LENGTH_SHORT)
            //        .setAction("Action", null).show()
            val i = Intent(this, CreateGroupActivity::class.java)
            startActivity(i)
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        //nav_view.setCheckedItem(R.id.nav_home)
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
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
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, GroupPage()).commit()
            }
           /* R.id.nav_slideshow -> {

            }*/
            R.id.nav_manage -> {
                val i = Intent(this, ManageActivity::class.java)
                startActivity(i)
            }
            R.id.nav_share -> {
                val i = Intent(this, GroupListActivity::class.java)
                startActivity(i)
            }
           /* R.id.nav_send -> {

            }*/
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStart() {
        super.onStart()
        Log.i("Tela1", "onStart")
    }

    private fun updateUI() {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("users").child(user!!.uid)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var nav = nav_view.getHeaderView(0)
                nav.txtName.hint = p0.child("name").getValue(String::class.java)
                nav.txtOrganization.hint = p0.child("email").getValue(String::class.java)
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(userListener)

    }

    override fun onResume() {
        super.onResume()
        updateUI()
        Log.i("Tela1", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.i("Tela1", "onPause")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("Tela1", "onRestart")
    }

    override fun onStop() {
        super.onStop()
        Log.i("Tela1", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("Tela1", "onDetroy")
    }
}
