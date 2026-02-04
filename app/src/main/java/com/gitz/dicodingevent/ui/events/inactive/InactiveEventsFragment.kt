package com.gitz.dicodingevent.ui.events.inactive

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.databinding.FragmentInactiveEventsBinding
import com.gitz.dicodingevent.ui.events.BaseEventFragment

class InactiveEventsFragment : BaseEventFragment<FragmentInactiveEventsBinding>() {

    override val eventType = 0
    override val navActionId = R.id.action_inactive_to_detail

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentInactiveEventsBinding.inflate(inflater, container, false)

    override fun getRecyclerView() = binding.rvEvents
    override fun getSwipeRefresh() = binding.swipeRefresh
    override fun getProgressBar() = binding.progressBar
}