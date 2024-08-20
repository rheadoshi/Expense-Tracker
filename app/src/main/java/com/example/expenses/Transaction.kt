package com.example.expenses

import java.util.Date

enum class TransactionType {
    INCOME,
    EXPENSE,
    SAVINGS
}

data class Transaction(
    val date: String,
    val category: String,
    val notes: String?,
    val amount: Double,
    val transactionType: TransactionType
)

