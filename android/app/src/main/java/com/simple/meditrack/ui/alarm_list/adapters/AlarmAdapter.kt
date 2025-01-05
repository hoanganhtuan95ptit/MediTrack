package com.simple.meditrack.ui.alarm_list.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setVisible
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemAlarmBinding
import com.simple.meditrack.entities.Alarm

open class AlarmAdapter(onItemClick: (View, AlarmViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<AlarmViewItem, ItemAlarmBinding>(onItemClick) {

    override fun bind(binding: ItemAlarmBinding, viewType: Int, position: Int, item: AlarmViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(TIME)) {
            refreshTime(binding, item)
        }

        if (payloads.contains(NAME)) {
            refreshName(binding, item)
        }

        if (payloads.contains(IMAGE)) {
            refreshImage(binding, item)
        }

        if (payloads.contains(DESCRIPTION)) {
            refreshDescription(binding, item)
        }
    }

    override fun bind(binding: ItemAlarmBinding, viewType: Int, position: Int, item: AlarmViewItem) {
        super.bind(binding, viewType, position, item)

        binding.root.transitionName = item.id

        refreshTime(binding, item)
        refreshName(binding, item)
        refreshImage(binding, item)
        refreshDescription(binding, item)
    }

    private fun refreshTime(binding: ItemAlarmBinding, item: AlarmViewItem) {

        binding.tvTime.text = item.time
    }

    private fun refreshName(binding: ItemAlarmBinding, item: AlarmViewItem) {

        binding.tvName.text = item.name
    }

    private fun refreshImage(binding: ItemAlarmBinding, item: AlarmViewItem) {

        binding.ivImage.setImage(item.image)
    }

    private fun refreshDescription(binding: ItemAlarmBinding, item: AlarmViewItem) {

        binding.tvDescription.text = item.description
        binding.tvDescription.setVisible(item.description.isNotBlank())
    }
}

class AlarmViewItem(
    val id: String,
    val data: Alarm,

    val image: String,

    var name: CharSequence = "",
    val description: CharSequence = "",

    var time: CharSequence = "",
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        time to TIME,
        name to NAME,
        image to IMAGE,
        description to DESCRIPTION
    )
}

private const val TIME = "TIME"
private const val NAME = "NAME"
private const val IMAGE = "IMAGE"
private const val DESCRIPTION = "DESCRIPTION"