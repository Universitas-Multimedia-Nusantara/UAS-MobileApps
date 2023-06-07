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
import com.example.chilli.grup.GrupFragment
import java.util.*

class broadCastAdapter(private val broadCashList: MutableLiveData<List<Messages>>) : RecyclerView.Adapter<broadCastAdapter.MyViewHolder>() {

    private var itemClickListener: broadCastAdapter.OnItemClickListener? = null

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
            holder.time.text = SimpleDateFormat(
                "dd MMMM yyyy HH:mm",
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
        val time: TextView = itemView.findViewById(R.id.time)
    }
}