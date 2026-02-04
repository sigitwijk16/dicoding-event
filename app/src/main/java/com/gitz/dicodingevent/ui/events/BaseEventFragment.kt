package com.gitz.dicodingevent.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.adapter.EventAdapter
import com.gitz.dicodingevent.data.Result
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.utils.addBottomPaddingForLastItem
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.viewmodel.event.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseEventFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val viewModel: EventViewModel by activityViewModels {
        ViewModelFactory.getInstance(
            Injection.provideRepository(requireContext()),
            Injection.provideSettingPreferences(requireContext()),
            Injection.provideWorkManager(requireContext())
        )
    }

    protected lateinit var adapter: EventAdapter
    protected abstract val eventType: Int
    protected abstract val navActionId: Int

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB
    protected abstract fun getRecyclerView(): RecyclerView
    protected abstract fun getSwipeRefresh(): SwipeRefreshLayout
    protected abstract fun getProgressBar(): ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel.searchQuery.observe(viewLifecycleOwner) { query ->
            if (query.isNullOrEmpty()) {
                getSwipeRefresh().isEnabled = true
                observeMainEvents()
            } else {
                getSwipeRefresh().isEnabled = false
                performSearch(query)
            }
        }
    }

    private fun performSearch(query: String) {
        viewModel.searchEvents(query).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        getProgressBar().isVisible = true
                    }
                    is Result.Success -> {
                        getProgressBar().isVisible = false
                        adapter.submitList(result.data) {
                            getRecyclerView().scrollToPosition(0)
                        }
                    }
                    is Result.Error -> {
                        getProgressBar().isVisible = false
                        Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { event ->
            hideKeyboard()
            findNavController().navigate(navActionId, bundleOf("eventId" to event.id))
        }

        getRecyclerView().apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@BaseEventFragment.adapter
        }

        getRecyclerView().addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    activity?.currentFocus?.clearFocus()

                    val imm = requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }
            }
        })

        getSwipeRefresh().apply {
            setColorSchemeResources(R.color.gold_primary)
            setOnRefreshListener {
                val query = viewModel.searchQuery.value
                if (query.isNullOrEmpty()) observeMainEvents() else performSearch(query)
            }
        }

        binding.root.post {
            val navView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
            val bottomNavHeight = navView?.height ?: 0
            getRecyclerView().addBottomPaddingForLastItem(R.layout.item_event, bottomNavHeight)
        }
    }

    private fun observeMainEvents() {
        viewModel.loadEvents(eventType).observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        if (!getSwipeRefresh().isRefreshing) {
                            getProgressBar().isVisible = true
                        }
                    }
                    is Result.Success -> {
                        getProgressBar().isVisible = false
                        getSwipeRefresh().isRefreshing = false
                        adapter.submitList(result.data) {
                            getRecyclerView().scrollToPosition(0)
                        }
                    }
                    is Result.Error -> {
                        getProgressBar().isVisible = false
                        getSwipeRefresh().isRefreshing = false
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