package id.ac.umn.chilli.addOrganisasi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import id.ac.umn.chilli.databinding.FragmentIdGroupBinding


class IdGroupFragment : Fragment() {
    private lateinit var binding: FragmentIdGroupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val id = (arguments?.getString("idGroup") as? String).toString()

        binding = FragmentIdGroupBinding.inflate(inflater)

        val qrCodeBitmap = generateQR(id, 500, 500)

        binding.QrView.setImageBitmap(qrCodeBitmap)
        binding.idView.text = id
        return binding.root
    }

}