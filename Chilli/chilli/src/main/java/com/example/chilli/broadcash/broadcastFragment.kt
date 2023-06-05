package com.example.chilli.broadcash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.broadcash.messageDetail.MessageDetailFragment
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Messages
import com.example.chilli.databinding.FragmentBroadcastBinding
import com.example.chilli.kalender.eventKalenderAdapter
import com.example.chilli.viewModel.MessageViewModel
import com.example.chilli.viewModel.MessageViewModelFactory

class broadcastFragment : Fragment(), broadCastAdapter.OnItemClickListener,
    eventKalenderAdapter.OnItemClickListener {
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

        val listGroup = arrayListOf("grup1", "grup2", "grup3")

        groupRecyclerView = binding.recyclerView
        groupAdapter = broadcastGroupAdapter(listGroup)
        groupRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        groupRecyclerView.adapter = groupAdapter


        val application = requireNotNull(this.activity).application
        val message = AppDatabase.getInstance(application).messagesDao
        val factory = MessageViewModelFactory(message, application)
        val ViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]
        ViewModel.startCollectingData()

        recyclerView = binding.recyclerView2
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        broadCastList = arrayListOf()
        adapter = broadCastAdapter(ViewModel.message)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(this)


        ViewModel.message.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        binding.lifecycleOwner = this.viewLifecycleOwner
        return binding.root
    }


    override fun onItemClick(item: Messages) {
        val bundle = Bundle().apply {
            putSerializable("idMessage", item)
        }
        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_broadcastFragment_to_messageDetailFragment, bundle)
    }


}
