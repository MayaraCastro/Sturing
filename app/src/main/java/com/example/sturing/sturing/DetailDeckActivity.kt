package com.example.sturing.sturing

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.example.sturing.sturing.Glide.GlideApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail_deck.*

class DetailDeckActivity : AppCompatActivity() {

    private val TAG = "DetailDeckActivity"
    private var deckKey: String? = null
    private var cardsList: ArrayList<Card> = ArrayList()
    private var actualCard: ArrayList<Card> = ArrayList()
    private val CREATE_CARD_CODE = 20
    private var index = 0
    private var postValues = mutableMapOf<String, Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_deck)

        val extra = intent.extras
        val author = extra.getString("author")
        val image = extra.getString("image")
        val title = extra.getString("title")
        val description = extra.getString("description")
        deckKey = extra.getString("key")
        ivImage.transitionName = deckKey

        cardsList.sortBy { card -> card.timestamp }
        actualCard.add(Card())
        rvCards.layoutManager = LinearLayoutManager(this)
        rvCards.adapter = CardAdapter(actualCard, this)

        val userRef = FirebaseDatabase.getInstance().getReference("users").child(author)

        val userListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)
                txtAuthor.text = user!!.name
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }

        userRef.addListenerForSingleValueEvent(userListener)

        if (!image.isNullOrEmpty()) {
            GlideApp.with(this)
                    .load(image)
                    .dontAnimate()
                    .into(ivImage)
        } else {
            ivImage.setBackgroundColor(Color.WHITE)
            GlideApp.with(this)
                    .load(R.drawable.deck_of_cards)
                    .dontAnimate()
                    .into(ivImage)
        }

        txtTitle.text = title
        txtAnswer.text = description

        fabBack.setOnClickListener {
            onBackPressed()
        }

        fabAddCard.setOnClickListener {
            createCard()
        }

        imbNext.setOnClickListener {
            next()
        }

        imbPrevious.setOnClickListener {
            previous()
        }
    }

    private fun updateUI() {
        val number = (index + 1).toString() + " / " + cardsList.size
        txtNumber.text = number
        when (cardsList.size) {
            0 -> {
                txtTipAddCard.visibility = View.VISIBLE
                imbNext.visibility = View.GONE
                imbPrevious.visibility = View.GONE
                txtNumber.visibility = View.GONE
            }
            1 -> {
                actualCard[0] = cardsList[index]
                rvCards.adapter!!.notifyDataSetChanged()
                txtTipAddCard.visibility = View.GONE
                imbNext.visibility = View.GONE
                imbPrevious.visibility = View.GONE
                txtNumber.visibility = View.VISIBLE
            }
            else -> {
                actualCard[0] = cardsList[index]
                rvCards.adapter!!.notifyDataSetChanged()
                txtTipAddCard.visibility = View.GONE
                imbNext.visibility = View.VISIBLE
                imbPrevious.visibility = View.VISIBLE
                txtNumber.visibility = View.VISIBLE
            }
        }
    }

    private fun startup() {
        val deckRef = FirebaseDatabase.getInstance().getReference("decks").child(deckKey!!)
        Log.d(TAG, deckKey)

        val groupListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val deck = p0.getValue(Deck::class.java)
                if (deck != null) {
                    Log.d(TAG, deck.title)
                    if (deck.cards != null) {
                        postValues = deck.cards!!
                        Log.d(TAG, postValues.size.toString())
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        deckRef.addListenerForSingleValueEvent(groupListener)
    }


    private fun getCards() {
        val deckRef = FirebaseDatabase.getInstance().getReference("decks").child(deckKey!!)
        val cardsListener = object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var deck = p0.getValue(Deck::class.java)
                addCardOnList(deck!!.cards)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        }
        deckRef.addListenerForSingleValueEvent(cardsListener)
    }

    private fun addCardOnList(cards: HashMap<String, Boolean>?) {
        if (cards == null) {
            return
        }
        for ((cardID, _) in cards) {
            val cardsRef = FirebaseDatabase.getInstance().getReference("cards").child(cardID)

            val cardsListener = object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    var card = p0.getValue(Card::class.java)

                    if (card != null) {
                        if (!cardsList.contains(card)) {
                            cardsList.add(card)
                            rvCards.adapter!!.notifyDataSetChanged()
                            updateUI()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            }
            cardsRef.addListenerForSingleValueEvent(cardsListener)
        }
    }

    private fun createCard() {
        val i = Intent(this, CreateCardActivity::class.java)
        startActivityForResult(i, CREATE_CARD_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CREATE_CARD_CODE) {
            val key = data?.extras?.get("key") as String
            val deckRef = FirebaseDatabase.getInstance().getReference("decks").child(deckKey!!)
            var hash = mutableMapOf<String, Boolean>()
            hash.put(key, true)

            for ((card, _) in postValues!!) {
                hash.put(card, true)
            }

            val childUpdates = HashMap<String, Any>()
            childUpdates.put("/cards/", hash)

            deckRef.updateChildren(childUpdates)
        }
    }

    private fun next() {
        if (index == cardsList.size - 1) {
            index = 0
        } else {
            index++
        }
        updateUI()
    }

    private fun previous() {
        if (index == 0) {
            index = cardsList.size - 1
        } else {
            index--
        }
        updateUI()
    }

    override fun onResume() {
        super.onResume()
        getCards()
        startup()
        updateUI()
    }
}
