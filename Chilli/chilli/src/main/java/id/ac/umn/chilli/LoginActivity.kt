package id.ac.umn.chilli

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import id.ac.umn.chilli.databinding.ActivityLoginBinding

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

        binding.linkRegis.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisActivity::class.java))
        }

        binding.loginButton.setOnClickListener {login()}
    }

    private fun login() {
        var error = false

            if (TextUtils.isEmpty(binding.emailInput.text.toString())) {
                binding.emailInput.error = "Please enter username"
                error = true
            }

            if (TextUtils.isEmpty(binding.passInput.text.toString())) {
                binding.passInput.error = "Please enter password"
                error = true
            }

            if (error) return

            // Show loading progress
            binding.loadingView.visibility = View.VISIBLE

            var email = binding.emailInput.text.toString().replace("\\s".toRegex(), "") // Remove all whitespace

            auth.signInWithEmailAndPassword(
                email,
                binding.passInput.text.toString()
            )
                .addOnCompleteListener {
                    // Hide loading progress
                    binding.loadingView.visibility = View.GONE

                    if (it.isSuccessful) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                }

    }

}