package com.simple.meditrack.ui

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.FragmentManager
import com.simple.coreapp.ui.base.activities.BaseViewBindingActivity
import com.simple.meditrack.databinding.ActivityMainBinding
import com.simple.meditrack.ui.alarm_list.AlarmListFragment
import com.simple.meditrack.ui.base.PageFragment
import com.simple.meditrack.ui.base.adapters.PagerAdapter
import com.simple.meditrack.utils.NavigationView
import com.simple.meditrack.utils.NavigationViewImpl
import com.simple.meditrack.utils.setupTheme


class MainActivity : BaseViewBindingActivity<ActivityMainBinding>(),
    NavigationView by NavigationViewImpl() {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        setupTheme(this)
        setupNavigation(this)

        setupViewPager()
    }

    private fun setupViewPager() {

        val binding = binding ?: return

        binding.viewPager.setUserInputEnabled(false)
        binding.viewPager.adapter = PagerAdapter(fragmentManager = supportFragmentManager, lifecycle = lifecycle, listOf("1", "2"), {

            this
        }, {

            PageFragment(this)
        })
    }

    override fun getSupportFragmentManager(): FragmentManager {

        return super.getSupportFragmentManager().fragments.filterIsInstance<PageFragment>().getOrNull(binding?.viewPager?.currentItem ?: 0)?.childFragmentManager ?: super.getSupportFragmentManager()
    }
}