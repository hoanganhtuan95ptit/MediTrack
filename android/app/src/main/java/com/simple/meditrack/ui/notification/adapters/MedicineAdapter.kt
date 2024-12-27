package com.simple.meditrack.ui.notification.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setVisible
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemMedicineBinding

open class MedicineAdapter(onItemClick: (View, MedicineViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<MedicineViewItem, ItemMedicineBinding>(onItemClick) {

    override fun bind(binding: ItemMedicineBinding, viewType: Int, position: Int, item: MedicineViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_IMAGE_RES)) {
            refreshActionRes(binding, item)
        }

        if (payloads.contains(PAYLOAD_IMAGE_SHOW)) {
            refreshActionShow(binding, item)
        }

        if (payloads.contains(PAYLOAD_BACKGROUND_COLOR)) {
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

        binding.root.delegate.strokeColor = item.backgroundColor
    }
}

class MedicineViewItem(
    val id: String = "",

    var name: CharSequence = "",
    var desciption: CharSequence,

    val actionRes: Int,
    val actionShow: Boolean,

    val backgroundColor: Int
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        actionRes to PAYLOAD_IMAGE_RES,
        actionShow to PAYLOAD_IMAGE_SHOW,
        backgroundColor to PAYLOAD_BACKGROUND_COLOR
    )
}

private const val PAYLOAD_IMAGE_RES = "PAYLOAD_IMAGE_RES"
private const val PAYLOAD_IMAGE_SHOW = "PAYLOAD_IMAGE_RES"
private const val PAYLOAD_BACKGROUND_COLOR = "PAYLOAD_BACKGROUND_COLOR"
