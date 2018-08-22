package com.brown.dnarial.blankoftheday

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class QuoteAdapter(val quoteItem: List<QuoteSelection>) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {

    // initiate the views that will be used inside each recycle view
    inner class ViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView){
        val quoteTextView: TextView
        val quotebyTextView: TextView
        val titleTextView: TextView

        init {
            quoteTextView = cardView.findViewById<TextView>(R.id.poemTextView)
            quotebyTextView = cardView.findViewById<TextView>(R.id.poembyTextView)
            titleTextView = cardView.findViewById<TextView>(R.id.titleTextView)
        }
    }

    // use this function to bind the views of the items held in the database
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = quoteItem[position]
        holder.quoteTextView.text =item.quote
        holder.quotebyTextView.text = item.author
        holder.titleTextView.text = item.title

    }

    // use this function so the adapter knows how many views to create
    override fun getItemCount(): Int {
        return quoteItem.size
    }

    // use this function to inflate the views as placed in the pre-created layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.poem_layout, parent, false)
        return ViewHolder(v)
    }
}