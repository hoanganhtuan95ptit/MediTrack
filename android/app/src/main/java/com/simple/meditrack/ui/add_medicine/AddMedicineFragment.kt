package com.simple.meditrack.ui.add_medicine

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.lifecycle.asFlow
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.simple.adapter.MultiAdapter
import com.simple.ai.english.ui.base.transition.TransitionFragment
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getSerializableOrNull
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.getOrEmpty
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.EventName
import com.simple.meditrack.Id
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentListBinding
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.base.adapters.CheckboxAdapter
import com.simple.meditrack.ui.base.adapters.InputAdapter
import com.simple.meditrack.ui.base.adapters.InputViewItem
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.ui.base.adapters.TextViewItem
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.doListenerEvent
import com.simple.meditrack.utils.sendDeeplink
import com.simple.meditrack.utils.sendEvent
import java.util.UUID

class AddMedicineFragment : TransitionFragment<FragmentListBinding, AddMedicineViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.root.updatePadding(top = heightStatusBar, bottom = heightNavigationBar)
        }

        val binding = binding ?: return

        binding.tvAction.setDebouncedClickListener {

            val viewItemList = viewModel.viewItemList.getOrEmpty()

            val texts = viewItemList.filterIsInstance<TextViewItem>()
            val inputs = viewItemList.filterIsInstance<InputViewItem>()


            val medicine = Alarm.MedicineItem(
                id = viewModel.medicineItem.value?.id ?: UUID.randomUUID().toString(),
                dosage = inputs.find { it.id == Id.DOSAGE }?.text?.toString().orEmpty().toDoubleOrNull() ?: 0.0,
                medicine = Medicine(
                    id = viewModel.medicineItem.value?.medicine?.id ?: UUID.randomUUID().toString(),
                    name = inputs.find { it.id == Id.NAME }?.text?.toString().orEmpty(),
                    image = "",
                    unit = texts.find { it.id == Id.UNIT }?.data.asObject<Medicine.Unit>().value,
                    note = inputs.find { it.id == Id.NOTE }?.text?.toString().orEmpty(),
                    quantity = inputs.find { it.id == Id.NAME }?.text?.toString().orEmpty().toDoubleOrNull() ?: Medicine.UNLIMITED
                )
            )

            sendEvent(EventName.ADD_MEDICINE, medicine)

            parentFragmentManager.popBackStack()
        }

        binding.frameHeader.ivBack.setDebouncedClickListener {

            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        doListenerEvent(viewLifecycleOwner.lifecycle, EventName.CHANGE_UNIT) {

            viewModel.updateUnit(it.asObject<Medicine.Unit>())
        }

        val textAdapter = TextAdapter { view, item ->

            if (item.id == Id.UNIT) {

                sendDeeplink(Deeplink.CHOOSE_UNIT, extras = bundleOf(Param.ID to item.data.asObject<Medicine.Unit>().value))
            }
        }

        val inputAdapter = InputAdapter { view, inputViewItem ->

            viewModel.refreshButtonInfo()
        }

        val checkboxAdapter = CheckboxAdapter { view, item ->

            viewModel.switchLowOnMedication()
        }

        adapter = MultiAdapter(textAdapter, inputAdapter, checkboxAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.recyclerView.layoutManager = layoutManager
        }
    }

    private fun observeData() = with(viewModel) {

        title.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.frameHeader.tvTitle.text = it
        }

        buttonInfo.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            binding.tvAction.text = it.title

            it.background.backgroundColor?.let {
                binding.tvAction.delegate.backgroundColor = it
            }
        }

        viewItemList.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            awaitTransition()

            binding.recyclerView.submitListAwait(it)

            val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
            binding.recyclerView.beginTransitionAwait(transition)
        }

        arguments?.getSerializableOrNull<Alarm.MedicineItem>(Param.MEDICINE)?.let {

            viewModel.updateMedicine(it)
        }
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class AddMedicineViewDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.ADD_MEDICINE
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is MainActivity) return false

        val fragment = AddMedicineFragment()
        fragment.arguments = extras

        val fragmentTransaction = activity.supportFragmentManager
            .beginTransaction()

        sharedElement?.forEach { (t, u) ->

            fragmentTransaction.addSharedElement(u, t)
        }

        fragmentTransaction
            .replace(R.id.fragment_container, fragment, "")
            .addToBackStack("")
            .commit()

        return true
    }
}