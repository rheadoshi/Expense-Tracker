package com.example.expensetracker.ui.income

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R

class IncomeAdapter(private val income : List<Income>): RecyclerView.Adapter<IncomeAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amt_view = itemView.findViewById<TextView>(R.id.income_amt)
        val source_view = itemView.findViewById<TextView>(R.id.income_source)
        val notes_view = itemView.findViewById<TextView>(R.id.income_notes)
        val date_view = itemView.findViewById<TextView>(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val rowView = inflater.inflate(R.layout.row_income, parent, false)
        // Return a new holder instance
        return ViewHolder(rowView)
    }

    // Involves populating data into the item through holder
    override fun onBindViewHolder(viewHolder: IncomeAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val m: Income= income.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.amt_view
        textView.text = "\u20B9"+m.amount.toString()
        val textView2 = viewHolder.source_view
        textView2.text = m.source
        val textView3 = viewHolder.notes_view
        textView3.text = m.notes
        val textView4 = viewHolder.date_view
        textView4.text = m.date
    }

    override fun getItemCount(): Int {
        return income.size
    }
}