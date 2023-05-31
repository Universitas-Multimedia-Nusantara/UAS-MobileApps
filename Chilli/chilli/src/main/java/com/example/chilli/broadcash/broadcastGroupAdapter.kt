package com.example.chilli.broadcash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.kalender.eventKalenderAdapter

class broadcastGroupAdapter(private val groupList: ArrayList<String>) : RecyclerView.Adapter<broadcastGroupAdapter.MyViewHolder>() {

    private var itemClickListener: eventKalenderAdapter.OnItemClickListener? = null

    fun setOnItemClickListener(listener: eventKalenderAdapter.OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.broadcast_group, parent, false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val group : String = groupList[position]
        holder.groupName.text = group.toString()
    }

    override fun getItemCount(): Int {
        return groupList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
       val groupName:TextView =  itemView.findViewById(R.id.group_name)
    }
}