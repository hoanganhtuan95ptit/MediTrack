package com.simple.meditrack.ui.add_alarm.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemImageBinding
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.exts.setBackground

open class ImageAdapter(
    onItemClick: (View, ImageViewItem) -> Unit
) : ViewItemAdapter<ImageViewItem, ItemImageBinding>(onItemClick) {

    override fun bind(binding: ItemImageBinding, viewType: Int, position: Int, item: ImageViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_IMAGE)) {
            refreshImage(binding, item)
        }
    }

    override fun bind(binding: ItemImageBinding, viewType: Int, position: Int, item: ImageViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivImage.setImage(item.image)

        binding.root.delegate.setBackground(item.background)

        refreshImage(binding, item)
    }

    private fun refreshImage(binding: ItemImageBinding, item: ImageViewItem) {

        binding.ivImage.setImage(item.image)
    }
}

class ImageViewItem(
    val id: String = "",

    var image: String = "",

    var background: Background? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        image to PAYLOAD_IMAGE
    )
}

private const val PAYLOAD_IMAGE = "PAYLOAD_IMAGE"