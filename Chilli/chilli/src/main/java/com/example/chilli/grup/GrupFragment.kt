package com.example.chilli.grup

import android.app.AlertDialog
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.broadcash.broadCastAdapter
import com.example.chilli.broadcash.broadcast
import com.example.chilli.broadcash.broadcastGroupAdapter
import com.example.chilli.broadcash.messageDetail.MessageDetailFragment
import com.example.chilli.database.Group
import com.example.chilli.database.Messages
import com.example.chilli.database.User
import com.example.chilli.databinding.FragmentGrupBinding
import com.example.chilli.kalender.eventKalenderAdapter
import com.example.chilli.viewModel.*

class GrupFragment : Fragment(), broadCastAdapter.OnItemClickListener{
    private lateinit var binding: FragmentGrupBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupRecyclerView : RecyclerView
    private lateinit var groupAdapter: broadcastGroupAdapter
    private lateinit var broadCastList: ArrayList<broadcast>
    private lateinit var adapter: broadCastAdapter
    private lateinit var id:String
    private lateinit var application: Application


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        id = (arguments?.getString("idGroup") as? String).toString()
        Log.d("item","$id")

        binding = FragmentGrupBinding.inflate(inflater)

        application = requireNotNull(this.activity).application


        val viewModel = getMessage(this, application).ViewModel
        val viewModel2 = getGroupMessage(this, application).viewModel
        viewModel2.startCollectingData(id)
        Log.d("list ada", "${viewModel2.message}")
        viewModel.getGroupData(viewModel2.message.value)

        viewModel2.message.observe(viewLifecycleOwner){ groupMessage->
            viewModel.getGroupData(groupMessage)
            viewModel.message.observe(viewLifecycleOwner) {
                adapter.notifyDataSetChanged()
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        broadCastList = arrayListOf()
        adapter = broadCastAdapter(viewModel.message)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener2(this)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.button2.setOnClickListener{
            seeQR()
        }

        binding.createMessageButton.setOnClickListener{
            createMessage()
        }

        binding.userButton.setOnClickListener{
            viewUser()
        }

        val groupViewModel = getGroup(this, application).ViewModel
        groupViewModel.isAdmin(id)

        groupViewModel.admin.observe(viewLifecycleOwner) { isAdmin ->
            if (isAdmin) {
                binding.createMessageButton.visibility = View.VISIBLE
            } else {
                binding.createMessageButton.visibility = View.GONE
            }
        }


        binding.buttonLeaveGroup.setOnClickListener{
            showConfirmationDialog()
        }


        return binding.root
    }

    override fun onItemClick(item: Messages) {
        val bundle = Bundle().apply {
            putSerializable("idMessage", item)
        }

        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_grupFragment_to_messageDetailFragment, bundle)
    }

    private fun seeQR(){
        val bundle = Bundle().apply {
            putString("idGroup", id)
        }

        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_grupFragment_to_idGroupFragment, bundle)
    }


    private fun createMessage(){
        val bundle = Bundle().apply {
            putString("idGroup", id)
        }

        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_grupFragment_to_inserMessageFragment, bundle)
    }

    private fun viewUser(){
        val bundle = Bundle().apply {
            putString("idGroup", id)
        }

        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_grupFragment_to_groupDetailFragment, bundle)
    }

    private fun showConfirmationDialog() {
        val dialogView = LayoutInflater.from(this.requireContext()).inflate(R.layout.dialog_confirm, null)

        val dialogTitle = dialogView.findViewById<TextView>(R.id.dialog_title)
        dialogTitle.text = "Confirmation"

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialog_message)
        dialogMessage.text = "Are you sure you want to proceed?"

        val alertDialog = AlertDialog.Builder(this.requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        alertDialog.show()

        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)
        btnCancel.setOnClickListener {
            // Handle cancel button click
            alertDialog.dismiss()
        }

        val btnConfirm = dialogView.findViewById<Button>(R.id.btn_confirm)
        btnConfirm.setOnClickListener {
            leaveGroup()
            alertDialog.dismiss()
        }
    }


    fun leaveGroup() {
        val groupFB = getFirebase().groupCollection
        val userFB = getFirebase().userCollection
        val userId = getFirebase().auth.uid

        try {
            // Get the group data
            groupFB.document(id).get().addOnSuccessListener { groupDoc ->
                val deskripsi = groupDoc?.get("deskripsi") as? String ?: ""
                val foto = groupDoc?.get("foto") as? String ?: ""
                val nama = groupDoc?.get("nama") as? String ?: ""
                var user = groupDoc?.get("user") as? List<Map<String, String>> ?: null
                var groupData = Group(id, deskripsi, foto, nama, user)

                groupData?.user = groupData?.user?.filter { user -> user["userId"] != userId }

                // Update the group document
                if (groupData != null) {
                    groupFB.document(id).set(groupData).addOnSuccessListener {
                        // Get the user data
                        if (userId != null) {
                            userFB.document(userId).get().addOnSuccessListener { userDoc ->
                                val email = userDoc?.get("email") as String
                                val foto = userDoc.get("foto") as String ?: null
                                val group = userDoc.get("group") as List<String>
                                val name = userDoc.get("name") as String
                                val nickName = userDoc.get("nickName") as String
                                var userData = User(userId, email, foto, group, name, nickName)

                                userData?.group = userData?.group?.filter { data -> data != id }

                                // Update the user document
                                if (userData != null) {
                                    userFB.document(userId).set(userData).addOnSuccessListener {
                                        view?.findNavController()?.navigate(R.id.action_grupFragment_to_homeFragment)
                                    }.addOnFailureListener { exception ->
                                        // Handle the failure to update user document
                                        Log.e("leaveGroup", "Failed to update user document: ${exception.message}")
                                    }
                                }
                            }.addOnFailureListener { exception ->
                                // Handle the failure to get user document
                                Log.e("leaveGroup", "Failed to get user document: ${exception.message}")
                            }
                        }
                    }.addOnFailureListener { exception ->
                        // Handle the failure to update group document
                        Log.e("leaveGroup", "Failed to update group document: ${exception.message}")
                    }
                }
            }.addOnFailureListener { exception ->
                // Handle the failure to get group document
                Log.e("leaveGroup", "Failed to get group document: ${exception.message}")
            }
        } catch (e: Exception) {
            // Handle the exception here
            Log.e("leaveGroup", "Exception occurred: ${e.message}")
        }
    }




}