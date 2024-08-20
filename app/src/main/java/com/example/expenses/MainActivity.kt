package com.example.expenses

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var txtOtp1: EditText
    private lateinit var txtOtp2: EditText
    private lateinit var txtOtp3: EditText
    private lateinit var txtOtp4: EditText
    private lateinit var submitButton: Button
    private lateinit var verifyTextView: TextView

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        txtOtp1 = findViewById(R.id.txt_otp1)
        txtOtp2 = findViewById(R.id.txt_otp2)
        txtOtp3 = findViewById(R.id.txt_otp3)
        txtOtp4 = findViewById(R.id.txt_otp4)
        submitButton = findViewById(R.id.submitButton)
        verifyTextView = findViewById(R.id.verify)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PIN_PREF", Context.MODE_PRIVATE)

        // Retrieve the stored pin from SharedPreferences
        val storedPin = sharedPreferences.getString("PIN_KEY", null)

        // Add TextWatchers
        txtOtp1.addTextChangedListener(PinTextWatcher(txtOtp1, txtOtp2))
        txtOtp2.addTextChangedListener(PinTextWatcher(txtOtp2, txtOtp3, txtOtp1))
        txtOtp3.addTextChangedListener(PinTextWatcher(txtOtp3, txtOtp4, txtOtp2))
        txtOtp4.addTextChangedListener(PinTextWatcher(txtOtp4, null, txtOtp3))

        // Set up the button click listener
        submitButton.setOnClickListener {
            val inputPin = "${txtOtp1.text}${txtOtp2.text}${txtOtp3.text}${txtOtp4.text}"

            if (storedPin.isNullOrEmpty()) {
                // If no pin is stored, save the input pin
                savePin(inputPin)
                Toast.makeText(this, "PIN saved successfully!", Toast.LENGTH_SHORT).show()
                navigateToNextActivity()
            } else {
                // If a pin is already stored, check if the input matches the stored pin
                if (inputPin == storedPin) {
                    Toast.makeText(this, "PIN verified successfully!", Toast.LENGTH_SHORT).show()
                    navigateToNextActivity()
                } else {
                    Toast.makeText(this, "Incorrect PIN!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun savePin(pin: String) {
        with(sharedPreferences.edit()) {
            putString("PIN_KEY", pin)
            apply()
        }
    }

    private fun navigateToNextActivity() {
        val intent = Intent(this, DashboardActivity::class.java)
        // Clear the back stack and start a new task
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private inner class PinTextWatcher(
        private val current: EditText,
        private val next: EditText?,
        private val previous: EditText? = null
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s.isNullOrEmpty()) return

            if (s.length == 1) {
                // Move to the next field if input length is 1
                next?.requestFocus()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            if (current.text.isEmpty() && previous != null && !previous.hasFocus()) {
                // Move focus back to the previous field if the current field is empty
                previous.requestFocus()
            }

            // Enable submit button only if all fields are filled
            submitButton.isEnabled = txtOtp1.text.isNotEmpty() && txtOtp2.text.isNotEmpty() &&
                    txtOtp3.text.isNotEmpty() && txtOtp4.text.isNotEmpty()
        }
    }
}
