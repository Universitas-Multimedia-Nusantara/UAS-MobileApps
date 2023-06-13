package id.ac.umn.chilli.kalender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import id.ac.umn.chilli.R
import id.ac.umn.chilli.database.Messages
import java.text.SimpleDateFormat
import java.util.*

class eventKalenderAdapter(private var eventList: List<Messages>?) :
    RecyclerView.Adapter<eventKalenderAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: Messages)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.calender_event, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event: Messages = eventList?.get(position)!!
        holder.title.text = event.title
        val parsedDate = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(event.pinTime)


        holder.time.text =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(parsedDate)?.toString()

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(event)
        }
    }

    override fun getItemCount(): Int {
        return eventList?.size ?: 0
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView = itemView.findViewById(R.id.time)
        var title: TextView = itemView.findViewById(R.id.title)
    }

    fun setData(data: List<Messages>) {
        eventList = data
    }
}
