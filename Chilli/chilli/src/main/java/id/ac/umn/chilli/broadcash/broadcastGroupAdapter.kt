package id.ac.umn.chilli.broadcash

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData

import androidx.recyclerview.widget.RecyclerView

import id.ac.umn.chilli.R
import id.ac.umn.chilli.database.Group

class broadcastGroupAdapter(private val groupList: MutableLiveData<List<Group>>?) : RecyclerView.Adapter<broadcastGroupAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: String?)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: broadcastFragment) {
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
        val group : Group = groupList?.value!![position]
        holder.groupName.text = group.nama.toString()
        holder.itemView.setOnClickListener{ itemClickListener?.onItemClick(group.groupId.toString()) }
    }

    override fun getItemCount(): Int {
        return groupList?.value?.size ?: 0
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
       val groupName:TextView =  itemView.findViewById(R.id.group_name)
    }
}