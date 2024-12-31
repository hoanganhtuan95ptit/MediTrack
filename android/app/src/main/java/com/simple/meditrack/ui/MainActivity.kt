package com.simple.meditrack.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.meditrack.databinding.ActivityMainBinding
import com.simple.meditrack.ui.base.PageFragment
import com.simple.meditrack.ui.base.adapters.PagerAdapter
import com.simple.meditrack.utils.NavigationView
import com.simple.meditrack.utils.NavigationViewImpl
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.setupTheme


class MainActivity : AppCompatActivity(),
    NavigationView by NavigationViewImpl() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))

        setContentView(binding!!.root)

        setupTheme(this)
        setupNavigation(this)

        appTheme.launchCollect(this) {
            window.statusBarColor = Color.parseColor(("#ECEEFA"))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {

                val binding = binding ?: return

                if (supportFragmentManager.backStackEntryCount > 0) {

                    supportFragmentManager.popBackStack()
                } else if (binding.viewPager.currentItem != 0) {

                    binding.viewPager.currentItem = 0
                } else {

                    finish()
                }
            }
        })

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