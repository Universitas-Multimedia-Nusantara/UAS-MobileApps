package com.example.chilli

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.chilli.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        login()
    }

    private fun login(){
        binding.linkRegis.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisActivity::class.java))
        }

        var error = false
        binding.loginButton.setOnClickListener{
            if(TextUtils.isEmpty(binding.emailInput.text.toString())){
                binding.emailInput.setError("Please enter username")
                error = true

            }

            if(TextUtils.isEmpty(binding.passInput.text.toString())){
                binding.passInput.setError("Please enter username")

            }

            if(error) return@setOnClickListener

            auth.signInWithEmailAndPassword(binding.emailInput.text.toString(), binding.passInput.text.toString())
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG)
                    }
                }
        }
    }
}