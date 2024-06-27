package com.example.expensetracker.ui.income

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import com.example.expensetracker.databinding.FragmentIncomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.app.DatePickerDialog
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.expensetracker.HomePageActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(IncomeViewModel::class.java)

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        root = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val _activity = activity as HomePageActivity
        val gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Income>>() {}.type

        var list = ArrayList<Income>()
        val json = _activity.listView("income")
        if (json != null) {
            list = gson.fromJson(json, type)
        }

        // RecyclerView setup
        val incomeList = root.findViewById<RecyclerView>(R.id.recyclerView)
        val incomeAdapter = IncomeAdapter(list)
        incomeList.adapter = incomeAdapter
        incomeList.layoutManager = LinearLayoutManager(activity)

        val add = root.findViewById<FloatingActionButton>(R.id.add_income)
        add.setOnClickListener {
            showDialogToAddIncome(list, incomeAdapter, gson, _activity)
        }
    }

    private fun showDialogToAddIncome(list: ArrayList<Income>, incomeAdapter: IncomeAdapter, gson: Gson, _activity: HomePageActivity) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_income_add, null)
        val builder = AlertDialog.Builder(requireContext()).setView(dialogView)
        val dialogBox = builder.show()

        var selectedDate = Calendar.getInstance()
        val saveButton = dialogView.findViewById<Button>(R.id.save_details)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
        val dateButton = dialogView.findViewById<Button>(R.id.btnDatePicker)
        val dateText = dialogView.findViewById<TextView>(R.id.date)

        dateButton.setOnClickListener {
            showDatePicker(selectedDate, dateText)
        }

        saveButton.setOnClickListener {
            val incomeInput = dialogView.findViewById<EditText>(R.id.income_input)
            val sourceInput = dialogView.findViewById<EditText>(R.id.source_input)
            val notesInput = dialogView.findViewById<EditText>(R.id.extra_notes)

            val incomeText = incomeInput.text.toString()
            val sourceText = sourceInput.text.toString()
            val dateTextValue = dateText.text.toString()

            if (incomeText.isEmpty() || sourceText.isEmpty() || dateTextValue.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all required values", Toast.LENGTH_SHORT).show()
            } else {
                dialogBox.dismiss()
                val income = incomeText.toInt()
                val source = sourceText
                val notes = notesInput.text.toString()

                list.add(Income(income, source, notes, dateTextValue))
                incomeAdapter.notifyDataSetChanged()  // Notify the adapter about the data change

                val json = gson.toJson(list)
                _activity.addLstView("income", json)
            }
        }

        cancelButton.setOnClickListener {
            dialogBox.dismiss()
        }
    }

    private fun showDatePicker(selectedDate: Calendar, dateText: TextView) {
        val calendar = Calendar.getInstance()
        context?.let {
            DatePickerDialog(
                it, { _, year, monthOfYear, dayOfMonth ->
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    dateText.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


