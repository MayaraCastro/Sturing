package com.example.sturing.sturing

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_question.*
import kotlinx.android.synthetic.main.fragment_group_questions.*

class QuestionActivity : AppCompatActivity() {

    private var question: String? = null
    private var groupSelecionado: String? = null

    var comments : ArrayList<Comment> = ArrayList()
    private var postValues = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        question = intent.getStringExtra("question")
        groupSelecionado = intent.getStringExtra("group")

        getQuestionFromBase()

        recyclerViewComment.layoutManager = LinearLayoutManager(this)
        recyclerViewComment.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerViewComment.adapter = CommentAdapter(comments, this!!.applicationContext)

        btAddComment.setOnClickListener {
            sendQuestion()
        }
        startup()

    }
    override fun onBackPressed() {
         //super.onBackPressed()
        val i = Intent(this, MainActivity::class.java)
        i.putExtra("group", groupSelecionado!!)
        i.putExtra("item", 1)
        i.putExtra("tab", 1)
        startActivity(i)
        finish()

    }

    fun sendQuestion(){

        val user = FirebaseAuth.getInstance().currentUser

        txtComment.error = null

        var comment = txtComment.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(comment)) {
            txtComment.error = getString(R.string.error_field_required)
            focusView = txtComment
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {

            writeNewComment(comment, user!!.uid)

        }

    }

    //LIST
    fun getQuestionFromBase(){
        val questionRef = FirebaseDatabase.getInstance().getReference("questions").child(question!!)

        val questionListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var quest = p0.getValue(Question::class.java)!!

                txtQuestion.text = quest.question
                getAuthorFromBase(quest.userAuthor!!)

                getcommentsFromBase(quest.comments)

            }
            override fun onCancelled(error: DatabaseError) { }


        }

        questionRef.addListenerForSingleValueEvent(questionListener)
    }

    fun getAuthorFromBase(userID: String){

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(userID)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var user = p0.getValue(User::class.java)

                if (user!!.image != null) {
                    GlideApp.with(applicationContext)
                            .load(user!!.image)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .circleCrop()
                            .into(imgUser)
                }
                txtName.text = user!!.name


            }
            override fun onCancelled(error: DatabaseError) { }


        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    private fun getcommentsFromBase(comments: HashMap<String, Boolean>?) {

        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("questions").child(question!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var quest = p0.getValue(Question::class.java)

                addCommentsOnList(quest!!.comments)

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    fun addCommentsOnList(comentarios : HashMap<String, Boolean>?){

        if (comentarios == null) {

            return
        }
        for((commentID, _) in comentarios){
            val userRef = FirebaseDatabase.getInstance().getReference("comments").child(commentID)

            val userListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var g1 = p0.getValue(Comment::class.java)!!
                    g1!!.commentKey = commentID
                    comments.add(g1!!)
                    recyclerViewComment.adapter!!.notifyDataSetChanged()

                }
                override fun onCancelled(p0: DatabaseError) {
                }
            }

            userRef.addListenerForSingleValueEvent(userListener)
        }
    }


    //create

    private fun writeNewComment(comment: String, userId : String) {
        val database = FirebaseDatabase.getInstance().reference
        val key = database.child("questions").push().key
        val post = Comment( userId, comment)
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/comments/$key", postValues)
        //childUpdates.put("/user-posts/$userId/$key", postValues)

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    addQuestionComment(key!!)
                    /*val i = Intent(this, MainActivity::class.java)
                    startActivity(i)
                    showProgress(false)
                    finish()*/
                }
                .addOnFailureListener {
                    /*Toast.makeText(this@FragmentGroupQuestions, getString(R.string.registration_failed),
                            Toast.LENGTH_SHORT).show()*/
                    Log.w("create group", "User didn`t add.")
                }
    }

    private fun startup() {
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("questions").child(question!!)
        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var quest = p0.getValue(Question::class.java)
                if (quest != null) {
                    if (quest.comments != null) {
                        postValues = quest.comments!!
                    }
                }

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    private fun addQuestionComment(commentId : String){
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("questions").child(question!!)

        var hash = mutableMapOf<String, Boolean>()
        hash.put(commentId, true)

        for((gp, _) in postValues!!){
            hash.put(gp, true)
        }

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/comments/", hash)

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener {


                    val i = Intent(this, QuestionActivity::class.java)
                    i.putExtra("question", question)
                    i.putExtra("group", groupSelecionado)
                    startActivity(i)
                    finish()

                }
                .addOnFailureListener {

                }
    }

}
