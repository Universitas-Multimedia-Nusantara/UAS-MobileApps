package id.ac.umn.chilli.home
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.ac.umn.chilli.R
import id.ac.umn.chilli.broadcash.messageDetail.MessageDetailFragment
import id.ac.umn.chilli.database.Group
import id.ac.umn.chilli.databinding.FragmentHomeBinding
import id.ac.umn.chilli.viewModel.getGroup
import id.ac.umn.chilli.viewModel.getUser


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
        val ViewModel = getGroup(this, application).ViewModel
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

        val user = getUser(this, application).userViewModel.name.value
        binding.textView3.text = "Hello "+user



        return binding.root
//        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onItemClick(item: String) {
        val bundle = Bundle().apply {
            putString("idGroup", item)
        }

        val detailFragment = MessageDetailFragment()
        detailFragment.arguments = bundle
        view?.findNavController()?.navigate(R.id.action_homeFragment_to_grupFragment, bundle)
    }
}