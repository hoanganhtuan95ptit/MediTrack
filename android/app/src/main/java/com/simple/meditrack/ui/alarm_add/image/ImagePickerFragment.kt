package com.simple.meditrack.ui.alarm_add.image

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.asFlow
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.base.dialogs.sheet.BaseViewModelSheetFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.EventName
import com.simple.meditrack.Param
import com.simple.meditrack.databinding.DialogListBinding
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.base.adapters.ImageAdapter
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.sendEvent

class ImagePickerFragment : BaseViewModelSheetFragment<DialogListBinding, ImagePickerViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightNavigationChange {

            val binding = binding ?: return@doOnHeightNavigationChange

            binding.root.updatePadding(bottom = it)
        }

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val textAdapter = TextAdapter { view, item ->

        }

        val imageAdapter = ImageAdapter { view, item ->

            sendEvent(EventName.CHANGE_IMAGE, item.id)
            dismiss()
        }

        adapter = MultiAdapter(textAdapter, imageAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.recyclerView.layoutManager = layoutManager
        }
    }

    private fun observeData() = with(viewModel) {

        viewItemList.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            binding.recyclerView.submitListAwait(it)

            val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
            binding.recyclerView.beginTransitionAwait(transition)
        }

        arguments?.getString(Param.IMAGE_PATH)?.let {

//            viewModel.updateUnit(it)
        }
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class ImagePickerDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.CHOOSE_IMAGE
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is MainActivity) return false

        val fragment = ImagePickerFragment()
        fragment.arguments = extras
        fragment.show(activity.supportFragmentManager, "")

        return true
    }
}