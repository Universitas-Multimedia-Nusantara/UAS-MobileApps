package id.ac.umn.chilli.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ac.umn.chilli.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import id.ac.umn.chilli.databinding.FragmentProfileBinding
import id.ac.umn.chilli.viewModel.getUser


class ProfileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    var database: FirebaseFirestore? = null
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
        val userViewModel = getUser(this, application).userViewModel
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.userViewModel = userViewModel

        loadProfile()

        return binding.root
    }


    private fun loadProfile(){
        binding.logoutButton.setOnClickListener{
            val context: Context = requireContext()
            val databaseName = "database_chilli2"

            val deleted = context.deleteDatabase(databaseName)

            if (deleted == true) {
                auth.signOut()
                startActivity(Intent(activity, LoginActivity::class.java))
            } else {
                loadProfile()
            }
        }
    }


}