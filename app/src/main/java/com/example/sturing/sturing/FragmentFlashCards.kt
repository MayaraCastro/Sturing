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
import kotlinx.android.synthetic.main.fragment_flash_cards.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FragmentFlashCards.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FragmentFlashCards.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FragmentFlashCards : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private var cardToDo: ArrayList<FlashCard> = ArrayList()
    private var cardDoing: ArrayList<FlashCard> = ArrayList()
    private var cardDone: ArrayList<FlashCard> = ArrayList()
    private var groupSelecionado: String? = null

    private val CREATE_FLASH_CARD_CODE = 40
    private val TAG = "FragmentFlashCards"

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

        val view = inflater.inflate(R.layout.fragment_flash_cards, container, false)

        val rvToDo = view.findViewById(R.id.rvToDo) as RecyclerView
        rvToDo.layoutManager = LinearLayoutManager(activity)
        rvToDo.adapter = FlashCardAdapter(cardToDo, activity!!.applicationContext, groupSelecionado!!)

        val rvDoing = view.findViewById(R.id.rvDoing) as RecyclerView
        rvDoing.layoutManager = LinearLayoutManager(activity)
        rvDoing.adapter = FlashCardAdapter(cardDoing, activity!!.applicationContext, groupSelecionado!!)

        val rvDone = view.findViewById(R.id.rvDone) as RecyclerView
        rvDone.layoutManager = LinearLayoutManager(activity)
        rvDone.adapter = FlashCardAdapter(cardDone, activity!!.applicationContext, groupSelecionado!!)

        cardToDo.sortBy { flashCard -> flashCard.timestamp }
        cardDoing.sortBy { flashCard -> flashCard.timestamp }
        cardDone.sortBy { flashCard -> flashCard.timestamp }

        val fabAddCard = view.findViewById(R.id.fabAddCard) as FloatingActionButton
        fabAddCard.setOnClickListener {
            createFlashCard()
        }

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
         * @return A new instance of fragment FragmentFlashCards.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FragmentFlashCards().apply {
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
                    if (group.flashcards != null) {
                        postValues = group.flashcards!!
                        Log.d(TAG, postValues.size.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        groupRef.addListenerForSingleValueEvent(groupListener)
    }

    private fun getFlashCards() {
        val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)

        val cardsListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var group = p0.getValue(Group::class.java)
                addFlashCardsOnList(group!!.flashcards)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        groupRef.addListenerForSingleValueEvent(cardsListener)
    }

    private fun addFlashCardsOnList(flashCards: HashMap<String, Boolean>?) {
        if (flashCards == null) {
            return
        }
        for ((cardID, _) in flashCards) {
            val cardsRef = FirebaseDatabase.getInstance().getReference("flashcards").child(cardID)

            val cardsListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var flashCard = p0.getValue(FlashCard::class.java)

                    if (flashCard != null) {
                        when (flashCard.state) {
                            0 -> {
                                if (!cardToDo.contains(flashCard)) {
                                    cardToDo.add(flashCard)
                                    rvToDo.adapter!!.notifyDataSetChanged()
                                    removeFlashCard(flashCard, 0)
                                }
                            }
                            1 -> {
                                if (!cardDoing.contains(flashCard)) {
                                    cardDoing.add(flashCard)
                                    rvDoing.adapter!!.notifyDataSetChanged()
                                    removeFlashCard(flashCard, 1)
                                }
                            }
                            2 -> {
                                if (!cardDone.contains(flashCard)) {
                                    cardDone.add(flashCard)
                                    rvDone.adapter!!.notifyDataSetChanged()
                                    removeFlashCard(flashCard, 2)
                                }
                            }
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            }
            cardsRef.addListenerForSingleValueEvent(cardsListener)
        }
    }

    private fun removeFlashCard(card: FlashCard, state: Int) {
        when (state) {
            0 -> {
                val existDoing = cardDoing.find { flashCard -> flashCard.key == card.key }
                if (cardDoing.contains(existDoing)) {
                    cardDoing.remove(existDoing)
                    rvDoing.adapter!!.notifyDataSetChanged()
                }
                val existDone = cardDone.find { flashCard -> flashCard.key == card.key }
                if (cardDone.contains(existDone)) {
                    cardDone.remove(existDone)
                    rvDone.adapter!!.notifyDataSetChanged()
                }
            }
            1 -> {
                val existToDo = cardToDo.find { flashCard -> flashCard.key == card.key }
                if (cardToDo.contains(existToDo)) {
                    cardToDo.remove(existToDo)
                    rvToDo.adapter!!.notifyDataSetChanged()
                }
                val existDone = cardDone.find { flashCard -> flashCard.key == card.key }
                if (cardDone.contains(existDone)) {
                    cardDone.remove(existDone)
                    rvDone.adapter!!.notifyDataSetChanged()
                }
            }
            2 -> {
                val existToDo = cardToDo.find { flashCard -> flashCard.key == card.key }
                if (cardToDo.contains(existToDo)) {
                    cardToDo.remove(existToDo)
                    rvToDo.adapter!!.notifyDataSetChanged()
                }
                val existDoing = cardDoing.find { flashCard -> flashCard.key == card.key }
                if (cardDoing.contains(existDoing)) {
                    cardDoing.remove(existDoing)
                    rvDoing.adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun createFlashCard() {
        val i = Intent(activity, CreateFlashCardActivity::class.java)
        startActivityForResult(i, CREATE_FLASH_CARD_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_FLASH_CARD_CODE) {
            val key = data?.extras?.get("key") as String
            val groupRef = FirebaseDatabase.getInstance().getReference("groups").child(groupSelecionado!!)
            var hash = mutableMapOf<String, Boolean>()
            hash.put(key, true)

            for ((card, _) in postValues!!) {
                hash.put(card, true)
            }

            val childUpdates = HashMap<String, Any>()
            childUpdates.put("/flashcards/", hash)

            groupRef.updateChildren(childUpdates)
        }
    }

    override fun onResume() {
        super.onResume()
        startup()
        getFlashCards()
    }
}
