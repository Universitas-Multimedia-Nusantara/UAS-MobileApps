package com.example.chilli.insertMessage

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.chilli.databinding.FragmentInserMessageBinding
import com.example.chilli.viewModel.Firebase
import com.example.chilli.viewModel.getUser
import com.google.firebase.firestore.FieldValue
import java.text.SimpleDateFormat
import java.util.*

class InserMessageFragment : Fragment() {
    private lateinit var binding: FragmentInserMessageBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private lateinit var selectedTime: String
    private lateinit var selectedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val id = (arguments?.getString("idGroup") as? String).toString()
        Log.d("item","$id")

        binding = FragmentInserMessageBinding.inflate(inflater)

        initDatePicker()
        binding.checkBox.setOnClickListener{
            if(binding.checkBox.isChecked){
                binding.datePickerButton.visibility = View.VISIBLE
                binding.timePickerButton.visibility = View.VISIBLE
            }else{
                binding.datePickerButton.visibility = View.GONE
                binding.timePickerButton.visibility = View.GONE
            }
        }

        binding.datePickerButton.text = getTodayDate()
        binding.timePickerButton.text = getNowTime()
        binding.datePickerButton.setOnClickListener{
            datePickerDialog.show()
        }

        binding.timePickerButton.setOnClickListener{
            timePickerDialog.show()
        }

        binding.buttonSubmit.setOnClickListener{
            insertMessage(id)
        }

        return binding.root
    }

    private fun getTodayDate():String{
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        selectedDate = "$year-${month+1}-$day"
        return makeDateString(day, month + 1, year)
    }

    private fun getNowTime():String{
        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        selectedTime = makeTimeString(hour, minute)
        return selectedTime
    }

    private fun initDatePicker(){
        val dateSetListener = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            val date = makeDateString(day, month + 1, year)
            binding.datePickerButton.text = date
            selectedDate = "$year-${month+1}-$day"
        }

        val cal: Calendar = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_MONTH, 0)
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)

        val style: Int = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, year, month, day)
        datePickerDialog.datePicker.minDate = cal.timeInMillis

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker: TimePicker, hourOfDay: Int, minute: Int ->
            selectedTime= makeTimeString(hourOfDay, minute)
            binding.timePickerButton.text = selectedTime
        }

        val currentTime = Calendar.getInstance()
        val hour = currentTime.get(Calendar.HOUR_OF_DAY)
        val minute = currentTime.get(Calendar.MINUTE)
        timePickerDialog = TimePickerDialog(requireContext(), timeSetListener, hour, minute, false)
    }

    private fun makeTimeString(hour: Int, minute: Int): String {
        val formattedHour = if (hour < 10) "0$hour" else hour.toString()
        val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
        return "$formattedHour:$formattedMinute"
    }


    private fun makeDateString(day: Int, month: Int, year: Int): String{
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int):String{
        when(month){
            1 -> return "JAN"
            2 -> return "FEB"
            3 -> return "MAR"
            4 -> return "APR"
            5 -> return "MAY"
            6 -> return "JUN"
            7 -> return "JUL"
            8 -> return "AUG"
            9 -> return "SEP"
            10 -> return "OCT"
            11 -> return "NOV"
            else-> return "DEC"
        }
    }


    private fun insertMessage(id:String){
        val dateTimeString = "$selectedDate $selectedTime"
        val dateFormated = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(dateTimeString)
        val data = hashMapOf(
            "sender" to getUser(this,  requireNotNull(activity).application).userViewModel.name.toString(),
            "timestamp" to FieldValue.serverTimestamp(),
            "title" to binding.inputTitleText.text.toString(),
            "body" to binding.inputBodyText.text.toString(),
            "pinTime" to dateFormated
        )
        Firebase().messagesCollection.document(id).collection("Messages").document().set(data).addOnSuccessListener{

        }
    }
}