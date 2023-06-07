package com.example.chilli.broadcash



import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chilli.R
import com.example.chilli.broadcash.messageDetail.MessageDetailFragment
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Messages
import com.example.chilli.databinding.FragmentBroadcastBinding
import com.example.chilli.viewModel.MessageViewModel
import com.example.chilli.viewModel.MessageViewModelFactory
import com.example.chilli.viewModel.getGroup
import com.example.chilli.viewModel.getGroupMessage

class broadcastFragment : Fragment(), broadCastAdapter.OnItemClickListener,
    broadcastGroupAdapter.OnItemClickListener {
    private lateinit var binding: FragmentBroadcastBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupRecyclerView : RecyclerView
    private lateinit var groupAdapter: broadcastGroupAdapter
    private lateinit var broadCastList: ArrayList<broadcast>
    private lateinit var adapter: broadCastAdapter
    private lateinit var listMessage: MutableLiveData<List<Messages>>
    private lateinit var application: Application
    private var isFiltered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        application = requireNotNull(this.activity).application
        val groupViewModel = getGroup(this, application).ViewModel
        groupViewModel.syncGroup()
        val listGroup = groupViewModel.Data.value

        groupRecyclerView = binding.recyclerView
        groupAdapter = broadcastGroupAdapter(listGroup)
        groupRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        groupRecyclerView.adapter = groupAdapter


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
        groupAdapter.setOnItemClickListener(this)


        ViewModel.message.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        binding.lifecycleOwner = this.viewLifecycleOwner
        listMessage = ViewModel.message

        binding.inputTitleText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterSearchResults(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                filterSearchResults(s.toString())
            }
        })


        return binding.root
    }

    private fun filterSearchResults(query: String) {
        val filteredList = if (query.isNullOrBlank() || query == " ") {
            listMessage.value
        } else {
            listMessage.value?.filter { result ->
                result.title.contains(query) ||
                        result.body?.contains(query) == true ||
                        result.sender?.contains(query) == true
            }
        }
        if (filteredList != null) {
            adapter.submitList(filteredList)
        }
    }


    override fun onItemClick(item: Messages) {
        val bundle = Bundle().apply {
            putSerializable("idMessage", item)
        }
        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_broadcastFragment_to_messageDetailFragment, bundle)
    }

    override fun onItemClick(groupId: String?) {
        val message = getGroupMessage(this, application ).viewModel
        if(groupId != null) message.startCollectingData(groupId)
        val query = message.message.value

        val filteredList = listMessage.value?.filter { result ->
            query?.contains( result.messageId) == true
        }

        if (filteredList != null) {
            adapter.submitList(filteredList)
        }
    }


}
