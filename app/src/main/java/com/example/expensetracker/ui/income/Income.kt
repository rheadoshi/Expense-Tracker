package com.example.expensetracker.ui.income

import java.util.Calendar
import java.util.Date

class Income(var amount: Int, var source: String, var notes: String?, var date: String){

    companion object{

        fun create(amt:Int, source:String, notes:String?, date: String):Income {
            return Income(amt,source,notes,date)
        }
    }
}
