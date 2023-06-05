package com.example.chilli.addOrganisasi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chilli.R
import com.example.chilli.databinding.FragmentIdGroupBinding


class IdGroupFragment : Fragment() {
    private lateinit var binding: FragmentIdGroupBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val id = (arguments?.getString("idGroup") as? String).toString()
        Log.d("item","$id")

        binding = FragmentIdGroupBinding.inflate(inflater)

        val qrCodeBitmap = generateQR(id, 500, 500)

        binding.QrView.setImageBitmap(qrCodeBitmap)
        binding.idView.text = id
        return binding.root
    }

}