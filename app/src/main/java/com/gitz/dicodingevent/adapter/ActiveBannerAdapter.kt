package com.gitz.dicodingevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.databinding.ItemEventBannerBinding

class ActiveBannerAdapter(
    private val onClick: (EventItem) -> Unit
) : ListAdapter<EventItem, ActiveBannerAdapter.ViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(a: EventItem, b: EventItem) = a.id == b.id
            override fun areContentsTheSame(a: EventItem, b: EventItem) = a == b
        }
    }

    inner class ViewHolder(
        private val binding: ItemEventBannerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: EventItem) {
            binding.tvTitle.text = event.name

            Glide.with(binding.root)
                .load(event.imageLogo)
                .into(binding.imgBanner)

            binding.root.setOnClickListener {
                onClick(event)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEventBannerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
