package com.example.chilli.grup

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
import com.example.chilli.broadcash.broadCastAdapter
import com.example.chilli.broadcash.broadcast
import com.example.chilli.broadcash.broadcastGroupAdapter
import com.example.chilli.broadcash.messageDetail.MessageDetailFragment
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Messages
import com.example.chilli.databinding.FragmentGrupBinding
import com.example.chilli.kalender.eventKalenderAdapter
import com.example.chilli.viewModel.MessageViewModel
import com.example.chilli.viewModel.MessageViewModelFactory


class grupFragment : Fragment(), broadCastAdapter.OnItemClickListener,
    eventKalenderAdapter.OnItemClickListener {
    private lateinit var binding: FragmentGrupBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupRecyclerView : RecyclerView
    private lateinit var groupAdapter: broadcastGroupAdapter
    private lateinit var broadCastList: ArrayList<broadcast>
    private lateinit var adapter: broadCastAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentGrupBinding.inflate(inflater)

        val application = requireNotNull(this.activity).application
        val message = AppDatabase.getInstance(application).messagesDao
        val factory = MessageViewModelFactory(message, application)
        val ViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        broadCastList = arrayListOf()
        adapter = broadCastAdapter(ViewModel.message)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener2(this)

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
        view?.findNavController()?.navigate(R.id.action_grupFragment_to_messageDetailFragment, bundle)
    }
}