package id.ac.umn.chilli.broadcash.messageDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import id.ac.umn.chilli.database.Messages
import id.ac.umn.chilli.databinding.FragmentMessageDetailBinding


class MessageDetailFragment : Fragment() {
    private lateinit var binding: FragmentMessageDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMessageDetailBinding.inflate(inflater)

        val item = arguments?.getSerializable("idMessage") as? Messages
        binding.body.text = item?.body
        binding.title.text = item?.title
        binding.timestamp.text = item?.timestamp.toString()


        return binding.root
    }
}