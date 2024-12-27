package com.simple.meditrack.ui.notification.adapters

import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.widget.TextViewCompat
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.meditrack.databinding.ItemTitleBinding

open class TextAdapter(onItemClick: (View, TextViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<TextViewItem, ItemTitleBinding>(onItemClick) {

    override fun bind(binding: ItemTitleBinding, viewType: Int, position: Int, item: TextViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(TEXT)) {
            refreshText(binding, item)
        }
    }

    override fun bind(binding: ItemTitleBinding, viewType: Int, position: Int, item: TextViewItem) {
        super.bind(binding, viewType, position, item)

        item.textSize?.let {
            binding.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
        }

        item.textGravity?.let {
            binding.tvTitle.gravity = it
        }

        refreshText(binding, item)
    }

    private fun refreshText(binding: ItemTitleBinding, item: TextViewItem) {

        binding.tvTitle.text = item.text
    }
}

class TextViewItem(
    val id: String = "",

    var text: CharSequence = "",
    var textSize: Float? = null,
    var textGravity: Int? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        text to TEXT
    )
}

private const val TEXT = "TEXT"