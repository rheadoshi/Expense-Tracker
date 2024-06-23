package com.example.expensetracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
 * Use the [SignUpPin.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpPin : Fragment() {
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
        views = inflater.inflate(R.layout.fragment_sign_up_pin, container, false)
        return views
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = views.findViewById<Button>(R.id.button)

        button.setOnClickListener {
            val _activity = activity as MainActivity
            val buttons1 = arrayOf<String?>(views.findViewById<EditText>(R.id.txt_otp1).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp2).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp3).text.toString(),
                views.findViewById<EditText>(R.id.txt_otp4).text.toString())
            val buttons2 = arrayOf<String?>(views.findViewById<EditText>(R.id.txt_otp11).text.toString(),
            views.findViewById<EditText>(R.id.txt_otp21).text.toString(),
            views.findViewById<EditText>(R.id.txt_otp31).text.toString(),
            views.findViewById<EditText>(R.id.txt_otp41).text.toString())
            if(_activity.checkPin(buttons1,buttons2)){
                _activity.addData("Pin1",buttons1[0])
                _activity.addData("Pin2",buttons1[1])
                _activity.addData("Pin3",buttons1[2])
                _activity.addData("Pin4",buttons1[3])
                val intent = Intent(activity, HomePage::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(_activity,"The pin does not match",Toast.LENGTH_SHORT).show()
            }
        }
//        button.setOnClickListener {
//            val _activity = activity as MainActivity
//            _activity.setCurrView(SignUpPin())
//        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpPin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpPin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}