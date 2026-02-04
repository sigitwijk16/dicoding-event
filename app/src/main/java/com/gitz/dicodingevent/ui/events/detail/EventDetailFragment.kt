package com.gitz.dicodingevent.ui.events.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.gitz.dicodingevent.data.Result
import com.gitz.dicodingevent.databinding.FragmentEventDetailBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.utils.DateFormatter
import com.gitz.dicodingevent.viewmodel.eventdetail.EventDetailViewModel
import androidx.core.content.ContextCompat
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.data.remote.response.EventItem

class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventDetailViewModel by viewModels {
        ViewModelFactory.getInstance(
            Injection.provideRepository(requireContext()),
            Injection.provideSettingPreferences(requireContext()),
            Injection.provideWorkManager(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = requireArguments().getInt("eventId")

        viewModel.loadDetail(eventId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.fabFavorite.isVisible = false
                }
                is Result.Success -> {
                    binding.progressBar.isVisible = false
                    binding.fabFavorite.isVisible = true

                    val event = result.data
                    setupUI(event)
                    setupFavoriteLogic(event)
                }
                is Result.Error -> {
                    binding.progressBar.isVisible = false
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUI(event: EventItem) {
        with(binding) {
            tvName.text = event.name
            tvOwner.text = "Organized by ${event.ownerName}"
            tvTime.text = DateFormatter.formatDateTime(event.beginTime)
            tvQuota.text = "Remaining Quota: ${event.quota - event.registrants}"

            val cleanHtml = event.description.replace(Regex("<img[^>]*>"), "")
            tvDescription.text = HtmlCompat.fromHtml(cleanHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)

            Glide.with(this@EventDetailFragment).load(event.mediaCover).into(ivEvent)

            btnLink.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, event.link.toUri()))
            }
        }
    }

    private fun setupFavoriteLogic(event: EventItem) {
        viewModel.isFavorite(event.id).observe(viewLifecycleOwner) { favoriteRecord ->
            val isCurrentlyFavorite = favoriteRecord != null

            val icon = if (isCurrentlyFavorite) {
                R.drawable.ic_favorite_active
            } else {
                R.drawable.ic_favorite_border
            }
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), icon))

            binding.fabFavorite.setOnClickListener {
                if (isCurrentlyFavorite) {
                    viewModel.setFavorite(event, false)
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setFavorite(event, true)
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}