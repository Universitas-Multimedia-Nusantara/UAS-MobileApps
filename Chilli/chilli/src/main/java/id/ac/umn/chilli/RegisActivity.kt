package id.ac.umn.chilli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.chilli.databinding.ActivityRegisBinding

class RegisActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivityRegisBinding
    var database: FirebaseFirestore? = null
    private var databaseRef: CollectionReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        databaseRef = database?.collection("User")

        binding.regisButton.setOnClickListener {register()}
    }

    private fun register() {
        var error = false


            if (TextUtils.isEmpty(binding.nameInput.text.toString())) {
                binding.nameInput.error = "Please enter first name"
                error = true
            }

            if (TextUtils.isEmpty(binding.nickNameInput.text.toString())) {
                binding.nickNameInput.error = "Please enter nickname"
                error = true
            }

            if (TextUtils.isEmpty(binding.emailInput.text.toString())) {
                binding.emailInput.error = "Please enter email"
                error = true
            }else if(binding.emailInput.text.toString().contains(" ")){
                binding.emailInput.error = "Email should not contain whitespace"
                error = true
            }

            if (TextUtils.isEmpty(binding.passInput.text.toString())) {
                binding.passInput.error = "Please enter password"
                error = true
            } else if (!isPasswordValid(binding.passInput.text.toString())) {
                binding.passInput.error = "Password must contain at least 6 characters, 1 number, and 1 uppercase letter"
                error = true
            }

            if (error) return

            binding.loadingView.visibility = View.VISIBLE


            auth.createUserWithEmailAndPassword(
                binding.emailInput.text.toString(),
                binding.passInput.text.toString()
            ).addOnCompleteListener { task ->
                val user = auth.currentUser

                var email = binding.emailInput.text.toString().replace("\\s".toRegex(), "") // Remove all whitespace

                if (task.isSuccessful) {

                    val data = hashMapOf(
                        "foto" to null,
                        "email" to email,
                        "name" to binding.nameInput.text.toString(),
                        "nickName" to binding.nickNameInput.text.toString(),
                        "group" to listOf<String>()
                    )

                    databaseRef?.document(user?.uid!!)?.set(data)!!
                        .addOnSuccessListener {
                            binding.loadingView.visibility = View.GONE
                            finish()
                        }
                        .addOnFailureListener {
                            binding.loadingView.visibility = View.GONE
                            Toast.makeText(
                                this@RegisActivity,
                                "Registration failed, please try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                } else {
                    binding.loadingView.visibility = View.GONE
                    Toast.makeText(
                        this@RegisActivity,
                        "Registration failed, please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    private fun isPasswordValid(password: String): Boolean {
        val passwordRegex = "^(?=.*[A-Z])(?=.*[0-9]).{6,}$".toRegex()
        return passwordRegex.matches(password)
    }

}