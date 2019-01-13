package com.example.sturing.sturing

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.card_item.view.*

class CardAdapter(var items: ArrayList<Card>, var context: Context) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(p0.context).inflate(R.layout.card_item, p0, false)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        if (items[0].key == null)
            return 0
        return 1
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {

        p0.txtCardAnswer.text = items[p1].answer
        p0.txtCardQuestion.text = items[p1].question

        p0.txtCardAnswer.animate().alpha(0F).setDuration(1).start()
        p0.txtAnswer.animate().alpha(0F).setDuration(1).start()
        p0.txtCardQuestion.animate().alpha(1.0F).setDuration(200).start()
        p0.txtQuestion.animate().alpha(1.0F).setDuration(200).start()

        p0.cvCard.setOnClickListener {
            items[p1].questionMoment = !items[p1].questionMoment
            if (items[p1].questionMoment) {
                p0.txtCardAnswer.animate().alpha(0F).setDuration(200).start()
                p0.txtAnswer.animate().alpha(0F).setDuration(200).start()
                p0.txtCardQuestion.animate().alpha(1.0F).setDuration(400).start()
                p0.txtQuestion.animate().alpha(1.0F).setDuration(400).start()
            } else {
                p0.txtCardAnswer.animate().alpha(1.0F).setDuration(400).start()
                p0.txtAnswer.animate().alpha(1.0F).setDuration(400).start()
                p0.txtCardQuestion.animate().alpha(0F).setDuration(200).start()
                p0.txtQuestion.animate().alpha(0F).setDuration(200).start()
            }
        }

        setAnimation(p0.cvCard)
    }

    private fun setAnimation(viewToAnimate: View) {
        val animation = AnimationUtils.loadAnimation(viewToAnimate.context, android.R.anim.fade_in)
        viewToAnimate.animation = animation
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtQuestion = view.txtQuestion
        val txtCardQuestion = view.txtCardQuestion
        val txtAnswer = view.txtAnswer
        val txtCardAnswer = view.txtCardAnswer
        val cvCard = view.cvCard
    }
}