package com.gitz.dicodingevent.ui.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.databinding.FragmentEventDetailBinding
import com.gitz.dicodingevent.viewmodel.EventDetailViewModel
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventId = requireArguments().getInt("eventId")

        viewModel.event.observe(viewLifecycleOwner) { event ->
            binding.tvName.text = event.name
            binding.tvOwner.text = event.ownerName
            binding.tvTime.text = event.beginTime
            binding.tvQuota.text = (event.quota - event.registrants).toString()
            binding.tvDescription.text = HtmlCompat.fromHtml(
                event.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this)
                .load(event.mediaCover)
                .into(binding.ivEvent)

            binding.btnLink.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(event.link)))
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { errorMessage ->
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadDetail(eventId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
