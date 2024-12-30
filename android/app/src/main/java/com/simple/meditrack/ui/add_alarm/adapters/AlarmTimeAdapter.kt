package com.simple.meditrack.ui.add_alarm.adapters

import android.view.View
import android.view.ViewGroup
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.image.setImage
import com.simple.meditrack.Param
import com.simple.meditrack.databinding.ItemAlarmTimeBinding
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.exts.setBackground

open class AlarmTimeAdapter(
    private val onTimeClick: (View, AlarmTimeViewItem) -> Unit,
    private val onImageClick: (View, AlarmTimeViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<AlarmTimeViewItem, ItemAlarmTimeBinding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemAlarmTimeBinding>? {

        val holder = super.createViewHolder(parent, viewType) ?: return null

        holder.binding.tvTime.setDebouncedClickListener {

            val item = getViewItem(holder.bindingAdapterPosition) ?: return@setDebouncedClickListener

            onTimeClick(holder.binding.tvTime, item)
        }

        holder.binding.ivImage.setDebouncedClickListener {

            val item = getViewItem(holder.bindingAdapterPosition) ?: return@setDebouncedClickListener

            onImageClick(holder.binding.ivImage, item)
        }

        return holder
    }

    override fun bind(binding: ItemAlarmTimeBinding, viewType: Int, position: Int, item: AlarmTimeViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_TIME)) {
            refreshTime(binding, item)
        }
    }

    override fun bind(binding: ItemAlarmTimeBinding, viewType: Int, position: Int, item: AlarmTimeViewItem) {
        super.bind(binding, viewType, position, item)


        binding.ivImage.setImage(item.image)

        binding.root.delegate.setBackground(item.background)

        refreshTime(binding, item)
    }

    private fun refreshTime(binding: ItemAlarmTimeBinding, item: AlarmTimeViewItem) {

        binding.tvTime.text = item.time
    }
}

class AlarmTimeViewItem(
    val id: String = "",

    val time: CharSequence = "",
    var image: String = "",

    var background: Background? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        time to PAYLOAD_TIME
    )
}

private const val PAYLOAD_TIME = "PAYLOAD_TIME"