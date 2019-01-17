package com.example.sturing.sturing

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var postsList: ArrayList<Home> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        postsList.sortBy { home -> home.timestamp }

        val rvHome = view.findViewById(R.id.rvHome) as RecyclerView
        rvHome.layoutManager = LinearLayoutManager(activity)
        rvHome.adapter = HomeAdapter(postsList, activity!!.applicationContext)

        getPosts()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun getPosts() {
        val postsRef = FirebaseDatabase.getInstance().getReference("posts").child("post1")
        val postsListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(Home::class.java)
                if (post != null)
                    addPostOnList(post)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        postsRef.addListenerForSingleValueEvent(postsListener)
    }

    private fun addPostOnList(post: Home?) {
        if (post == null) {
            if (post!!.userAuthor == null)
                return
        }

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(post.userAuthor!!)
        val usersListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                if (user != null) {
                    if (user.image != null)
                        post.userImage = user.image
                    if (user.name != null)
                        post.userName = user.name
                    val exist = postsList.find { home -> home.timestamp == post.timestamp }
                    if (exist == null) {
                        postsList.add(post)
                        rvHome.adapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        userRef.addListenerForSingleValueEvent(usersListener)
    }
}
