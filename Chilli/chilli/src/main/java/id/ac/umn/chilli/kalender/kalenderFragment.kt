package id.ac.umn.chilli.kalender

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.umn.chilli.R
import id.ac.umn.chilli.broadcash.messageDetail.MessageDetailFragment
import id.ac.umn.chilli.database.AppDatabase
import id.ac.umn.chilli.database.Messages
import id.ac.umn.chilli.databinding.FragmentKalenderBinding
import id.ac.umn.chilli.viewModel.MessageViewModel
import id.ac.umn.chilli.viewModel.MessageViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class kalenderFragment : Fragment(), eventKalenderAdapter.OnItemClickListener, CustomCalendarAdapter.OnItemClickListener{
    private lateinit var binding: FragmentKalenderBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: eventKalenderAdapter
    private var data: MutableLiveData<List<Messages>> = MutableLiveData()
    private lateinit var today: String
    private lateinit var selectedDate: LocalDate
    private lateinit var calendarAdapter: CustomCalendarAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentKalenderBinding.inflate(inflater)
        today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val application = requireNotNull(this.activity).application
        val message = AppDatabase.getInstance(application).messagesDao
        val factory = MessageViewModelFactory(message, application)
        val viewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        adapter = eventKalenderAdapter(emptyList())
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(this)

        binding.lifecycleOwner = this.viewLifecycleOwner

        viewModel.startCollectingData()

        viewModel.message.observe(viewLifecycleOwner) { data ->
            filterDataByDate(data, today)
            setMonthView()
            calendarAdapter.notifyDataSetChanged()
        }

//        val datePin = data.value?.map { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it.pinTime).toString() }
//        binding.calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
//            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
//                GregorianCalendar(year, month, dayOfMonth).time
//            )
//
//            today = selectedDate
//            filterDataByDate(viewModel.message.value ?: emptyList(), selectedDate)
//        }

//        if (datePin != null) {
//            binding.calendarView.setColoredDates(datePin)
//        }

        selectedDate  = LocalDate.now()
        binding.nextMonth.setOnClickListener{
            view?.let { it1 -> nextMonthAct(it1) }
        }

        binding.prevMonth.setOnClickListener{
            view?.let { it1 -> prevMontAct(it1) }
        }

        setMonthView()
        return binding.root
    }


    private fun setMonthView(){
        val Nowdate = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate.toString())
        )

        val viewModel = getViewModel()

        var pin = viewModel.message.value?.map{
            it.pinTime.toString()
        }

        val pinTime = pin?.mapNotNull {
            try {
                val pinDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(pinDateTime)
            } catch (e: ParseException) {
                null
            }
        } ?: emptyList()

//        val pinTime = pin?.map{
//            val pinDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(it)
//            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(pinDateTime)
//        }


        Log.d("pin", "$pinTime")

        binding.monthYearTv.text = monthYearFromDate(selectedDate)
        val dayInMonth = daysInMonthArray(selectedDate)
        calendarAdapter = CustomCalendarAdapter(ArrayList(dayInMonth),
            pinTime, Nowdate
        )
        val layoutManager = GridLayoutManager(requireNotNull(this.activity).application,7)
        binding.calendarView.layoutManager = layoutManager
        binding.calendarView.adapter = calendarAdapter
        calendarAdapter.setOnItemClickListener(this)
    }

    private fun daysInMonthArray(date: LocalDate): MutableList<String> {
        var daysInMonthArray = mutableListOf<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstMonth.dayOfWeek.value
        for(i in 1..42){
            if(i <= dayOfWeek || i > daysInMonth + dayOfWeek){
                daysInMonthArray.add("")
            }else{
                val daysOfWeekResult: Int = dayOfWeek
                daysInMonthArray.add(((i - daysOfWeekResult)).toString())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(Date: LocalDate):String{
        val format = DateTimeFormatter.ofPattern("MMMM yyyy")
        return Date.format(format)
    }

    private fun filterDataByDate(messages: List<Messages>, selectedDate: String) {
        val filteredData = messages.filter {
            if (it.pinTime != "") {
                val formattedPinTime = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.pinTime).toString()
                val selectedDateFormated = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate).toString()
                formattedPinTime == selectedDateFormated

            } else {
                false
            }
        }

        data.value = filteredData
        adapter.setData(filteredData)
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(item: Messages) {
        val bundle = Bundle().apply {
            putSerializable("idMessage", item)
        }
        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_kalenderFragment_to_messageDetailFragment, bundle)
    }

     private fun prevMontAct(view: View){
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
    }

     private fun nextMonthAct(view: View){
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
    }

    private fun getViewModel(): MessageViewModel {
        val application = requireNotNull(this.activity).application
        val message = AppDatabase.getInstance(application).messagesDao
        val factory = MessageViewModelFactory(message, application)
        return ViewModelProvider(this, factory)[MessageViewModel::class.java]
    }

    override fun onItemClick(position: Int, day: String) {
        val viewModel = getViewModel()
        if(day != "" && day != "00"){
//            val message = "Selected Date" + day + "" + monthYearFromDate(selectedDate)
//            Toast.makeText(this.activity, message, Toast.LENGTH_LONG).show()

            var date = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(selectedDate.toString())
            )
            date = "$date-$day"

            today = date
            filterDataByDate(viewModel.message.value ?: emptyList(), date)
        }
    }
}
