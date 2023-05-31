package com.example.chilli.home
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
import com.example.chilli.database.AppDatabase
import com.example.chilli.database.Group
import com.example.chilli.database.Messages
import com.example.chilli.databinding.FragmentHomeBinding
import com.example.chilli.viewModel.GrupViewModel
import com.example.chilli.viewModel.GrupViewModelFactory
import com.example.chilli.kalender.eventKalenderAdapter


class homeFragment : Fragment(), GrupAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupAdapter: GrupAdapter
    private lateinit var GroupList: List<Group>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(activity?.layoutInflater!!)

        binding.buttonAddGroup.setOnClickListener{view: View->
            view.findNavController().navigate(R.id.addOrganisasiFragment)}

        binding.buttonSearchGroup.setOnClickListener{view: View->
            view.findNavController().navigate(R.id.searchGroupFragment)}


        val application = requireNotNull(activity).application
        val dataSource = AppDatabase.getInstance(application).groupDao
        val factory = GrupViewModelFactory(dataSource, application)
        val ViewModel = ViewModelProvider(this, factory)[GrupViewModel::class.java]

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        GroupList = arrayListOf()
        groupAdapter = GrupAdapter(ViewModel.Data)
        recyclerView.adapter = groupAdapter
        groupAdapter.setOnItemClickListener(this)

//        binding.lifecycleOwner = this.viewLifecycleOwner

        ViewModel.Data.observe(viewLifecycleOwner){
            groupAdapter.notifyDataSetChanged()
        }



        return binding.root
//        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onItemClick(item: Group) {
//        val bundle = Bundle().apply {
//            putSerializable("idMessage", item)
//        }
//        val detailFragment = MessageDetailFragment()
//        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_grupFragment)
    }
}