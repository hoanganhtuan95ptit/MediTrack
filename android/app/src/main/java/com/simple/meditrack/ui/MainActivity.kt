@file:Suppress("DEPRECATION")

package com.simple.meditrack.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.simple.coreapp.utils.ext.getViewModel
import com.simple.coreapp.utils.ext.launchCollect
import com.simple.coreapp.utils.ext.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.meditrack.Deeplink
import com.simple.meditrack.databinding.ActivityMainBinding
import com.simple.meditrack.ui.base.PageFragment
import com.simple.meditrack.ui.base.adapters.PagerAdapter
import com.simple.meditrack.utils.NavigationView
import com.simple.meditrack.utils.NavigationViewImpl
import com.simple.meditrack.utils.appTheme
import com.simple.meditrack.utils.sendDeeplink
import com.simple.meditrack.utils.setupSize
import com.simple.meditrack.utils.setupTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(),
    NavigationView by NavigationViewImpl() {

    private val mainViewModel: MainViewModel by lazy {
        getViewModel(this, MainViewModel::class)
    }

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding!!.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            window.setDecorFitsSystemWindows(false)

            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {

            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        }

        with(mainViewModel) {

            bottomInfo.asFlow().launchCollect(this@MainActivity) {

                val binding = binding ?: return@launchCollect

                binding.frameBottomBar.animate().cancel()
                binding.frameBottomBar.animate().translationY(if (it.isShow) 0f else binding.frameBottomBar.height * 1f)
                    .setDuration(350)
                    .start()
            }
        }

        setupSize(this)
        setupTheme(this)
        setupNavigation(this)

        setupBackPress()
        setupBottomBar()
        setupViewPager()
    }

    private fun setupBackPress() = onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {

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

    private fun setupBottomBar() {

        doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

            val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

            binding.frameBottomBar.updatePadding(bottom = heightNavigationBar)
        }

        val binding = binding ?: return


        val pageTransformer = object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                lifecycleScope.launch {

                    val theme = appTheme.first()

                    binding.ivAlarm.colorFilter = PorterDuffColorFilter(if (position == 0) theme.colorAccent else theme.colorOnSurface, PorterDuff.Mode.SRC_IN)
                    binding.ivMedicine.colorFilter = PorterDuffColorFilter(if (position == 1) theme.colorAccent else theme.colorOnSurface, PorterDuff.Mode.SRC_IN)
                }
            }
        }
        pageTransformer.onPageSelected(0)
        binding.viewPager.registerOnPageChangeCallback(pageTransformer)

        binding.ivAlarm.setDebouncedClickListener {

            binding.viewPager.setCurrentItem(0, false)
        }

        binding.ivAdd.setDebouncedClickListener {

            sendDeeplink(Deeplink.ADD_ALARM)
        }

        binding.ivMedicine.setDebouncedClickListener {

            binding.viewPager.setCurrentItem(1, false)
        }
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