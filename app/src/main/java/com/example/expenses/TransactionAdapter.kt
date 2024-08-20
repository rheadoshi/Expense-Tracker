package com.example.expenses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private val transactions: List<Transaction>,
    private val actionListener: OnTransactionActionListener
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    interface OnTransactionActionListener {
        fun onEditTransaction(transaction: Transaction)
        fun onDeleteTransaction(transaction: Transaction)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount() = transactions.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvNotes: TextView = itemView.findViewById(R.id.tv_notes)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        private val cardView: CardView = itemView.findViewById(R.id.card_view)
        private val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        private val btnDelete: Button = itemView.findViewById(R.id.btn_delete)

        fun bind(transaction: Transaction) {
            tvDate.text = transaction.date
            tvCategory.text = transaction.category
            tvNotes.text = transaction.notes ?: ""
            tvAmount.text = "₹ ${transaction.amount}"

            // Set background color based on transaction type
            cardView.setCardBackgroundColor(
                when (transaction.transactionType) {
                    TransactionType.INCOME -> itemView.context.getColor(R.color.income_color)
                    TransactionType.EXPENSE -> itemView.context.getColor(R.color.expense_color)
                    TransactionType.SAVINGS -> itemView.context.getColor(R.color.savings_color)
                }
            )

            // Set click listeners for edit and delete buttons
            btnEdit.setOnClickListener {
                actionListener.onEditTransaction(transaction)
            }

            btnDelete.setOnClickListener {
                actionListener.onDeleteTransaction(transaction)
            }
        }
    }
}
