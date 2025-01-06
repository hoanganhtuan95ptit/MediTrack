package com.simple.meditrack.ui.base.adapters

import android.view.View
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.coreapp.utils.ext.setVisible
import com.simple.coreapp.utils.ext.updateMargin
import com.simple.image.setImage
import com.simple.meditrack.databinding.ItemTitleBinding
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.ui.view.Margin
import com.simple.meditrack.ui.view.Padding
import com.simple.meditrack.ui.view.Size
import com.simple.meditrack.ui.view.TextStyle
import com.simple.meditrack.utils.exts.setBackground
import com.simple.meditrack.utils.exts.setTextStyle

open class TextAdapter(onItemClick: (View, TextViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<TextViewItem, ItemTitleBinding>(onItemClick) {

    override fun bind(binding: ItemTitleBinding, viewType: Int, position: Int, item: TextViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(TEXT)) {
            refreshText(binding, item)
        }
    }

    override fun bind(binding: ItemTitleBinding, viewType: Int, position: Int, item: TextViewItem) {
        super.bind(binding, viewType, position, item)

        binding.root.transitionName = item.id

        item.size?.let {

            binding.root.updateLayoutParams {
                width = it.width
                height = it.height
            }
        }


        item.image?.end?.let {
            binding.ivEnd.setImage(it)
        }
        binding.ivEnd.setVisible(item.image?.end != null)

        item.image?.start?.let {
            binding.ivStart.setImage(it)
        }
        binding.ivStart.setVisible(item.image?.start != null)


        item.margin?.let {
            binding.root.updateMargin(left = it.left, top = it.top, right = it.right, bottom = it.bottom)
        }

        item.padding?.let {
            binding.root.updatePadding(left = it.left, top = it.top, right = it.right, bottom = it.bottom)
        }

        binding.tvTitle.setTextStyle(item.textStyle)
        binding.root.delegate.setBackground(item.background)

        refreshText(binding, item)
    }

    private fun refreshText(binding: ItemTitleBinding, item: TextViewItem) {

        binding.tvTitle.text = item.text
    }
}

data class TextViewItem(
    val id: String = "",
    val data: Any? = null,

    var text: CharSequence = "",

    val size: Size? = null,
    val image: Image? = null,
    val margin: Margin? = null,
    val padding: Padding? = null,
    var textStyle: TextStyle? = null,
    var background: Background? = null
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        text to TEXT
    )

    data class Image(
        val end: Int? = null,
        val start: Int? = null
    )
}

private const val TEXT = "TEXT"