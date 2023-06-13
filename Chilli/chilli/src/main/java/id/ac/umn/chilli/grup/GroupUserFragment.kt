package id.ac.umn.chilli.grup

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import id.ac.umn.chilli.database.Group
import id.ac.umn.chilli.database.User
import id.ac.umn.chilli.databinding.FragmentGroupUserBinding
import id.ac.umn.chilli.viewModel.getFirebase
import id.ac.umn.chilli.viewModel.getGroup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GroupDetailFragment : Fragment() {

    private lateinit var binding: FragmentGroupUserBinding
    private lateinit var id: String
    private lateinit var application: Application
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GrupDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        id = arguments?.getString("idGroup").toString()
        Log.d("id", "$id")

        binding = FragmentGroupUserBinding.inflate(inflater)

        application = requireNotNull(this.activity).application
        val groupViewModel = getGroup(this, application).ViewModel.singleData(id)
        binding.groupDesc.text = groupViewModel?.deskripsi.toString()
        binding.groupName.text = groupViewModel?.nama.toString()
        bindImage(binding.groupImage, groupViewModel?.foto)

        recyclerView = binding.recirclerUser
        recyclerView.layoutManager = LinearLayoutManager(activity)
        groupAdapter = GrupDetailAdapter(emptyList(), emptyList())
        recyclerView.adapter = groupAdapter

        fetchUser()

        return binding.root
    }

    @BindingAdapter("imageUrl")
    fun bindImage(imageView: ImageView, imgUrl: String?) {
        imgUrl?.let {
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
            imageView.load(imgUri)
        }
    }

    private fun fetchUser() {
        val userList: MutableList<User> = mutableListOf()

        lifecycleScope.launch {
            try {
                val group = withContext(Dispatchers.IO) {
                    getFirebase().groupCollection.document(id).get().await()
                }

                val deskripsi = group?.get("deskripsi") as? String ?: ""
                val foto = group?.get("foto") as? String ?: ""
                val nama = group?.get("nama") as? String ?: ""
                val user = group?.get("user") as? List<Map<String, String>> ?: emptyList()
                val groupData = Group(id, deskripsi, foto, nama, user)

                val userIds = groupData?.user?.mapNotNull { it["userId"] } ?: emptyList()

                val users = withContext(Dispatchers.IO) {
                    getFirebase().userCollection.whereIn("userId", userIds).get().await()
                }

                for (userDoc in users) {
                    val userId = userDoc?.get("userId") as String
                    val email = userDoc?.get("email") as String
                    val foto = userDoc.get("foto") as String?
                    val group = userDoc.get("group") as List<String>
                    val name = userDoc.get("name") as String
                    val nickName = userDoc.get("nickName") as String

                    val userData = User(userId, email, foto, group, name, nickName)

                    userList.add(userData)
                    Log.d("User", "$userData")
                }

                groupAdapter.updateGroupData(groupData.user ?: emptyList())
                groupAdapter.updateUserData(userList)
            } catch (exception: Exception) {
                // Handle exceptions
                Log.e("fetchUser", "Failed to fetch user data: ${exception.message}")
            }
        }
    }

}
