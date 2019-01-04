package com.example.sturing.sturing

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_fragment_group_questions.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentGroupQuestions.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentGroupQuestions.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentGroupQuestions : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    var questions : ArrayList<Question> = ArrayList()
    private var groupSelecionado: String? = null
    private var postValues = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val bundle = arguments
        groupSelecionado = bundle!!.getString("group")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_fragment_group_questions, container, false)

        val btAddQuestion = view.findViewById(R.id.btAddQuestion) as CardView

        getQuestionsGroup()

        val recyclerViewQuestion = view.findViewById(R.id.recyclerViewQuestion) as RecyclerView
        recyclerViewQuestion.layoutManager = LinearLayoutManager(activity)
        recyclerViewQuestion.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerViewQuestion.adapter = QuestionAdapter(questions, activity!!.applicationContext, groupSelecionado!!)

        btAddQuestion.setOnClickListener { sendQuestion() }

        startup()

        return view //inflater.inflate(R.layout.fragment_fragment_group_questions, container, false)
    }


    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentGroupQuestions.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentGroupQuestions().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    fun sendQuestion(){

        val user = FirebaseAuth.getInstance().currentUser

        txtQuestion1.error = null

        var question = txtQuestion1.text.toString()

        var cancel = false
        var focusView: View? = null

        if (TextUtils.isEmpty(question)) {
            txtQuestion1.error = getString(R.string.error_field_required)
            focusView = txtQuestion1
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            //showProgress(true)

           // val questionUid = FirebaseDatabase.getInstance().getReference("groups").child(question).push().key
            //val questionRef = FirebaseDatabase.getInstance().getReference("groups").child(questionUid!!)
          //  val imageRef = FirebaseStorage.getInstance().getReference("groupImages").child(questionUid).child("groupImage")
/*
            if (::bitmap.isInitialized) {
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

                val data = baos.toByteArray()
                imageRef.putBytes(data)
                        .addOnFailureListener {
                            Toast.makeText(this@CreateGroupActivity, "Image upload fail",
                                    Toast.LENGTH_SHORT).show()
                        }
                        .addOnSuccessListener {
                            imageRef.downloadUrl
                                    .onSuccessTask { it ->
                                        questionRef.child("image").setValue(it.toString())
                                    }
                        }
            }*/

            writeNewQuestion(question, user!!.uid)

        }

    }

    private fun writeNewQuestion(question: String, userId : String) {
        val database = FirebaseDatabase.getInstance().reference
        val key = database.child("questions").push().key
        val post = Question( userId, question)
        val postValues = post.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/questions/$key", postValues)
        //childUpdates.put("/user-posts/$userId/$key", postValues)

        database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    addGroupQuestion(key!!)
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
        val userRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)
        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var group = p0.getValue(Group::class.java)
                if (group != null) {
                    if (group.questions != null) {
                        postValues = group.questions!!
                    }
                }

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    private fun addGroupQuestion(questionId : String){
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)

        var hash = mutableMapOf<String, Boolean>()
        hash.put(questionId, true)

        for((gp, _) in postValues!!){
            hash.put(gp, true)
        }

        val childUpdates = HashMap<String, Any>()
        childUpdates.put("/questions/", hash)

        userRef.updateChildren(childUpdates)
                .addOnSuccessListener {


                    val i = Intent(activity, MainActivity::class.java)
                    i.putExtra("group", groupSelecionado)
                    i.putExtra("item", 1)
                    i.putExtra("tab", 1)
                    startActivity(i)
                    activity!!.finish()

                }
                .addOnFailureListener {

                }
    }

    fun getQuestionsGroup(){
        val user = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)

        val user1Listener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                var group = p0.getValue(Group::class.java)

                addQuestionsOnList(group!!.questions)

            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(user1Listener)
    }

    fun addQuestionsOnList(questoes : HashMap<String, Boolean>?){

        if (questoes == null) {

            return
        }
        for((questionID, _) in questoes){
            val userRef = FirebaseDatabase.getInstance().getReference("questions").child(questionID)

            val userListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var g1 = p0.getValue(Question::class.java)!!
                    g1!!.questionKey=questionID
                    questions.add(g1!!)
                    recyclerViewQuestion.adapter!!.notifyDataSetChanged()

                }
                override fun onCancelled(p0: DatabaseError) {
                }
            }

            userRef.addListenerForSingleValueEvent(userListener)
        }
    }
}
