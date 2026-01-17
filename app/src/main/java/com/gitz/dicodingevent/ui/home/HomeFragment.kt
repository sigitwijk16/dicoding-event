package com.gitz.dicodingevent.ui.home

import HorizontalSpaceItemDecoration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.adapter.ActiveBannerAdapter
import com.gitz.dicodingevent.adapter.EventAdapter
import com.gitz.dicodingevent.databinding.FragmentActiveEventsBinding
import com.gitz.dicodingevent.databinding.FragmentHomeBinding
import com.gitz.dicodingevent.viewmodel.HomeViewModel
import com.gitz.dicodingevent.utils.addBottomPaddingForLastItem

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activeAdapter = ActiveBannerAdapter { event ->
            findNavController().navigate(
                R.id.navigation_event_detail,
                bundleOf("eventId" to event.id)
            )
        }

        val inactiveAdapter = EventAdapter { event ->
            findNavController().navigate(
                R.id.navigation_event_detail,
                bundleOf("eventId" to event.id)
            )
        }

        binding.rvActive.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvInactive.layoutManager = LinearLayoutManager(context)

        binding.rvActive.adapter = activeAdapter
        binding.rvInactive.adapter = inactiveAdapter

        binding.rvActive.addItemDecoration(
            HorizontalSpaceItemDecoration(
                sidePadding = resources.getDimensionPixelSize(R.dimen.page_padding),
                itemSpacing = resources.getDimensionPixelSize(R.dimen.item_spacing)
            )
        )

        binding.root.post {
            val bottomNavHeight = (activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
                ?.height ?: 0)
            binding.rvInactive.addBottomPaddingForLastItem(R.layout.item_event, bottomNavHeight)
        }

        viewModel.activeEvents.observe(viewLifecycleOwner) {
            activeAdapter.submitList(it)
        }

        viewModel.inactiveEvents.observe(viewLifecycleOwner) {
            inactiveAdapter.submitList(it)
        }

        viewModel.loadHomeEvents()
    }
}
