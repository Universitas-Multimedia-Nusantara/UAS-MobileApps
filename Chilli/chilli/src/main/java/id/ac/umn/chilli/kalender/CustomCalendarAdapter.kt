package id.ac.umn.chilli.kalender

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.ac.umn.chilli.R
import kotlin.collections.ArrayList

class CustomCalendarAdapter(private val daysOftheMonth: ArrayList<String>, private val highlightedDates: List<String>, val date: String) :
    RecyclerView.Adapter<CustomCalendarAdapter.MyViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null
    private var lastClickedPosition: Int? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int, day: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.calendar_cell, parent, false)
        itemView.layoutParams.height = ((parent.height * 0.16666666).toInt())
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val day = daysOftheMonth[position]
        holder.daysOfMonth.text = day

        val fullDate = "$date-${day.toString().padStart(2, '0')}"

        val formattedHighlightedDates = highlightedDates?.map { it?.padStart(10, '0') }
        if (formattedHighlightedDates?.contains(fullDate) == true) {
            Log.d("ture", "true")
            holder.itemView.setBackgroundColor(R.drawable.custom_circle_background)
            if (position == lastClickedPosition) {
                holder.itemView.setBackgroundColor(Color.RED)
            }
        } else {
            if (position == lastClickedPosition) {
                holder.itemView.setBackgroundColor(Color.RED)
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }



        holder.itemView.setOnClickListener {
            val adapterPosition = holder.adapterPosition
            if (adapterPosition != RecyclerView.NO_POSITION && !day.equals("")) {
                val clickedDay = daysOftheMonth[adapterPosition]
                lastClickedPosition = adapterPosition
                notifyDataSetChanged()
                itemClickListener?.onItemClick(adapterPosition, clickedDay)
            }
        }

    }

    override fun getItemCount(): Int {
        return daysOftheMonth.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var daysOfMonth: TextView = itemView.findViewById(R.id.cellDayText)
    }
}

