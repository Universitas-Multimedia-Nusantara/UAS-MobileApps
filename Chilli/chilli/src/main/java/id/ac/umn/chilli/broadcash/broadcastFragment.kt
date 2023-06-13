package id.ac.umn.chilli.broadcash



import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.umn.chilli.R
import id.ac.umn.chilli.broadcash.messageDetail.MessageDetailFragment

import id.ac.umn.chilli.database.AppDatabase
import id.ac.umn.chilli.database.Messages
import id.ac.umn.chilli.databinding.FragmentBroadcastBinding
import id.ac.umn.chilli.viewModel.MessageViewModel
import id.ac.umn.chilli.viewModel.MessageViewModelFactory
import id.ac.umn.chilli.viewModel.getGroup
import id.ac.umn.chilli.viewModel.getGroupMessage
import kotlinx.coroutines.launch

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
    private lateinit var ViewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBroadcastBinding.inflate(inflater, container, false)
        application = requireNotNull(this.activity).application
        val groupViewModel = getGroup(this, application).ViewModel
        groupViewModel.syncGroup()


        groupRecyclerView = binding.recyclerView
        groupAdapter = broadcastGroupAdapter(groupViewModel.Data)
        groupRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        groupRecyclerView.adapter = groupAdapter

        groupViewModel.Data.observe(viewLifecycleOwner){
            groupAdapter.notifyDataSetChanged()
        }


        val message = AppDatabase.getInstance(application).messagesDao
        val factory = MessageViewModelFactory(message, application)
        ViewModel = ViewModelProvider(this, factory)[MessageViewModel::class.java]
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
        lifecycleScope.launch {
            val message = activity?.let { getGroupMessage(it, application).viewModel }
            if (groupId != null) {
                message?.startCollectingData(groupId)
            }

            var query: List<String>? = emptyList()
            lifecycleScope.launch {
                query = message?.message?.value
                Log.d("Query", "$query")
            }

            lifecycleScope.launch {

                    val filteredList = listMessage.value?.filter { result ->
                        query?.contains(result.messageId) == true
                    }
                    Log.d("filtered", "$filteredList")


                    if (filteredList != null) {
                        adapter.submitList(filteredList)
                    }

            }
        }

    }
}
