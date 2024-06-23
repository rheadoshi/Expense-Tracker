package com.example.expensetracker

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginPin.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginPin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var views:View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        views = inflater.inflate(R.layout.fragment_login_pin, container, false)
        return views
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val _activity = activity as MainActivity

        val button = views.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val buttons1 = arrayOf<String?>(views.findViewById<EditText>(R.id.txt_otp1).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp2).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp3).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp4).text.toString())
            if (_activity.checkPin(buttons1)) {
                val intent = Intent(activity, HomePage::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(_activity, "Please enter your correct pin!", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginPin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginPin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}