package com.simple.meditrack.ui.medicine_list.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.meditrack.databinding.ItemMedicineBinding

open class MedicineAdapter(onItemClick: (View, MedicineViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<MedicineViewItem, ItemMedicineBinding>(onItemClick) {

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvName.text = item.name
        binding.tvDescription.text = item.description
    }
}

class MedicineViewItem(
    val id: String = "",

    var name: CharSequence = "",
    var description: CharSequence = "",
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )
}