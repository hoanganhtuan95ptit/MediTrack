package com.simple.meditrack.ui.base.adapters

import android.text.InputType
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.entities.ViewItem
import com.simple.meditrack.databinding.ItemInputBinding
import com.simple.meditrack.ui.view.Background
import com.simple.meditrack.utils.exts.setBackground

open class InputAdapter(private val onInputChange: (View, InputViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<InputViewItem, ItemInputBinding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemInputBinding>? {

        val holder = super.createViewHolder(parent, viewType) ?: return null

        holder.binding.edtName.doAfterTextChanged {

            val item = getViewItem(holder.bindingAdapterPosition) ?: return@doAfterTextChanged
            if (!holder.binding.edtName.isFocused) return@doAfterTextChanged

            item.text = it.toString()

            onInputChange(holder.binding.edtName, item)
        }

        return holder
    }

    override fun bind(binding: ItemInputBinding, viewType: Int, position: Int, item: InputViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        item.text = binding.edtName.text.toString()
    }

    override fun bind(binding: ItemInputBinding, viewType: Int, position: Int, item: InputViewItem) {
        super.bind(binding, viewType, position, item)

        binding.edtName.hint = item.hint

        binding.edtName.setInputType(item.inputType)
        binding.edtName.setText(item.text)

        binding.root.delegate.setBackground(item.background)
    }
}

class InputViewItem(
    val id: String = "",

    val hint: CharSequence = "",
    val inputType: Int = InputType.TYPE_CLASS_TEXT,

    var text: CharSequence = "",

    var background: Background? = null,
) : ViewItem {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
    )
}