package com.simple.meditrack.ui.alarm_list

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asFlow
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionSet
import com.simple.adapter.MultiAdapter
import com.simple.ai.english.ui.base.transition.TransitionFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.DP
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.ext.updateMargin
import com.simple.coreapp.utils.extentions.beginTransitionAwait
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.doOnHeightStatusChange
import com.simple.coreapp.utils.extentions.submitListAwait
import com.simple.meditrack.Deeplink
import com.simple.meditrack.Param
import com.simple.meditrack.R
import com.simple.meditrack.databinding.FragmentAlarmListBinding
import com.simple.meditrack.ui.AlarmReceiver
import com.simple.meditrack.ui.alarm_list.adapters.AlarmAdapter
import com.simple.meditrack.ui.notification.NotificationActivity
import com.simple.meditrack.ui.notification.NotificationFragment
import com.simple.meditrack.utils.DeeplinkHandler
import com.simple.meditrack.utils.exts.launchCollect
import com.simple.meditrack.utils.sendDeeplink
import com.simple.state.ResultState
import java.util.Calendar

class AlarmListFragment : TransitionFragment<FragmentAlarmListBinding, AlarmListViewModel>() {

    private var adapter by autoCleared<MultiAdapter>()

    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (Settings.canDrawOverlays(requireContext())) {
            // Quyền được cấp
        } else {
            // Quyền bị từ chối
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->
//
//            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange
//
//            binding.root.updatePadding(top = heightStatusBar)
//            binding.frameAdd.updateMargin(bottom = DP.DP_16 + heightNavigationBar)
//        }

        val binding = binding ?: return

        binding.frameAdd.setDebouncedClickListener {

            val transitionName = binding.frameAdd.transitionName

            sendDeeplink(Deeplink.ADD_ALARM, extras = bundleOf(Param.ROOT_TRANSITION_NAME to transitionName), sharedElement = mapOf(transitionName to binding.frameAdd))
        }

        if (!Settings.canDrawOverlays(requireContext())) {

            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
            overlayPermissionLauncher.launch(intent)
        }

        setupRecyclerView()

        observeData()
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val alarmAdapter = AlarmAdapter { view, item ->

            val transitionName = view.transitionName

            val extras = bundleOf(
                Param.ID to item.data.id,
                Param.ROOT_TRANSITION_NAME to transitionName
            )

            val sharedElement = mapOf(
                transitionName to view
            )

            sendDeeplink(Deeplink.ADD_ALARM, extras = extras, sharedElement = sharedElement)
        }

        adapter = MultiAdapter(alarmAdapter).apply {

            setRecyclerView(binding.recyclerView)
        }
    }

    private fun observeData() = with(viewModel) {

        lockTransition(Tag.VIEW_ITEM.name)

        alarmState.observe(viewLifecycleOwner) { state ->

            if (state !is ResultState.Success) {

                return@observe
            }

            val alarmManager = requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager

            state.data.map {

                // Intent và PendingIntent
                val intent = Intent(requireActivity(), AlarmReceiver::class.java)
                intent.putExtra(Param.ID, it.id)
                val pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE)

                // Thiết lập thời gian
                val calendar: Calendar = Calendar.getInstance()
                calendar.setTimeInMillis(System.currentTimeMillis())
                calendar.set(Calendar.HOUR_OF_DAY, it.hour)
                calendar.set(Calendar.MINUTE, it.minute)
                calendar.set(Calendar.SECOND, 0)

                // Đặt lịch lặp lại hàng ngày
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    it.step * AlarmManager.INTERVAL_DAY,  // Thời gian lặp lại mỗi ngày
                    pendingIntent
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
                )
            }
        }

        alarmViewItemEvent.launchCollect(viewLifecycleOwner) { it, anim ->

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

        VIEW_ITEM
    }
}

@com.tuanha.deeplink.annotation.Deeplink
class NotificationViewDeeplink : DeeplinkHandler {

    override fun getDeeplink(): String {
        return Deeplink.ALARM_LIST
    }

    override suspend fun navigation(activity: ComponentActivity, deepLink: String, extras: Bundle?, sharedElement: Map<String, View>?): Boolean {

        if (activity !is FragmentActivity) return false

        val fragment = AlarmListFragment()
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