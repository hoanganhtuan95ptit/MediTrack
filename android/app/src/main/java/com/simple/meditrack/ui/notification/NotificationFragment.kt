package com.simple.meditrack.ui.notification

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.updatePadding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.ncorti.slidetoact.SlideToActView
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.adapters.TextAdapter
import com.simple.coreapp.ui.base.fragments.transition.TransitionFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.image.setImage
import com.simple.meditrack.Deeplink
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentNotificationBinding
import com.simple.meditrack.ui.notification.adapters.NotificationMedicineAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.NotificationUtils
import com.simple.meditrack.utils.RingtoneUtils
import com.simple.state.doSuccess
import com.simple.state.toSuccess
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class NotificationFragment : TransitionFragment<FragmentNotificationBinding, NotificationViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    private val resetState = MutableLiveData(1)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.root.updatePadding(top = heightStatusBar)
        }

        setupRecycleView()
        setupSlideToActView()

        observeData()
    }

    private fun setupRecycleView() {

        val binding = binding ?: return

        val notificationMedicineAdapter = NotificationMedicineAdapter { view, item ->

            viewModel.updateSelected(item.id)
        }

        adapter = MultiAdapter(TextAdapter(), notificationMedicineAdapter).apply {

            binding.recyclerView.adapter = this
            binding.recyclerView.itemAnimator = null

            val layoutManager = FlexboxLayoutManager(context)
            layoutManager.justifyContent = JustifyContent.FLEX_START
            binding.recyclerView.layoutManager = layoutManager
        }
    }

    private fun setupSlideToActView() {

        val binding = binding ?: return

        binding.slideToActView.onSlideToActAnimationEventListener = object : SlideToActView.OnSlideToActAnimationEventListener {

            override fun onSlideCompleteAnimationEnded(view: SlideToActView) {

                viewModel.nextAction()
            }

            override fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float) {

                resetState.postValue(null)
            }

            override fun onSlideResetAnimationEnded(view: SlideToActView) {

                resetState.postValue(1)
            }

            override fun onSlideResetAnimationStarted(view: SlideToActView) {
            }
        }
    }

    private fun observeData() = with(viewModel) {


        titleInfo.observe(viewLifecycleOwner) {

            val binding = binding ?: return@observe

            binding.tvName.text = it.title
            binding.tvDescription.text = it.note

            binding.ivPhoto.setImage(it.image)
        }

        alarmState.observe(viewLifecycleOwner) { state ->

            if (arguments?.getBoolean(Param.FROM_NOTIFICATION) != true) state.doSuccess {

                NotificationUtils.sendNotification(requireContext(), it)
            }
        }

        actionState.observe(viewLifecycleOwner) {

            val alarm = alarmState.value?.toSuccess()?.data

            if (arguments?.getBoolean(Param.FROM_NOTIFICATION) != true && it == NotificationViewModel.ActionState.NOT_VIEW) {

                RingtoneUtils.play()
            }

            if (it == NotificationViewModel.ActionState.VIEWED) {

                RingtoneUtils.stop()
            }

            if (alarm != null && it == NotificationViewModel.ActionState.VIEWED) {

                NotificationUtils.cancelNotification(alarm.idInt)
            }

            if (it == NotificationViewModel.ActionState.DONE) {

                activity?.finish()
            }
        }

        buttonInfo.asFlow().launchCollect(viewModelScope) {

            val binding = binding ?: return@launchCollect

            binding.slideToActView.text = it.title
            binding.slideToActView.isLocked = it.isLocked
            binding.slideToActView.outerColor = it.outerColor
            binding.slideToActView.setCompleted(completed = it.completed, withAnimation = true)
        }

        viewItemList.asFlow().launchCollect(viewModelScope) {

            val binding = binding ?: return@launchCollect

            awaitTransition()

            resetState.asFlow().filterNotNull().first()

            binding.recyclerView.submitListAwait(it)

            val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
            binding.recyclerView.beginTransitionAwait(transition)
        }

        arguments?.getString(Param.ID)?.let {

            viewModel.updateId(it)
        }
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class NotificationViewDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.NOTIFICATION_VIEW
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is NotificationActivity) return false

        val fragment = NotificationFragment()
        fragment.arguments = extras

        val fragmentTransaction = activity.supportFragmentManager
            .beginTransaction()

        sharedElement?.forEach { (t, u) ->

            fragmentTransaction.addSharedElement(u, t)
        }

        fragmentTransaction
            .add(R.id.fragment_container, fragment, "")
            .commit()

        return true
    }
}