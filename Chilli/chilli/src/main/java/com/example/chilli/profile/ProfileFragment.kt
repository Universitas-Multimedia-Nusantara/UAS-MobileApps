package com.example.chilli.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.chilli.LoginActivity
import com.example.chilli.viewModel.UserViewModel
import com.example.chilli.viewModel.UserViewModelFactory
import com.example.chilli.database.AppDatabase
import com.example.chilli.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore




class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    var database: FirebaseFirestore? = null
    var databaseRef: CollectionReference? = null
    private lateinit var binding: FragmentProfileBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater)

        auth = FirebaseAuth.getInstance()

        val application = requireNotNull(activity).application
        val dataSource = AppDatabase.getInstance(application).userDao
        val factory = UserViewModelFactory(auth.currentUser?.uid!!,dataSource, application)
        val userViewModel = ViewModelProvider(this, factory)[UserViewModel::class.java]

        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.userViewModel = userViewModel

        loadProfile()
        return binding.root
    }

    private fun loadProfile(){
        binding.logoutButton.setOnClickListener{
            auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
        }
    }


}