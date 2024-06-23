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
import android.widget.TextView
import com.example.expensetracker.HomePage
import com.example.expensetracker.MainActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class IncomeFragment : Fragment() {

    private var _binding: FragmentIncomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(IncomeViewModel::class.java)

        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView setup
        val incomeList = root.findViewById<RecyclerView>(R.id.recyclerView)
        var incomes = ArrayList<Income>()
        val incomeAdapter = IncomeAdapter(incomes)
        incomeList.adapter = incomeAdapter
        incomeList.layoutManager = LinearLayoutManager(activity)

        // check via shared preferences for old income
        val _activity = activity as HomePage
        val json = _activity.listView("income")





        val add = root.findViewById<FloatingActionButton>(R.id.add_income)
        add.setOnClickListener {
            // Dialog
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_income_add, null)
            val builder = AlertDialog.Builder(requireContext()).setView(dialogView)
            val dialogBox = builder.show()
            var selectedDate = Calendar.getInstance()
            val saveButton = dialogView.findViewById<Button>(R.id.save_details)
            val cancelButton = dialogView.findViewById<Button>(R.id.cancel)
            val dateButton= dialogView.findViewById<Button>(R.id.btnDatePicker)
            val date_text = dialogView.findViewById<TextView>(R.id.date)
            dateButton.setOnClickListener {
                val calendar = Calendar.getInstance()
                // Show the DatePicker dialog
                context?.let { it1 ->
                    DatePickerDialog(
                        it1, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                            // Set the selected date using the values received from the DatePicker dialog
                            selectedDate.set(year, monthOfYear, dayOfMonth)
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = dateFormat.format(selectedDate.time)
                            date_text.text = formattedDate
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                }?.show()
            }
            saveButton.setOnClickListener {
                dialogBox.dismiss()
                val income = dialogView.findViewById<EditText>(R.id.income_input).text.toString()
                val source = dialogView.findViewById<EditText>(R.id.source_input).text.toString()
                val notes = dialogView.findViewById<EditText>(R.id.extra_notes).text.toString()
                incomes.add(Income.create(income.toInt(), source, notes,date_text.text.toString()))
                incomeAdapter.notifyDataSetChanged()  // Notify the adapter about the data change
            }
            cancelButton.setOnClickListener {
                dialogBox.dismiss()
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
