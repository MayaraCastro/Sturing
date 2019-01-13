package com.example.sturing.sturing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_deck.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentDeck.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentDeck.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentDeck : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var deckList: ArrayList<Deck> = ArrayList()
    private var groupSelecionado: String? = null

    private val CREATE_DECK_CODE = 30
    private val TAG = "FragmentDeck"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        val bundle = arguments
        groupSelecionado = bundle!!.getString("group")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_deck, container, false)

        val rvDeck = view.findViewById(R.id.rvDeck) as RecyclerView
        rvDeck.layoutManager = LinearLayoutManager(activity)
        rvDeck.adapter = DeckAdapter(deckList, activity!!.applicationContext, groupSelecionado!!)

        val fabAddDeck = view.findViewById(R.id.fabAddDeck) as FloatingActionButton
        fabAddDeck.setOnClickListener {
            createDeck()
        }

        deckList.sortBy { deck -> deck.timestamp }

        return view
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

        private var postValues = mutableMapOf<String, Boolean>()

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentDeck.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentDeck().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    private fun startup() {
        val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)
        Log.d(TAG, groupSelecionado)

        val groupListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val group = p0.getValue(Group::class.java)
                if (group != null) {
                    Log.d(TAG, group.name)
                    if (group.decks != null) {
                        postValues = group.decks!!
                        Log.d(TAG, postValues.size.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        groupRef.addListenerForSingleValueEvent(groupListener)
    }

    private fun getDecks() {
        val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)
        val decksListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var group = p0.getValue(Group::class.java)
                addDecksOnList(group!!.decks)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        groupRef.addListenerForSingleValueEvent(decksListener)
    }

    private fun addDecksOnList(decks: HashMap<String, Boolean>?) {
        if (decks == null) {
            return
        }
        for ((deckID, _) in decks) {
            val decksRef = FirebaseDatabase.getInstance().getReference("decks").child(deckID)

            val decksListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var deck = p0.getValue(Deck::class.java)

                    if (deck != null) {
                        val exist = deckList.find { it -> it.key == deck.key }
                        if (exist == null) {
                            deckList.add(deck)
                            rvDeck.adapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            }
            decksRef.addListenerForSingleValueEvent(decksListener)
        }
    }

    private fun createDeck() {
        val i = Intent(activity, CreateDeckActivity::class.java)
        startActivityForResult(i, CREATE_DECK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_DECK_CODE) {
            val key = data?.extras?.get("key") as String
            val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)
            var hash = mutableMapOf<String, Boolean>()
            hash.put(key, true)

            for ((card, _) in postValues!!) {
                hash.put(card, true)
            }

            val childUpdates = HashMap<String, Any>()
            childUpdates.put("/decks/", hash)

            groupRef.updateChildren(childUpdates)
        }
    }

    override fun onResume() {
        super.onResume()
        startup()
        getDecks()
    }
}
