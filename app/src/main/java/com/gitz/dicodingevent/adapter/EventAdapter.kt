package com.gitz.dicodingevent.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gitz.dicodingevent.data.response.EventItem
import com.gitz.dicodingevent.databinding.ItemEventBinding

class EventAdapter(
    private val onClick: (EventItem) -> Unit
) : ListAdapter<EventItem, EventAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventItem) {
            binding.tvName.text = item.name
            binding.tvOwnerName.text = item.ownerName
            Glide.with(binding.root)
                .load(item.imageLogo.ifEmpty { item.mediaCover })
                .into(binding.ivEvent)

            binding.root.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(old: EventItem, new: EventItem) = old.id == new.id
            override fun areContentsTheSame(old: EventItem, new: EventItem) = old == new
        }
    }
}
