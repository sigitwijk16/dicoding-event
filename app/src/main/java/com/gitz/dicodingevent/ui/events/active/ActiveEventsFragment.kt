package com.gitz.dicodingevent.ui.events.active

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.adapter.EventAdapter
import com.gitz.dicodingevent.data.Result
import com.gitz.dicodingevent.databinding.FragmentActiveEventsBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.utils.addBottomPaddingForLastItem
import com.gitz.dicodingevent.viewmodel.event.EventViewModel

class ActiveEventsFragment : Fragment() {

    private var _binding: FragmentActiveEventsBinding? = null
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
        _binding = FragmentActiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchBar()

        observeEvents(1)
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            findNavController().navigate(
                R.id.action_active_to_detail,
                bundleOf("eventId" to event.id)
            )
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

        binding.root.post {
            val navView = activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
            val bottomNavHeight = navView?.height ?: 0
            binding.rvEvents.addBottomPaddingForLastItem(R.layout.item_event, bottomNavHeight)
        }
    }

    private fun setupSearchBar() {
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView.editText.setOnEditorActionListener { _, _, _ ->
                val query = searchView.text.toString()
                searchBar.setText(query)
                searchView.hide()

                if (query.isNotEmpty()) {
                    performSearch(query)
                } else {
                    observeEvents(1)
                }
                false
            }
        }
    }

    private fun observeEvents(active: Int) {
        viewModel.loadEvents(active).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is Result.Success -> {
                        binding.progressBar.isVisible = false
                        adapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun performSearch(query: String) {
        viewModel.searchEvents(query).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is Result.Success -> {
                        binding.progressBar.isVisible = false
                        adapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}