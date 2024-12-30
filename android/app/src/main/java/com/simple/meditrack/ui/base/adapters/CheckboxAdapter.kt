package com.simple.meditrack.ui.base.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemCheckboxBinding

open class CheckboxAdapter(onItemClick: (View, CheckboxViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<CheckboxViewItem, ItemCheckboxBinding>(onItemClick) {

    override fun bind(binding: ItemCheckboxBinding, viewType: Int, position: Int, item: CheckboxViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(IMAGE)) {
            refreshImage(binding, item)
        }
    }

    override fun bind(binding: ItemCheckboxBinding, viewType: Int, position: Int, item: CheckboxViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvCheckbos.text = item.text

        refreshImage(binding, item)
    }

    private fun refreshImage(binding: ItemCheckboxBinding, item: CheckboxViewItem) {

        binding.ivCheckbox.setImage(item.image)
    }
}

class CheckboxViewItem(
    val id: String = "",

    var text: CharSequence = "",
    var image: Int,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        image to IMAGE
    )
}

private const val IMAGE = "IMAGE"