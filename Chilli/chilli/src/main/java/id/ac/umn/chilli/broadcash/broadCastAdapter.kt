package id.ac.umn.chilli.broadcash

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import id.ac.umn.chilli.R
import id.ac.umn.chilli.database.Messages
import id.ac.umn.chilli.databinding.BroadcastCardBinding
import id.ac.umn.chilli.grup.GrupFragment
import java.util.*

class broadCastAdapter(private val broadCashList: MutableLiveData<List<Messages>>) : RecyclerView.Adapter<broadCastAdapter.MyViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: Messages)
    }
    fun setOnItemClickListener(listener: broadcastFragment) {
        itemClickListener = listener
    }

    fun setOnItemClickListener2(listener: GrupFragment) {
        itemClickListener = listener
    }

    lateinit var binding: BroadcastCardBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.broadcast_card,parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val broadcast : Messages? = broadCashList.value?.getOrNull(position)
        if (broadcast != null) {
            holder.title.text = broadcast.title ?: ""
            val cutString = if (broadcast.body?.length!! > 30) broadcast.body?.substring(0, 30)+"..." else broadcast.body
            holder.body.text = cutString

            val calendar = Calendar.getInstance()
            calendar.time = broadcast.timestamp

            holder.year.text = calendar.get(Calendar.YEAR).toString()
            holder.day.text = calendar.get(Calendar.DAY_OF_MONTH).toString()
            holder.month.text = when((calendar.get(Calendar.MONTH) + 1 ).toString()){
                "1" -> "JAN"
                "2" ->  "FEB"
                "3" ->  "MAR"
                "4" ->  "APR"
                "5" ->  "MAY"
                "6" ->  "JUN"
                "7"->  "JUL"
                "8" ->  "AUG"
                "9" ->  "SEP"
                "10" ->  "OCT"
                "11" ->  "NOV"
                else -> "DEC"
            }

            holder.time.text = SimpleDateFormat(
                "HH:mm",
                Locale.getDefault()
            ).format(broadcast.timestamp).toString()
            holder.itemView.setOnClickListener { itemClickListener?.onItemClick(broadcast) }
        }
    }
    override fun getItemCount(): Int = broadCashList.value?.size ?: 0

    fun submitList(list:  List<Messages>){
        broadCashList.value = list
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.title)
        val body: TextView = itemView.findViewById(R.id.body)

        val year: TextView = itemView.findViewById(R.id.year)
        val month: TextView = itemView.findViewById(R.id.month)
        val day: TextView = itemView.findViewById(R.id.day)
        val time: TextView = itemView.findViewById(R.id.time)

    }
}