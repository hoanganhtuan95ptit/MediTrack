package com.simple.meditrack.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
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
import com.simple.ai.english.ui.base.transition.TransitionFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.postValue
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.image.setImage
import com.simple.meditrack.Deeplink
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentNotificationBinding
import com.simple.meditrack.ui.notification.adapters.MedicineAdapter
import com.simple.meditrack.ui.notification.adapters.TextAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

        val medicineAdapter = MedicineAdapter { view, item ->

            viewModel.updateSelected(item.id)
        }

        adapter = MultiAdapter(TextAdapter(), medicineAdapter).apply {

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

            binding.framePhoto.setImage(it.image)
        }

        actionState.observe(viewLifecycleOwner) {

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
    }
}


@com.tuanha.deeplink.annotation.Deeplink
class CameraDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.NOTIFICATION
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is FragmentActivity) return false

        val fragment = NotificationFragment()
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