package com.example.chilli.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.chilli.R
import com.example.chilli.database.Group
import com.example.chilli.kalender.eventKalenderAdapter
import java.util.*

class GrupAdapter(private val groupList: MutableLiveData<List<Group>>) : RecyclerView.Adapter<GrupAdapter.MyViewHolder>() {

    private var itemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(item: String)
    }

    fun setOnItemClickListener(listener: homeFragment) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group,parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val group : Group = groupList.value!![position]
        holder.groupName.text = group.nama
        holder.groupDesc.text = group.deskripsi
        bindImage(holder.image, group.foto)

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(group.groupId)
        }
    }

    override fun getItemCount(): Int = groupList.value?.size ?: 0

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var image: ImageView = itemView.findViewById(R.id.group_image)
        var groupName: TextView = itemView.findViewById(R.id.group_title)
        var groupDesc: TextView = itemView.findViewById(R.id.group_deskripsi)
    }

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, imgUrl: String?){
        imgUrl?.let{
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imageView.load(imgUri)
        }
    }
}