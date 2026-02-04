package com.gitz.dicodingevent.ui

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.databinding.ActivityMainBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.viewmodel.setting.SettingViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SettingViewModel by viewModels {
            ViewModelFactory.getInstance(
                Injection.provideRepository(this),
                Injection.provideSettingPreferences(this),
                Injection.provideWorkManager(this)
            )
        }

        viewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            val mode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        setupNavigation(navController)

        handleIntent(intent, navController)
    }

    private fun setupNavigation(navController: androidx.navigation.NavController) {
        binding.navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.navView.visibility =
                if (destination.id == R.id.navigation_event_detail) View.GONE
                else View.VISIBLE
        }
    }

    private fun handleIntent(intent: android.content.Intent?, navController: androidx.navigation.NavController) {
        intent?.let {
            val idFromNotif = it.getIntExtra("EVENT_ID", -1)
            if (idFromNotif != -1) {
                val bundle = Bundle().apply {
                    putInt("eventId", idFromNotif)
                }
                navController.navigate(R.id.navigation_event_detail, bundle)

                it.removeExtra("EVENT_ID")
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        handleIntent(intent, navHostFragment.navController)
    }
}