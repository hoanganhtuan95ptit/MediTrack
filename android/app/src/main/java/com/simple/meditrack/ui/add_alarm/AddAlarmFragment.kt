package com.simple.meditrack.ui.add_alarm

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
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.get
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.EventName
import com.simple.meditrack.Id
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentListBinding
import com.simple.meditrack.entities.Alarm
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.add_alarm.adapters.AlarmMedicineAdapter
import com.simple.meditrack.ui.add_alarm.adapters.AlarmTimeAdapter
import com.simple.meditrack.ui.base.adapters.InputAdapter
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.doListenerEvent
import com.simple.meditrack.utils.exts.setBackground
import com.simple.meditrack.utils.sendDeeplink

class AddAlarmFragment : TransitionFragment<FragmentListBinding, AddAlarmViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        doListenerEvent(lifecycle, EventName.TIME) {

            val hour = it.asObject<Bundle>().getInt(Param.HOUR)
            val minute = it.asObject<Bundle>().getInt(Param.MINUTE)

            viewModel.updateTime(hour, minute)
        }

        doListenerEvent(lifecycle, EventName.ADD_MEDICINE) {

            val medicine = it.asObject<Alarm.MedicineItem>()

            viewModel.updateMedicine(medicine)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.root.updatePadding(top = heightStatusBar, bottom = heightNavigationBar)
        }

        val binding = binding ?: return

        binding.tvAction.setDebouncedClickListener {

        }

        binding.frameHeader.ivBack.setDebouncedClickListener {

            parentFragmentManager.popBackStack()
        }

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val textAdapter = TextAdapter { view, item ->

            if (item.id == Id.ADD_MEDICINE) {

                sendDeeplink(Deeplink.ADD_MEDICINE)
            }
        }

        val inputAdapter = InputAdapter { view, inputViewItem ->

            viewModel.refreshButtonInfo()
        }

        val alarmTimeAdapter = AlarmTimeAdapter(
            onTimeClick = { view, item ->

                sendDeeplink(Deeplink.PICK_TIME, extras = bundleOf(Param.HOUR to viewModel.hour.get(), Param.MINUTE to viewModel.minute.get()))
            },
            onImageClick = { view, item ->

            }
        )

        val alarmMedicineAdapter = AlarmMedicineAdapter(
            onRemoveClick = { view, item ->

                viewModel.removeMedicine(item)
            },
            onItemClick = { view, item ->

                sendDeeplink(Deeplink.ADD_MEDICINE, extras = bundleOf(Param.MEDICINE to item.data))
            }
        )

        adapter = MultiAdapter(textAdapter, inputAdapter, alarmTimeAdapter, alarmMedicineAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.recyclerView.layoutManager = layoutManager
        }
    }

    private fun observeData() = with(viewModel) {

        lockTransition(Tag.TITLE.name, Tag.BUTTON.name, Tag.VIEW_ITEM.name)

        title.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.frameHeader.tvTitle.text = it

            unlockTransition(Tag.TITLE.name)
        }

        buttonInfo.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            binding.tvAction.text = it.title

            binding.tvAction.delegate.setBackground(it.background)

            unlockTransition(Tag.BUTTON.name)
        }

        viewItemList.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            awaitTransition()

            binding.recyclerView.submitListAwait(it)

            val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
            binding.recyclerView.beginTransitionAwait(transition)

            unlockTransition(Tag.VIEW_ITEM.name)
        }
    }

    private enum class Tag {

        TITLE, BUTTON, VIEW_ITEM
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class AddMedicineViewDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.ADD_ALARM
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is MainActivity) return false

        val fragment = AddAlarmFragment()
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