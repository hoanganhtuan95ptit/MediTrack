package com.simple.meditrack.ui.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStateAtLeast
import com.simple.coreapp.utils.ext.getViewModel
import com.simple.meditrack.Deeplink
import com.simple.meditrack.databinding.FragmentPageBinding
import com.simple.meditrack.ui.MainViewModel
import com.simple.meditrack.ui.base.transition.TransitionFragment
import com.simple.meditrack.ui.base.transition.TransitionViewModel
import com.simple.meditrack.utils.sendDeeplink
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PageFragment(val index: String) : TransitionFragment<FragmentPageBinding, PageViewModel>() {

    private val mainViewModel: MainViewModel by lazy {
        getViewModel(requireActivity(), MainViewModel::class)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {

            channelFlow {

                viewLifecycleOwner.withStateAtLeast(Lifecycle.State.RESUMED) {

                    trySend(Unit)
                }

                awaitClose {

                }
            }.first()

            if (index == "1") {
                sendDeeplink(Deeplink.ALARM_LIST)
            } else {
                sendDeeplink(Deeplink.MEDICINE_LIST)
            }
        }

        childFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {

            override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
                super.onFragmentAttached(fm, f, context)
                updateBottomStatus()
            }

            override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
                super.onFragmentDetached(fm, f)
                updateBottomStatus()
            }
        }, true)
    }

    override fun onResume() {
        super.onResume()

        updateBottomStatus()
    }

    override fun onPause() {
        super.onPause()

        updateBottomStatus(MainViewModel.BottomStatus.HIDE)
    }

    private fun updateBottomStatus(status: MainViewModel.BottomStatus? = null) {

        val _status = status ?: if (childFragmentManager.backStackEntryCount > 0) {

            MainViewModel.BottomStatus.HIDE
        } else {

            MainViewModel.BottomStatus.SHOW
        }

        mainViewModel.updateBottomStatus(index, _status)
    }
}

class PageViewModel : TransitionViewModel()