package com.example.chilli.broadcash

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.database.Messages
import java.text.SimpleDateFormat
import com.example.chilli.databinding.BroadcastCardBinding
import com.example.chilli.grup.grupFragment
import com.example.chilli.kalender.eventKalenderAdapter
import java.util.*

class broadCastAdapter(private val broadCashList: MutableLiveData<List<Messages>>) : RecyclerView.Adapter<broadCastAdapter.MyViewHolder>() {

    private var itemClickListener: eventKalenderAdapter.OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: Messages)
    }
    fun setOnItemClickListener(listener: broadcastFragment) {
        itemClickListener = listener
    }

    fun setOnItemClickListener2(listener: grupFragment) {
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
        val broadcast : Messages = broadCashList.value!![position]
        holder.title.text = broadcast.title
        holder.body.text = broadcast.body
        holder.time.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(broadcast.timestamp).toString()

        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(broadcast) }
    }

    override fun getItemCount(): Int = broadCashList.value?.size ?: 0

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.title)
        val body: TextView = itemView.findViewById(R.id.body)
        val time: TextView = itemView.findViewById(R.id.time)
    }

}