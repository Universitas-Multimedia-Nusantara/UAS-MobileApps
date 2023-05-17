package com.example.uconnect

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.uconnect.databinding.ActivityMainBinding
import com.example.uconnect.databinding.ActivityRegisBinding
import com.google.android.play.integrity.internal.t
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class HomeActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var database: FirebaseFirestore? = null
    var databaseRef: CollectionReference? = null
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        databaseRef = database?.collection("User")
        loadHome()
    }

    private fun loadHome(){

        val user = auth.currentUser
        val userRef = databaseRef?.document(user?.uid!!)

        userRef?.get()?.addOnSuccessListener{documentSnapshot->
            if (documentSnapshot.exists()) {
                binding.namaLengkap.text = documentSnapshot.getString("name")
                binding.namaPanggilan.text = documentSnapshot.getString("nickName")
                binding.email.text = documentSnapshot.getString("email")
            } else {
                Toast.makeText(this@HomeActivity, "data not found", Toast.LENGTH_LONG).show()
            }
        }?.addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this@HomeActivity, "some errors", Toast.LENGTH_LONG).show()
        }

        binding.logoutButton.setOnClickListener{
            auth.signOut()
            startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
            finish()
        }

    }
}