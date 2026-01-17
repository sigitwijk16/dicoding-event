package com.gitz.dicodingevent.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gitz.dicodingevent.R
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gitz.dicodingevent.adapter.EventAdapter
import com.gitz.dicodingevent.databinding.FragmentActiveEventsBinding
import com.gitz.dicodingevent.utils.addBottomPaddingForLastItem
import com.gitz.dicodingevent.viewmodel.EventsViewModel
import com.google.android.material.search.SearchView

class ActiveEventsFragment : Fragment() {

    private var _binding: FragmentActiveEventsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EventsViewModel by viewModels()
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentActiveEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = EventAdapter { event ->
            findNavController().navigate(
                R.id.action_active_to_detail,
                bundleOf("eventId" to event.id)
            )
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

        binding.root.post {
            val bottomNavHeight = (activity?.findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.nav_view)
                ?.height ?: 0)
            binding.rvEvents.addBottomPaddingForLastItem(R.layout.item_event, bottomNavHeight)
        }

        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { textView, actionId, event ->
                    searchBar.setText(searchView.text)
                    searchView.hide()
                    viewModel.searchEvents(searchView.text.toString())
                    false
                }
        }

        viewModel.events.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.loadEvents(active = 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
