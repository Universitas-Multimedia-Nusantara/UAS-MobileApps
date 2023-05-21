package com.example.chilli.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chilli.LoginActivity
import com.example.chilli.R
import com.example.chilli.databinding.ActivityMainBinding
import com.example.chilli.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [profileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class profileFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    var database: FirebaseFirestore? = null
    var databaseRef: CollectionReference? = null
    lateinit var binding: FragmentProfileBinding

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        databaseRef = database?.collection("User")

        loadProfile()
        return binding.root
    }

    private fun loadProfile(){

        val user = auth.currentUser
        val userRef = databaseRef?.document(user?.uid!!)

        userRef?.get()?.addOnSuccessListener{documentSnapshot->
            if (documentSnapshot.exists()) {
                binding.namaLengkap.text = documentSnapshot.getString("name")
                binding.namaPanggilan.text = documentSnapshot.getString("nickName")
                binding.email.text = documentSnapshot.getString("email")
            } else {
                Toast.makeText(getActivity(), "data not found", Toast.LENGTH_LONG).show()
            }
        }?.addOnFailureListener { exception ->
            // Handle any errors
            Toast.makeText(getActivity(), "some errors", Toast.LENGTH_LONG).show()
        }

        binding.logoutButton.setOnClickListener{
            auth.signOut()
            startActivity(Intent(getActivity(), LoginActivity::class.java))
        }
    }


}