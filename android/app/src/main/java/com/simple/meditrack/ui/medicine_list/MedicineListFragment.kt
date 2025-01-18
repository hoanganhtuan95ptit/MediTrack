package com.simple.meditrack.ui.medicine_list

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.adapters.EmptyAdapter
import com.simple.coreapp.ui.base.fragments.transition.TransitionFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.ext.updateMargin
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentPageListBinding
import com.simple.meditrack.ui.medicine_list.adapters.MedicineAdapter
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.exts.launchCollect
import com.simple.meditrack.utils.sendDeeplink

class MedicineListFragment : TransitionFragment<FragmentPageListBinding, MedicineListViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.root.updatePadding(top = heightStatusBar)
            binding.frameAdd.updateMargin(bottom = DP.DP_24 + DP.DP_56 + heightNavigationBar)
        }

        val binding = binding ?: return

        binding.frameAdd.setDebouncedClickListener {

            val transitionName = binding.frameAdd.transitionName

            val extras = bundleOf(
                Param.ID to "",
                Param.ROOT_TRANSITION_NAME to transitionName
            )

            sendDeeplink(Deeplink.ADD_MEDICINE, extras = extras, sharedElement = mapOf(transitionName to binding.frameAdd))
        }

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val medicineAdapter = MedicineAdapter { view, item ->

            val transitionName = view.transitionName

            val extras = bundleOf(
                Param.ID to item.id,
                Param.ROOT_TRANSITION_NAME to transitionName
            )

            val sharedElement = mapOf(
                transitionName to view
            )

            sendDeeplink(Deeplink.ADD_MEDICINE, extras = extras, sharedElement = sharedElement)
        }

        adapter = MultiAdapter(medicineAdapter, EmptyAdapter()).apply {

            setRecyclerView(binding.recyclerView)
        }
    }

    private fun observeData() = with(viewModel) {

        lockTransition(Tag.SCREEN.name, Tag.VIEW_ITEM.name)

        screenInfo.observe(viewLifecycleOwner) {

            val theme = theme.value ?: return@observe
            val binding = binding ?: return@observe

            binding.tvHeader.text = it.header
            binding.tvAction.text = it.action
            binding.ivAction.setColorFilter(theme.colorOnPrimary)

            unlockTransition(Tag.SCREEN.name)
        }

        medicineViewItemEvent.launchCollect(viewLifecycleOwner) { it, anim ->

            val binding = binding ?: return@launchCollect

            if (anim) {

                unlockTransition(Tag.VIEW_ITEM.name)

                awaitTransition()

                binding.recyclerView.submitListAwait(it)

                val transition = TransitionSet().addTransition(ChangeBounds().setDuration(350)).addTransition(Fade().setDuration(350))
                binding.recyclerView.beginTransitionAwait(transition)
            } else {

                binding.recyclerView.submitListAwait(it)

                unlockTransition(Tag.VIEW_ITEM.name)
            }
        }
    }

    private enum class Tag {

        SCREEN, VIEW_ITEM
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class MedicineViewDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.MEDICINE_LIST
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is FragmentActivity) return false

        val fragment = MedicineListFragment()
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