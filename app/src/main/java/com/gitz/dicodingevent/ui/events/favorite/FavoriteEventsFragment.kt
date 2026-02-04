package com.gitz.dicodingevent.ui.events.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.adapter.EventAdapter
import com.gitz.dicodingevent.data.remote.response.EventItem
import com.gitz.dicodingevent.databinding.FragmentFavoriteEventsBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.utils.addBottomPaddingForLastItem
import com.gitz.dicodingevent.viewmodel.event.EventViewModel
import com.gitz.dicodingevent.viewmodel.ViewModelFactory

class FavoriteEventsFragment : Fragment() {

    private var _binding: FragmentFavoriteEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(
            Injection.provideRepository(requireContext()),
            Injection.provideSettingPreferences(requireContext()),
            Injection.provideWorkManager(requireContext())
        )
    }

    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavorites()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            findNavController().navigate(
                R.id.action_favorite_to_detail,
                bundleOf("eventId" to event.id)
            )
        }

        binding.rvFavoriteEvents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@FavoriteEventsFragment.adapter
        }

        binding.root.post {
            val navView = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
            val bottomNavHeight = navView?.height ?: 0
            if (isAdded) {
                binding.rvFavoriteEvents.addBottomPaddingForLastItem(R.layout.item_event, bottomNavHeight)
            }
        }
    }

    private fun observeFavorites() {
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteList ->
            binding.progressBar.visibility = View.GONE

            val items = favoriteList.map { fav ->
                EventItem(
                    id = fav.id,
                    name = fav.name,
                    mediaCover = fav.mediaCover ?: "",
                    summary = "",
                    registrants = 0,
                    imageLogo = "",
                    link = "",
                    description = "",
                    ownerName = "",
                    cityName = "",
                    quota = 0,
                    beginTime = "",
                    endTime = "",
                    category = ""
                )
            }

            adapter.submitList(items)

            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}