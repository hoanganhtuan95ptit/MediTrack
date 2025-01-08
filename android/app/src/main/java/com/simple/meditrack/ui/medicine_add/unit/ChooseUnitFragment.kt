package com.simple.meditrack.ui.medicine_add.unit

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
import com.simple.core.utils.extentions.orDefault
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
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.entities.Medicine.Companion.toUnit
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.sendEvent

class ChooseUnitFragment : BaseViewModelSheetFragment<DialogListBinding, ChooseUnitViewModel>() {

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

            sendEvent(EventName.CHANGE_UNIT, item.data ?: return@TextAdapter)
            dismiss()
        }

        adapter = MultiAdapter(textAdapter).apply {

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

        arguments?.getInt(Param.ID).orDefault(Medicine.Unit.TABLET.value).toUnit()?.let {

            viewModel.updateUnit(it)
        }
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class ChooseUnitDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.CHOOSE_UNIT
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is MainActivity) return false

        val fragment = ChooseUnitFragment()
        fragment.arguments = extras
        fragment.show(activity.supportFragmentManager, "")

        return true
    }
}