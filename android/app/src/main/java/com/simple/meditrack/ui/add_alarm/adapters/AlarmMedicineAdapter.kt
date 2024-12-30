package com.simple.meditrack.ui.add_alarm.adapters

import android.view.View
import android.view.ViewGroup
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.meditrack.databinding.ItemMedicineItemBinding
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.exts.setBackground

open class AlarmMedicineAdapter(
    private val onItemClick: (View, AlarmMedicineViewItem) -> Unit,
    private val onRemoveClick: (View, AlarmMedicineViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<AlarmMedicineViewItem, ItemMedicineItemBinding>(onItemClick) {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemMedicineItemBinding>? {

        val holder = super.createViewHolder(parent, viewType) ?: return null

        holder.binding.ivRemove.setDebouncedClickListener {

            val item = getViewItem(holder.bindingAdapterPosition) ?: return@setDebouncedClickListener

            item.text = it.toString()

            onRemoveClick(holder.binding.ivRemove, item)
        }

        return holder
    }

    override fun bind(binding: ItemMedicineItemBinding, viewType: Int, position: Int, item: AlarmMedicineViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvName.text = item.text

        binding.tvDescription.text = item.description

        binding.frameContent.delegate.setBackground(item.background)
    }
}

class AlarmMedicineViewItem(
    val id: String = "",
    val data: Alarm.MedicineItem,

    var text: CharSequence = "",
    val description: CharSequence = "",

    var background: Background? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
    )
}