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
    }

    override fun bind(binding: ItemAlarmBinding, viewType: Int, position: Int, item: AlarmViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvName.text = item.name

        binding.ivImage.setImage(item.image)

        binding.tvDescription.text = item.description
        binding.tvDescription.setVisible(item.description.isNotBlank())

        binding.tvTime.text = item.time
    }

}

class AlarmViewItem(
    val data: Alarm,

    val image: String,

    var name: CharSequence = "",
    val description: CharSequence = "",

    var time: CharSequence = "",
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        time
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        time to TIME
    )
}

private const val TIME = "TIME"