package com.example.chilli.broadcash;

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.databinding.FragmentBroadcastBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch


public class broadcastFragment : Fragment() {
    private lateinit var binding: FragmentBroadcastBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupRecyclerView : RecyclerView
    private lateinit var groupAdapter: broadcastGroupAdapter
    private lateinit var broadCastList: ArrayList<broadcast>
    private lateinit var adapter: broadCastAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBroadcastBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerView2
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        broadCastList = arrayListOf()
        adapter = broadCastAdapter(broadCastList)
        recyclerView.adapter = adapter

        val listGroup = arrayListOf("grup1", "grup2", "grup3")

        groupRecyclerView = binding.recyclerView
        groupAdapter = broadcastGroupAdapter(listGroup)
        groupRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        groupRecyclerView.adapter = groupAdapter

        eventChangeListener()

        return binding.root
    }

    private fun eventChangeListener() {
        val auth = FirebaseAuth.getInstance()
        val database = FirebaseFirestore.getInstance()
        val databaseRef = database.collection("User")
        val user = auth.currentUser
        val userRef = databaseRef.document(user?.uid!!)

        lifecycleScope.launch {
            try {
                userRef.get().addOnSuccessListener { userSnapshot ->
                    val groupIDs = userSnapshot.get("group") as? List<String>
                    groupIDs?.let {
                        for (groupID in groupIDs) {
                            val groupRef = FirebaseFirestore.getInstance().collection("BroadcastMessages").document(groupID)
                            val messagesRef = groupRef.collection("Messages")
                            messagesRef.get().addOnSuccessListener { broadcastSnapshot ->
                                for (broadcastDocument in broadcastSnapshot.documents) {
                                    val title = broadcastDocument.getString("title")
                                    val body = broadcastDocument.getString("body")
                                    val time = broadcastDocument.getTimestamp("time")

                                    broadCastList.add(broadcast(title, body, time))
                                }
                                adapter.notifyDataSetChanged()
                            }.addOnFailureListener { exception ->
                                Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(activity, "Error retrieving data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
