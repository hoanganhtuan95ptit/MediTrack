package com.simple.meditrack.ui.medicine_add

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.simple.adapter.MultiAdapter
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.bottom
import com.simple.coreapp.utils.ext.getStringOrEmpty
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.ext.setVisible
import com.simple.coreapp.utils.ext.top
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.EventName
import com.simple.meditrack.Id
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentListBinding
import com.simple.meditrack.entities.Medicine
import com.simple.meditrack.ui.MainActivity
import com.simple.meditrack.ui.base.adapters.CheckboxAdapter
import com.simple.meditrack.ui.base.adapters.ImageAdapter
import com.simple.meditrack.ui.base.adapters.InputAdapter
import com.simple.meditrack.ui.base.adapters.TextAdapter
import com.simple.meditrack.ui.base.transition.TransitionFragment
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.doListenerEvent
import com.simple.meditrack.utils.exts.setBackground
import com.simple.meditrack.utils.sendDeeplink
import com.simple.state.doSuccess
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent.setEventListener
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


class AddMedicineFragment : TransitionFragment<FragmentListBinding, AddMedicineViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        childFragmentManager.setFragmentResultListener("DELETE", viewLifecycleOwner) { keyRequest, a ->

            viewModel.deleteAlarm()
            parentFragmentManager.popBackStack()
        }

        val binding = binding ?: return

        binding.tvAction.setDebouncedClickListener {

            viewModel.insertOrUpdateMedicine()
        }

        binding.tvAction1.setDebouncedClickListener {

            val translate = viewModel.translate.value ?: return@setDebouncedClickListener

            viewLifecycleOwner.lifecycleScope.launch {

                awaitConfirm(
                    fragmentManager = childFragmentManager,

                    isCancel = true,

                    image = R.drawable.img_delete,

                    positive = translate["Xóa"].orEmpty(),
                    negative = translate["Đóng"].orEmpty(),

                    keyRequestPositive = "DELETE",

                    title = translate["Xóa thông báo"].orEmpty(),
                    message = translate["Bạn có chắc chắn muốn xóa thông báo không"].orEmpty(),
                )
            }
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

        val inputAdapter = InputAdapter(
            onInputFocus = { view, item ->

            },
            onInputChange = { view, item ->

                viewModel.refreshButtonInfo()
            }
        )

        val imageAdapter = ImageAdapter { view, item ->

            sendDeeplink(Deeplink.CHOOSE_IMAGE)
        }

        val checkboxAdapter = CheckboxAdapter { view, item ->

            viewModel.switchLowOnMedication()
        }

        adapter = MultiAdapter(textAdapter, inputAdapter, imageAdapter, checkboxAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

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

            val y = it.bottom(binding.recyclerView.id) - (rect.height() - binding.recyclerView.top(binding.rootList.id)) + DP.DP_16

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

            binding.tvAction.text = it.action0.text
            binding.tvAction.isClickable = it.action0.isClicked
            binding.tvAction.setVisible(it.action0.isShow)
            binding.tvAction.delegate.setBackground(it.action0.background)

            binding.tvAction1.text = it.action1.text
            binding.tvAction1.isClickable = it.action1.isClicked
            binding.tvAction1.setVisible(it.action1.isShow)
            binding.tvAction1.delegate.setBackground(it.action1.background)

            unlockTransition(Tag.BUTTON.name)
        }

        viewItemList.asFlow().launchCollect(viewLifecycleOwner) {

            val binding = binding ?: return@launchCollect

            awaitTransition()

            binding.recyclerView.submitListAwait(it)

            val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
            binding.recyclerView.beginTransitionAwait(transition)
        }

        insertOrUpdateState.observe(viewLifecycleOwner) {

            it.doSuccess {
                parentFragmentManager.popBackStack()
            }
        }

        arguments.getStringOrEmpty(Param.ID).let {

            viewModel.updateMedicineId(it)
        }
    }

    private enum class Tag {

        TITLE, BUTTON
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