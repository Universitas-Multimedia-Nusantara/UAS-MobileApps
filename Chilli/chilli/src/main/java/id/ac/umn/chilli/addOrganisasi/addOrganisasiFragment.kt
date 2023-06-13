package id.ac.umn.chilli.addOrganisasi

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import id.ac.umn.chilli.R
import id.ac.umn.chilli.databinding.FragmentAddOrganisasiBinding
import java.text.SimpleDateFormat
import java.util.*

class addOrganisasiFragment : Fragment() {
    private lateinit var binding: FragmentAddOrganisasiBinding
    private var imageUri: Uri? = null
    private lateinit var progress: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var id: String

    // Default image URI when it is null
    private val defaultImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddOrganisasiBinding.inflate(inflater)

        binding.prevButon.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.createButton.setOnClickListener {
            createGroup()
        }

        binding.profileButton.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Glide.with(this).load(imageUri).into(binding.profileButton)
        }
    }

    private fun createGroup() {
        var uri = imageUri ?: defaultImageUri // Use default value if imageUri is null

        if(binding.groupNameText.text?.toString()?.isEmpty()!! || binding.groupNameText.text.toString() == null ){
            Toast.makeText(activity, "Group Name Required", Toast.LENGTH_SHORT).show()
        }else{
            if(uri == null){
                val drawable = resources.getDrawable(R.drawable.group_profile_png)
                uri = drawableToUri(this.requireContext(), drawable)
            }

            uri?.let { selectedUri ->
//            progress = ProgressDialog(requireActivity()).apply {
//                setTitle("Upload File...")
//                show()
//            }

                binding.loadingView.visibility = View.VISIBLE
                val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                val fileName = formatter.format(Date())

                val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")
                storageReference.putFile(selectedUri)
                    .addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener { url ->
                            val imageUrl = url.toString()
                            uploadData(imageUrl)
                        }
                    }
                    .addOnFailureListener { exception ->
                        binding.loadingView.visibility = View.GONE
                        Toast.makeText(activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
                    }
            }

        }


    }


    private fun uploadData(image: String) {
        id = FirebaseAuth.getInstance().uid!!

        val user = listOf(hashMapOf(
            "type" to "admin",
            "userId" to id
        ))

        val data = hashMapOf(
            "deskripsi" to binding.groupDescText.text.toString(),
            "foto" to image,
            "nama" to binding.groupNameText.text.toString(),
            "user" to user
        )

        db = FirebaseFirestore.getInstance()
        val collectionReference = db.collection("Group")

        // Add the string data as a new document with an auto-generated ID
        collectionReference.add(data)
            .addOnSuccessListener { documentReference ->
                addUserArray(documentReference.id)
            }
            .addOnFailureListener { exception ->
                binding.loadingView.visibility = View.GONE
                Toast.makeText(activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addUserArray(groupId: String) {
        val documentRef = db.collection("User").document(id)
        documentRef.update("group", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                binding.loadingView.visibility = View.GONE
                Toast.makeText(activity, "Success upload", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_addOrganisasiFragment_to_homeFragment2)
            }
            .addOnFailureListener { exception ->
                binding.loadingView.visibility = View.GONE
                Toast.makeText(activity, "Create Group Fail", Toast.LENGTH_SHORT).show()
            }
    }

    fun drawableToUri(context: Context, drawable: Drawable): Uri? {
        val resolver: ContentResolver = context.contentResolver
        val bitmap: Bitmap = (drawable as BitmapDrawable).bitmap

        return try {
            val path = MediaStore.Images.Media.insertImage(resolver, bitmap, "Title", null)
            Uri.parse(path)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

