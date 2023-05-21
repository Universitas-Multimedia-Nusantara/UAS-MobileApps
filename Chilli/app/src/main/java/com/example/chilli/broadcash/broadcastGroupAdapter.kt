package com.example.chilli.broadcash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R

class broadcastGroupAdapter(private val groupList: ArrayList<String>) : RecyclerView.Adapter<broadcastGroupAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): broadcastGroupAdapter.MyViewHolder {
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

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
       val groupName:TextView =  itemView.findViewById(R.id.group_name)
    }
}