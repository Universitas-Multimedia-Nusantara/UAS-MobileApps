package com.example.chilli.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.chilli.R
import com.example.chilli.databinding.FragmentHomeBinding


class homeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(activity?.layoutInflater!!)

        binding.buttonAddGroup.setOnClickListener{view: View->
            view.findNavController().navigate(R.id.addOrganisasiFragment)}

        binding.buttonSearchGroup.setOnClickListener{view: View->
            view.findNavController().navigate(R.id.searchGroupFragment)}

        return binding.root



//        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}