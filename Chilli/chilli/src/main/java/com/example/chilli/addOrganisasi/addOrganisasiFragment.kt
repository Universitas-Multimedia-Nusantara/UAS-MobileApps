package com.example.chilli.addOrganisasi

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chilli.databinding.FragmentAddOrganisasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class addOrganisasiFragment : Fragment() {
    private lateinit var binding: FragmentAddOrganisasiBinding
    private lateinit var imageUri: Uri
    lateinit var progress: ProgressDialog
    private lateinit var db: FirebaseFirestore
    private lateinit var id: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentAddOrganisasiBinding.inflate(inflater)


        binding.prevButon.setOnClickListener{
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.createButton.setOnClickListener{
            createGroup()
        }

        binding.profileButton.setOnClickListener{
            selectImage()
        }



        return binding.root
    }


    private fun selectImage(){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && data != null && data.data != null){
            imageUri = data.data!!
            Glide.with(this).load(imageUri).into(binding.profileButton)

        }
    }

    fun createGroup(){
        progress = ProgressDialog(requireActivity()).apply {
            setTitle("Upload File...")
            show()
        }
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val fileName = formatter.format(Date())


        val storageReference = FirebaseStorage.getInstance().getReference("images/"+fileName)
        storageReference.putFile(imageUri).addOnSuccessListener{taskSnapshot->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { url ->
                val imageUrl = url.toString()
                uploadData(imageUrl)
            }
        }.addOnFailureListener{exception->
            progress.dismiss()
            Toast.makeText(this.activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadData(image: String){
        id = FirebaseAuth.getInstance().currentUser?.uid!!

        val user = hashMapOf(
            "type" to "admin",
            "userId" to id
        )

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
                progress.dismiss()
                Toast.makeText(this.activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }
    }


    private fun addUserArray(groupId: String){
        val documentRef = db.collection("User").document(id)
        documentRef.update("group", FieldValue.arrayUnion(groupId))
            .addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(this.activity, "Success upload", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                progress.dismiss()
                Toast.makeText(this.activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

}