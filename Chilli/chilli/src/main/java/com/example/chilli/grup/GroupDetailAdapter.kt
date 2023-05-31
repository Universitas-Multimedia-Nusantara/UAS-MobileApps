//package com.example.chilli.grup
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.lifecycle.MutableLiveData
//import androidx.recyclerview.widget.RecyclerView
//import com.example.chilli.R
//import com.example.chilli.database.Group
//import com.example.chilli.database.Messages
//import com.example.chilli.databinding.BroadcastCardBinding
//import java.util.*
//
//class GrupDetailAdapter(private val groupList: MutableLiveData<List<Group>>) : RecyclerView.Adapter<GrupDetailAdapter.MyViewHolder>() {
//    lateinit var binding: BroadcastCardBinding
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): GrupDetailAdapter.MyViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.broadcast_card,parent, false)
//
//        return MyViewHolder(itemView)
//    }
//
//
//    override fun onBindViewHolder(holder: GrupDetailAdapter.MyViewHolder, position: Int) {
//        val group : Group = groupList.value!![position]
//        holder.foto = group.foto
//        holder.nama = group.nama
//        holder.deskripsi = group.deskripsi
//    }
//
//    override fun getItemCount(): Int = groupList.value?.size ?: 0
//
//    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
//        val title: TextView = itemView.findViewById(R.id.title)
//        val body: TextView = itemView.findViewById(R.id.body)
//        val time: TextView = itemView.findViewById(R.id.time)
//    }
//}