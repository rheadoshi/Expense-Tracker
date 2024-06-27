package com.example.expensetracker.ui.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.ui.income.Income
import com.example.expensetracker.ui.income.IncomeAdapter

class ExpenseAdapter(private val expenses : List<Expense>) :RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val amt_view = itemView.findViewById<TextView>(R.id.expense_amt)
        val category_view = itemView.findViewById<TextView>(R.id.expense_category)
        val notes_view = itemView.findViewById<TextView>(R.id.expense_notes)
        val date_view = itemView.findViewById<TextView>(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        // Inflate the custom layout
        val rowView = inflater.inflate(R.layout.row_expense, parent, false)
        // Return a new holder instance
        return ViewHolder(rowView)
    }

    override fun onBindViewHolder(viewHolder: ExpenseAdapter.ViewHolder, position: Int) {
        // Get the data model based on position
        val m = expenses.get(position)
        // Set item views based on your views and data model
        val textView = viewHolder.amt_view
        textView.text = "\u20B9"+m.amount.toString()
        val textView2 = viewHolder.category_view
        textView2.text = m.category
        val textView3 = viewHolder.notes_view
        textView3.text = m.notes
        val textView4 = viewHolder.date_view
        textView4.text = m.date
    }

    override fun getItemCount(): Int {
        return expenses.size
    }


}