package com.example.chilli.searchGroup

import android.app.ProgressDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.chilli.databinding.FragmentSearchGroupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class SearchGroupFragment : Fragment() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: FragmentSearchGroupBinding

    companion object {
        private const val CAMERA_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchGroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPermission()
        setupCodeScanner()
    }

    private fun setupCodeScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                requireActivity().runOnUiThread {
                    binding.tvTextView.text = it.text
                    ViewGroup(it.text)
                }
            }

            errorCallback = ErrorCallback { error ->
                requireActivity().runOnUiThread {
                    Log.e("Main", "Camera initialization error: ${error.message}")
                }
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    fun ViewGroup(id: String){
        val progress = ProgressDialog(requireActivity()).apply {
            setTitle("Upload File...")
            show()
        }
        val db = FirebaseFirestore.getInstance()
        val user = db.collection("User")
        val group = db.collection("Group")
        val groupRef = group.document(id)
        val documentRef = user.document(FirebaseAuth.getInstance().currentUser?.uid!!)

        val data = hashMapOf(
            "type" to "member",
            "userId" to FirebaseAuth.getInstance().currentUser?.uid!!
        )
        documentRef.update("group", FieldValue.arrayUnion(id))
            .addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(this.activity, "Success upload", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                progress.dismiss()
                Toast.makeText(this.activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }

        groupRef.update("user", FieldValue.arrayUnion(data))
            .addOnSuccessListener {
                progress.dismiss()
                Toast.makeText(this.activity, "Success upload", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                progress.dismiss()
                Toast.makeText(this.activity, "Failed Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermission() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        requireContext(),
                        "Camera permission granted. You can use this feature now.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You need the camera permission to use this feature",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
