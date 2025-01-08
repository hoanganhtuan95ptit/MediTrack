package com.simple.meditrack.ui.alarm_add.adapters

import android.view.View
import android.view.ViewGroup
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.ext.updateMargin
import com.simple.meditrack.databinding.ItemAlarmMedicineBinding
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Margin
import com.simple.meditrack.utils.exts.setBackground

open class AlarmMedicineAdapter(
    private val onItemClick: (View, AlarmMedicineViewItem) -> Unit,
    private val onRemoveClick: (View, AlarmMedicineViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<AlarmMedicineViewItem, ItemAlarmMedicineBinding>(onItemClick) {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemAlarmMedicineBinding>? {

        val holder = super.createViewHolder(parent, viewType) ?: return null

        holder.binding.ivRemove.setDebouncedClickListener {

            val item = getViewItem(holder.bindingAdapterPosition) ?: return@setDebouncedClickListener

            item.text = it.toString()

            onRemoveClick(holder.binding.root, item)
        }

        return holder
    }

    override fun bind(binding: ItemAlarmMedicineBinding, viewType: Int, position: Int, item: AlarmMedicineViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(TEXT)) {
            refreshText(binding, item)
        }

        if (payloads.contains(DESCRIPTION)) {
            refreshDescription(binding, item)
        }
    }

    override fun bind(binding: ItemAlarmMedicineBinding, viewType: Int, position: Int, item: AlarmMedicineViewItem) {
        super.bind(binding, viewType, position, item)

        binding.root.transitionName = item.id

        item.margin?.let {
            binding.frameContent.updateMargin(left = it.left, top = it.top, right = it.right, bottom = it.bottom)
        }

        binding.frameContent.delegate.setBackground(item.background)

        refreshText(binding, item)
        refreshDescription(binding, item)
    }

    private fun refreshText(binding: ItemAlarmMedicineBinding, item: AlarmMedicineViewItem) {
        binding.tvName.text = item.text
    }

    private fun refreshDescription(binding: ItemAlarmMedicineBinding, item: AlarmMedicineViewItem) {
        binding.tvDescription.text = item.description
    }
}

class AlarmMedicineViewItem(
    val id: String = "",
    val data: Alarm.MedicineItem,

    var text: CharSequence = "",
    val description: CharSequence = "",

    var margin: Margin? = null,
    var background: Background? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        text to TEXT,
        description to DESCRIPTION
    )
}

private const val TEXT = "TEXT"
private const val DESCRIPTION = "DESCRIPTION"