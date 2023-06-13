package id.ac.umn.chilli.grup

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.ac.umn.chilli.R
import id.ac.umn.chilli.database.User
import java.util.*

class GrupDetailAdapter(private var userType:List<Map<String, String>>, private var groupList: List<User>?) : RecyclerView.Adapter<GrupDetailAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.group,parent, false)

        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user : User = groupList!![position]
        holder.userName.text = user.name

        val type: String = if (userType.any { it["userId"] == user.userId && it["type"] == "admin" }) {
            "admin"
        } else {
            ""
        }

        holder.type.text = type
        bindImage(holder.foto, user.foto)
    }

    override fun getItemCount(): Int = groupList?.size ?: 0

    public class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val foto:ImageView = itemView.findViewById(R.id.group_image)
        val userName: TextView = itemView.findViewById(R.id.group_title)
        val type: TextView = itemView.findViewById(R.id.group_deskripsi)
    }

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, imgUrl: String?){
        imgUrl?.let{
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imageView.load(imgUri)
        }
    }

    fun updateGroupData(maps: List<Map<String, String>>) {
        userType = maps
    }

    fun updateUserData(userList: MutableList<User>) {
        groupList = userList
    }
}