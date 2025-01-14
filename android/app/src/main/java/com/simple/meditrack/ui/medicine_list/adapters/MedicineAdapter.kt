package com.simple.meditrack.ui.medicine_list.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemMedicineBinding

open class MedicineAdapter(onItemClick: (View, MedicineViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<MedicineViewItem, ItemMedicineBinding>(onItemClick) {

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_NAME)) {
            refreshName(binding, item)
        }

        if (payloads.contains(PAYLOAD_IMAGE)) {
            refreshImage(binding, item)
        }

        if (payloads.contains(PAYLOAD_DESCRIPTION)) {
            refreshDescription(binding, item)
        }
    }

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem) {
        super.bind(binding, viewType, position, item)

        refreshName(binding, item)
        refreshImage(binding, item)
        refreshDescription(binding, item)
    }

    private fun refreshName(binding: ItemMedicineBinding, item: MedicineViewItem) {
        binding.tvName.text = item.name
    }

    private fun refreshImage(binding: ItemMedicineBinding, item: MedicineViewItem) {
        binding.ivImage.setImage(item.image)
    }

    private fun refreshDescription(binding: ItemMedicineBinding, item: MedicineViewItem) {
        binding.tvDescription.text = item.description
    }
}

class MedicineViewItem(
    val id: String = "",

    val image: String = "",

    var name: CharSequence = "",
    var description: CharSequence = "",
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        name to PAYLOAD_NAME,
        image to PAYLOAD_IMAGE,
        description to PAYLOAD_DESCRIPTION
    )
}

private const val PAYLOAD_NAME = "PAYLOAD_NAME"
private const val PAYLOAD_IMAGE = "PAYLOAD_IMAGE"
private const val PAYLOAD_DESCRIPTION = "PAYLOAD_DESCRIPTION"