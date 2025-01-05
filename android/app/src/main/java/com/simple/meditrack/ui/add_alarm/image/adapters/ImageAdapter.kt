package com.simple.meditrack.ui.add_alarm.image.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setVisible
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemAlarmBinding
import com.simple.meditrack.databinding.ItemImageBinding
import com.simple.meditrack.entities.Alarm

open class ImageAdapter(onItemClick: (View, ImageViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<ImageViewItem, ItemImageBinding>(onItemClick) {

    override fun bind(binding: ItemImageBinding, viewType: Int, position: Int, item: ImageViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)
    }

    override fun bind(binding: ItemImageBinding, viewType: Int, position: Int, item: ImageViewItem) {
        super.bind(binding, viewType, position, item)

        binding.root.transitionName = item.id

        binding.ivImage.setImage(item.image)
    }
}

class ImageViewItem(
    val id: String,
    val data: String,

    val image: String,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
    )
}