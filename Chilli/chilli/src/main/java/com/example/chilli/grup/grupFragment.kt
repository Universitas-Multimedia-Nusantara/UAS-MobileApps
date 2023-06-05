package com.example.chilli.grup

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.broadcash.broadCastAdapter
import com.example.chilli.broadcash.broadcast
import com.example.chilli.broadcash.broadcastGroupAdapter
import com.example.chilli.broadcash.messageDetail.MessageDetailFragment
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Messages
import com.example.chilli.databinding.FragmentGrupBinding
import com.example.chilli.kalender.eventKalenderAdapter
import com.example.chilli.viewModel.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class grupFragment : Fragment(), broadCastAdapter.OnItemClickListener,
    eventKalenderAdapter.OnItemClickListener {
    private lateinit var binding: FragmentGrupBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupRecyclerView : RecyclerView
    private lateinit var groupAdapter: broadcastGroupAdapter
    private lateinit var broadCastList: ArrayList<broadcast>
    private lateinit var adapter: broadCastAdapter
    private lateinit var id:String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        id = (arguments?.getString("idGroup") as? String).toString()
        Log.d("item","$id")

        binding = FragmentGrupBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application


        val ViewModel = getMessage(this, application).ViewModel
        val ViewModel2 = getGroupMessage(this, application).viewModel
        ViewModel2.startCollectingData(id)
        Log.d("list ada", "${ViewModel2.message}")
        ViewModel.getGroupData(ViewModel2.message.value)

        ViewModel2.message.observe(viewLifecycleOwner){groupMessage->
            ViewModel.getGroupData(groupMessage)
            ViewModel.message.observe(viewLifecycleOwner) {
                adapter.notifyDataSetChanged()
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        broadCastList = arrayListOf()
        adapter = broadCastAdapter(ViewModel.message)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener2(this)

        binding.lifecycleOwner = this.viewLifecycleOwner

        binding.button2.setOnClickListener{
            seeQR()
        }

        binding.createMessageButton.setOnClickListener{
            createMessage()
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
}