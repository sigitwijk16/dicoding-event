package com.gitz.dicodingevent.ui.events.active

import android.view.LayoutInflater
import android.view.ViewGroup
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.databinding.FragmentActiveEventsBinding
import com.gitz.dicodingevent.ui.events.BaseEventFragment

class ActiveEventsFragment : BaseEventFragment<FragmentActiveEventsBinding>() {

    override val eventType = 1
    override val navActionId = R.id.action_active_to_detail

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentActiveEventsBinding.inflate(inflater, container, false)

    override fun getRecyclerView() = binding.rvEvents
    override fun getSwipeRefresh() = binding.swipeRefresh
    override fun getProgressBar() = binding.progressBar
}