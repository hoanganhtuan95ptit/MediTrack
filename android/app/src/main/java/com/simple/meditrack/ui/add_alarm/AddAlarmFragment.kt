package com.simple.meditrack.ui.add_alarm

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.Window
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
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.getStringOrEmpty
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.ext.top
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
import com.simple.meditrack.ui.add_alarm.adapters.ImageAdapter
import com.simple.meditrack.ui.base.adapters.InputAdapter
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.doListenerEvent
import com.simple.meditrack.utils.exts.launchCollect
import com.simple.meditrack.utils.exts.setBackground
import com.simple.meditrack.utils.sendDeeplink
import com.simple.state.doSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


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

        doListenerEvent(lifecycle, EventName.CHANGE_IMAGE) {

            val imagePath = it.asObject<String>()

            viewModel.updateImage(imagePath)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.root.updatePadding(top = heightStatusBar)
            binding.frameAction.updatePadding(bottom = heightNavigationBar)
        }

        val binding = binding ?: return

        binding.tvAction.setDebouncedClickListener {

            viewModel.insertOrUpdateAlarm()
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

            val transitionName = view.transitionName

            if (item.id == Id.ADD_MEDICINE) {

                sendDeeplink(Deeplink.ADD_MEDICINE, extras = bundleOf(Param.ROOT_TRANSITION_NAME to transitionName), sharedElement = mapOf(transitionName to view))
            } else if (item.id == Id.TIME) {

                sendDeeplink(Deeplink.PICK_TIME, extras = bundleOf(Param.HOUR to viewModel.hour.get(), Param.MINUTE to viewModel.minute.get()))
            }
        }

        val inputAdapter = InputAdapter { view, inputViewItem ->

            viewModel.refreshButtonInfo()
        }

        val imageAdapter = ImageAdapter { view, item ->

            sendDeeplink(Deeplink.CHOOSE_IMAGE)
        }

        val alarmMedicineAdapter = AlarmMedicineAdapter(
            onRemoveClick = { view, item ->

                viewModel.removeMedicine(item)
            },
            onItemClick = { view, item ->

                val transitionName = view.transitionName

                sendDeeplink(Deeplink.ADD_MEDICINE, extras = bundleOf(Param.MEDICINE to item.data, Param.ROOT_TRANSITION_NAME to transitionName), sharedElement = mapOf(transitionName to view))
            }
        )

        adapter = MultiAdapter(textAdapter, inputAdapter, imageAdapter, alarmMedicineAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

            binding.recyclerView.setItemViewCacheSize(20)
            binding.recyclerView.setDrawingCacheEnabled(true)
            binding.recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH)

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.recyclerView.layoutManager = layoutManager
        }

        channelFlow {

            var viewFocus: View? = null
            var showKeyboard = false

            binding.root.viewTreeObserver.addOnGlobalLayoutListener {

                if (showKeyboard && viewFocus != null) {

                    trySend(viewFocus!!)
                }
            }

            binding.root.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus, newFocus ->

                viewFocus = newFocus

                if (showKeyboard && viewFocus != null) {

                    trySend(viewFocus!!)
                }
            }

            setEventListener(requireActivity(), viewLifecycleOwner, KeyboardVisibilityEventListener {

                showKeyboard = it

                if (showKeyboard && viewFocus != null) {

                    trySend(viewFocus!!)
                }
            })

            awaitClose {

            }
        }.launchCollect(viewLifecycleOwner) {

            val rect = Rect()
            requireActivity().window.decorView.getWindowVisibleDisplayFrame(rect)

            val y = it.top(binding.recyclerView.id) - (rect.height() - binding.recyclerView.top(binding.rootList.id)) + DP.DP_16

            binding.recyclerView.smoothScrollBy(0, y)
        }
    }

    private fun observeData() = with(viewModel) {

        lockTransition(Tag.TITLE.name, Tag.BUTTON.name)

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

        viewItemListEvent.launchCollect(viewLifecycleOwner) { list, anim ->

            val binding = binding ?: return@launchCollect

            if (anim) {

                unlockTransition(Tag.VIEW_ITEM.name)

                awaitTransition()

                binding.recyclerView.submitListAwait(list)

                val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
                binding.recyclerView.beginTransitionAwait(transition)
            } else {

                binding.recyclerView.submitListAwait(list)

                unlockTransition(Tag.VIEW_ITEM.name)
            }
        }

        insertOrUpdateState.observe(viewLifecycleOwner) {

            it.doSuccess {
                parentFragmentManager.popBackStack()
            }
        }

        arguments.getStringOrEmpty(Param.ID).let {

            viewModel.updateId(it)
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