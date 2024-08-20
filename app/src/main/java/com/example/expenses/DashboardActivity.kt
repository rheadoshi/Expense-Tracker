package com.example.expenses

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity(), TransactionAdapter.OnTransactionActionListener {

    private lateinit var totalBalanceTextView: TextView
    private lateinit var totalIncomeTextView: TextView
    private lateinit var totalSpendingTextView: TextView
    private lateinit var totalSavingsTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionsAdapter: TransactionAdapter

    private var totalIncome = 0
    private var totalSpending = 0
    private var totalSavings = 0
    private var totalBalance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        totalBalanceTextView = findViewById(R.id.tv_total_balance)
        totalIncomeTextView = findViewById(R.id.tv_total_income)
        totalSpendingTextView = findViewById(R.id.tv_total_spending)
        totalSavingsTextView = findViewById(R.id.tv_total_savings)
        recyclerView = findViewById(R.id.recycler_view_transactions)

        val fabAddTransaction = findViewById<FloatingActionButton>(R.id.fab_add_transaction)
        fabAddTransaction.setOnClickListener {
            showAddTransactionDialog()
        }

        val fabViewChart = findViewById<FloatingActionButton>(R.id.fab_view_chart)
        fabViewChart.setOnClickListener {
            showMonthInputDialog()
        }

        setupRecyclerView()
        loadTransactionData()
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionAdapter(emptyList(), this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionsAdapter
    }

    private fun loadTransactionData() {
        val sharedPreferences = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsJson = sharedPreferences.getString("transactions", "[]") ?: "[]"

        val transactionsType = object : TypeToken<List<Transaction>>() {}.type
        val transactions: List<Transaction> = Gson().fromJson(transactionsJson, transactionsType)

        // Calculate totals
        totalIncome = transactions.filter { it.transactionType == TransactionType.INCOME }.sumOf { it.amount }.toInt()
        totalSpending = transactions.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }.toInt()
        totalSavings = transactions.filter { it.transactionType == TransactionType.SAVINGS }.sumOf { it.amount }.toInt()
        totalBalance = totalIncome - totalSpending + totalSavings

        // Update UI
        totalIncomeTextView.text = "Income: ₹$totalIncome"
        totalSpendingTextView.text = "Spending: ₹$totalSpending"
        totalSavingsTextView.text = "Savings: ₹$totalSavings"
        totalBalanceTextView.text = "Total Balance: ₹$totalBalance"

        transactionsAdapter = TransactionAdapter(transactions, this)
        recyclerView.adapter = transactionsAdapter
    }

    private fun showAddTransactionDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinner_category)
        val amountEditText: EditText = dialogView.findViewById(R.id.edit_text_amount)
        val notesEditText: EditText = dialogView.findViewById(R.id.edit_text_notes)
        val dateEditText: EditText = dialogView.findViewById(R.id.edit_text_date)
        val typeRadioGroup: RadioGroup = dialogView.findViewById(R.id.radio_group_type)

        val categories = arrayOf("Food", "Clothes", "Job", "Part-time", "Gift", "Invest", "Other")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)

        // Add TextWatchers
        amountEditText.addTextChangedListener(DialogTextWatcher(amountEditText))
        notesEditText.addTextChangedListener(DialogTextWatcher(notesEditText))
        dateEditText.addTextChangedListener(DialogTextWatcher(dateEditText))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Transaction")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val amount = amountEditText.text.toString().toIntOrNull() ?: 0
                val notes = notesEditText.text.toString()
                val date = dateEditText.text.toString()
                val selectedTypeId = typeRadioGroup.checkedRadioButtonId
                val type = when (selectedTypeId) {
                    R.id.radio_income -> TransactionType.INCOME
                    R.id.radio_expense -> TransactionType.EXPENSE
                    else -> TransactionType.SAVINGS
                }

                // Create a new transaction
                val transaction = Transaction(date, categorySpinner.selectedItem.toString(), notes, amount.toDouble(), type)

                // Save the transaction to SharedPreferences
                saveTransactionToPreferences(transaction)

                // Refresh data
                loadTransactionData()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun saveTransactionToPreferences(transaction: Transaction) {
        val sharedPreferences = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsJson = sharedPreferences.getString("transactions", "[]") ?: "[]"

        val transactionsType = object : TypeToken<MutableList<Transaction>>() {}.type
        val transactions: MutableList<Transaction> = Gson().fromJson(transactionsJson, transactionsType) ?: mutableListOf()

        transactions.add(transaction)

        val newTransactionsJson = Gson().toJson(transactions)
        sharedPreferences.edit().putString("transactions", newTransactionsJson).apply()
    }

    private fun showMonthInputDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input_month, null)
        val monthEditText: EditText = dialogView.findViewById(R.id.edit_text_month)
        val yearEditText: EditText = dialogView.findViewById(R.id.edit_text_year)

        // Add TextWatchers
        monthEditText.addTextChangedListener(DialogTextWatcher(monthEditText))
        yearEditText.addTextChangedListener(DialogTextWatcher(yearEditText))

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Month and Year")
            .setView(dialogView)
            .setPositiveButton("Show Chart") { _, _ ->
                val month = monthEditText.text.toString().toIntOrNull()
                val year = yearEditText.text.toString().toIntOrNull()
                if (month != null && year != null) {
                    showPieChart(month, year)
                } else {
                    Toast.makeText(this, "Please enter valid month and year", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun showPieChart(month: Int, year: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pie_chart, null)
        val pieChart: PieChart = dialogView.findViewById(R.id.pie_chart)

        val sharedPreferences = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsJson = sharedPreferences.getString("transactions", "[]") ?: "[]"

        val transactionsType = object : TypeToken<List<Transaction>>() {}.type
        val transactions = Gson().fromJson<List<Transaction>>(transactionsJson, transactionsType)

        val filteredTransactions = transactions.filter {
            val transactionDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(it.date)
            val calendar = Calendar.getInstance().apply { time = transactionDate }
            calendar.get(Calendar.MONTH) + 1 == month && calendar.get(Calendar.YEAR) == year
        }

        val totalIncome = filteredTransactions.filter { it.transactionType == TransactionType.INCOME }.sumOf { it.amount }
        val totalSpending = filteredTransactions.filter { it.transactionType == TransactionType.EXPENSE }.sumOf { it.amount }
        val totalSavings = filteredTransactions.filter { it.transactionType == TransactionType.SAVINGS }.sumOf { it.amount }

        val entries = listOf(
            PieEntry(totalIncome.toFloat(), "Income"),
            PieEntry(totalSpending.toFloat(), "Spending"),
            PieEntry(totalSavings.toFloat(), "Savings")
        )

        val dataSet = PieDataSet(entries, "Transactions")
        dataSet.colors = listOf(Color.GREEN, Color.RED, Color.BLUE)
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        pieChart.data = PieData(dataSet)
        pieChart.invalidate()

        AlertDialog.Builder(this)
            .setTitle("Transaction Summary for $month/$year")
            .setView(dialogView)
            .setPositiveButton("Close", null)
            .create()
            .show()
    }

    override fun onEditTransaction(transaction: Transaction) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null)
        val categorySpinner: Spinner = dialogView.findViewById(R.id.spinner_category)
        val amountEditText: EditText = dialogView.findViewById(R.id.edit_text_amount)
        val notesEditText: EditText = dialogView.findViewById(R.id.edit_text_notes)
        val dateEditText: EditText = dialogView.findViewById(R.id.edit_text_date)
        val typeRadioGroup: RadioGroup = dialogView.findViewById(R.id.radio_group_type)

        val categories = arrayOf("Food", "Clothes", "Job", "Part-time", "Gift", "Invest", "Other")
        categorySpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.setSelection(categories.indexOf(transaction.category))

        amountEditText.setText(transaction.amount.toInt().toString())
        notesEditText.setText(transaction.notes)
        dateEditText.setText(transaction.date)

        typeRadioGroup.check(
            when (transaction.transactionType) {
                TransactionType.INCOME -> R.id.radio_income
                TransactionType.EXPENSE -> R.id.radio_expense
                else -> R.id.radio_savings
            }
        )

        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Transaction")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val amount = amountEditText.text.toString().toIntOrNull() ?: 0
                val notes = notesEditText.text.toString()
                val date = dateEditText.text.toString()
                val selectedTypeId = typeRadioGroup.checkedRadioButtonId
                val type = when (selectedTypeId) {
                    R.id.radio_income -> TransactionType.INCOME
                    R.id.radio_expense -> TransactionType.EXPENSE
                    else -> TransactionType.SAVINGS
                }

                // Update transaction
                val updatedTransaction = transaction.copy(date = date, category = categorySpinner.selectedItem.toString(), notes = notes, amount = amount.toDouble(), transactionType = type)
                updateTransactionInPreferences(transaction, updatedTransaction)

                // Refresh data
                loadTransactionData()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    override fun onDeleteTransaction(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton("Delete") { _, _ ->
                deleteTransactionFromPreferences(transaction)
                loadTransactionData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateTransactionInPreferences(oldTransaction: Transaction, updatedTransaction: Transaction) {
        val sharedPreferences = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsJson = sharedPreferences.getString("transactions", "[]") ?: "[]"

        val transactionsType = object : TypeToken<MutableList<Transaction>>() {}.type
        val transactions: MutableList<Transaction> = Gson().fromJson(transactionsJson, transactionsType) ?: mutableListOf()

        val index = transactions.indexOf(oldTransaction)
        if (index >= 0) {
            transactions[index] = updatedTransaction
        }

        val newTransactionsJson = Gson().toJson(transactions)
        sharedPreferences.edit().putString("transactions", newTransactionsJson).apply()
    }

    private fun deleteTransactionFromPreferences(transaction: Transaction) {
        val sharedPreferences = getSharedPreferences("transactions_prefs", MODE_PRIVATE)
        val transactionsJson = sharedPreferences.getString("transactions", "[]") ?: "[]"

        val transactionsType = object : TypeToken<MutableList<Transaction>>() {}.type
        val transactions: MutableList<Transaction> = Gson().fromJson(transactionsJson, transactionsType) ?: mutableListOf()

        transactions.remove(transaction)

        val newTransactionsJson = Gson().toJson(transactions)
        sharedPreferences.edit().putString("transactions", newTransactionsJson).apply()
    }

    private inner class DialogTextWatcher(private val editText: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val parent = editText.parent as? LinearLayout
            val index = parent?.indexOfChild(editText) ?: -1
            if (index >= 0 && s?.length == 1) {
                // Move to next field if there is input
                if (index < parent?.childCount?.minus(1) ?: 0) {
                    parent?.getChildAt(index + 1)?.requestFocus()
                }
            } else if (s?.length == 0 && index > 0) {
                // Move focus back if backspace is pressed
                parent?.getChildAt(index - 1)?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            val dialog = (editText.parent as? AlertDialog)?.let { it as AlertDialog }
            if (dialog != null) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = allFieldsFilled(dialog)
            }
        }

        private fun allFieldsFilled(dialog: AlertDialog): Boolean {
            val amountEditText: EditText = dialog.findViewById(R.id.edit_text_amount) ?: return false
            val notesEditText: EditText = dialog.findViewById(R.id.edit_text_notes) ?: return false
            val dateEditText: EditText = dialog.findViewById(R.id.edit_text_date) ?: return false

            return amountEditText.text.isNotEmpty() && notesEditText.text.isNotEmpty() && dateEditText.text.isNotEmpty()
        }
    }
}
