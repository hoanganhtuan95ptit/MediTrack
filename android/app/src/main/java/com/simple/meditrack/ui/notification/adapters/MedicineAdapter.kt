package com.simple.meditrack.ui.notification.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setVisible
import com.simple.image.setImage
import com.simple.meditrack.R
import com.simple.meditrack.databinding.ItemMedicineBinding
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.exts.setBackground

open class MedicineAdapter(onItemClick: (View, MedicineViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<MedicineViewItem, ItemMedicineBinding>(onItemClick) {

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_IMAGE_RES)) {
            refreshActionRes(binding, item)
        }

        if (payloads.contains(PAYLOAD_IMAGE_SHOW)) {
            refreshActionShow(binding, item)
        }

        if (payloads.contains(PAYLOAD_BACKGROUND)) {
            refreshBackground(binding, item)
        }
    }

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvName.text = item.name

        binding.tvDescription.text = item.desciption
        binding.tvDescription.setVisible(item.desciption.isNotBlank())

        refreshActionRes(binding, item)
        refreshActionShow(binding, item)
        refreshBackground(binding, item)
    }

    private fun refreshActionRes(binding: ItemMedicineBinding, item: MedicineViewItem) {

        binding.ivImage.setImage(item.actionRes)
    }

    private fun refreshActionShow(binding: ItemMedicineBinding, item: MedicineViewItem) {

        binding.ivImage.setVisible(item.actionShow)
    }

    private fun refreshBackground(binding: ItemMedicineBinding, item: MedicineViewItem) {

        binding.root.delegate.setBackground(item.background)
    }
}

class MedicineViewItem(
    val id: String = "",

    val data: Medicine? = null,

    var name: CharSequence = "",
    var desciption: CharSequence,

    val actionRes: Int = R.drawable.ic_tick_24dp,
    val actionShow: Boolean = false,

    val background: Background? = null
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        actionRes to PAYLOAD_IMAGE_RES,
        actionShow to PAYLOAD_IMAGE_SHOW,
        (background ?: Unit) to PAYLOAD_BACKGROUND
    )
}

private const val PAYLOAD_IMAGE_RES = "PAYLOAD_IMAGE_RES"
private const val PAYLOAD_IMAGE_SHOW = "PAYLOAD_IMAGE_RES"
private const val PAYLOAD_BACKGROUND = "PAYLOAD_BACKGROUND_COLOR"
