package com.example.chilli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chilli.databinding.ActivityRegisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class RegisActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityRegisBinding
    var database: FirebaseFirestore? = null
    var databaseRef: CollectionReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        databaseRef = database?.collection("User")

        register()
    }

    private fun register(){

        var error = false

        binding.regisButton.setOnClickListener{
            if( TextUtils.isEmpty(binding.nameInput.text.toString())){
                binding.nameInput.error = "Please enter first name"
                error = true
            }

            if(TextUtils.isEmpty(binding.nickNameInput.text.toString())){
                binding.nickNameInput.error = "Please enter nickname"
                error = true
            }

            if(TextUtils.isEmpty(binding.emailInput.text.toString())){
                binding.emailInput.error = "Please enter email"
                error = true
            }

            if(TextUtils.isEmpty(binding.passInput.text.toString())){
                binding.passInput.error = "Please enter password"
                error = true
            }

            if(error) return@setOnClickListener

            auth.createUserWithEmailAndPassword(binding.emailInput.text.toString(), binding.passInput.text.toString())
                .addOnCompleteListener{
                    val user = auth.currentUser

                    if(it.isSuccessful){
                       val data = hashMapOf(
                           "email" to binding.emailInput.text.toString(),
                           "name" to binding.nameInput.text.toString(),
                           "nickName" to binding.nickNameInput.text.toString(),
                       )

                        databaseRef?.document(user?.uid!!)?.set(data)!!
                            .addOnSuccessListener {
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this@RegisActivity, "Registration failed, please try again", Toast.LENGTH_LONG)
                            }

//                        Toast.makeText(this@RegisActivity, "Registration Success", Toast.LENGTH_LONG)

                    }else{
                        Toast.makeText(this@RegisActivity, "Registration failed, please try again", Toast.LENGTH_LONG)
                    }
                }

        }
    }
}