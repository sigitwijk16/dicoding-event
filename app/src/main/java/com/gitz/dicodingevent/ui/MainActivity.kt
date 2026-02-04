package com.gitz.dicodingevent.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.gitz.dicodingevent.R
import com.gitz.dicodingevent.databinding.ActivityMainBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.viewmodel.event.EventViewModel
import com.gitz.dicodingevent.viewmodel.setting.SettingViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val settingViewModel: SettingViewModel by viewModels {
        ViewModelFactory.getInstance(Injection.provideRepository(this), Injection.provideSettingPreferences(this), Injection.provideWorkManager(this))
    }

    private val eventViewModel: EventViewModel by viewModels {
        ViewModelFactory.getInstance(Injection.provideRepository(this), Injection.provideSettingPreferences(this), Injection.provideWorkManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeTheme()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        setupSearchLogic()
        setupNavigation(navController)
        handleIntent(intent, navController)
        setupBackPress(navController)
    }

    override fun dispatchTouchEvent(ev: android.view.MotionEvent?): Boolean {
        if (ev?.action == android.view.MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is android.widget.EditText) {
                val outRect = android.graphics.Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun observeTheme() {
        settingViewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            val mode = if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }

    private fun setupSearchLogic() {
        with(binding) {
            searchEditText.addTextChangedListener(object : TextWatcher {
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = s.toString().trim()

                    eventViewModel.setSearchQuery(query)

                    btnClearSearch.isVisible = query.isNotEmpty()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun afterTextChanged(s: Editable?) {}
            })

            btnClearSearch.setOnClickListener {
                searchEditText.text.clear()
            }

            searchEditText.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                    true
                } else false
            }

            searchEditText.setOnFocusChangeListener { _, hasFocus ->
                binding.navContainer.isVisible = !hasFocus
            }
        }
    }

    private fun setupNavigation(navController: NavController) {
        binding.navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isSearchable = destination.id == R.id.navigation_active_events || destination.id == R.id.navigation_inactive_events
            binding.searchCard.isVisible = isSearchable
            binding.topDivider.isVisible = isSearchable

            binding.navContainer.isVisible = destination.id != R.id.navigation_event_detail
        }
    }

    private fun setupBackPress(navController: NavController) {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id == R.id.navigation_active_events) {
                    showExitDialog()
                } else if (binding.searchEditText.hasFocus()) {
                    binding.searchEditText.clearFocus()
                    binding.navContainer.isVisible = true
                } else {
                    if (!navController.navigateUp()) showExitDialog()
                }
            }
        })
    }

    private fun showExitDialog() {
        com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to close the app?")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Exit") { _, _ -> finish() }
            .show()
    }

    private fun handleIntent(intent: Intent?, navController: NavController) {
        intent?.let {
            val idFromNotif = it.getIntExtra("EVENT_ID", -1)
            if (idFromNotif != -1) {
                navController.navigate(R.id.navigation_event_detail, bundleOf("eventId" to idFromNotif))
                it.removeExtra("EVENT_ID")
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        handleIntent(intent, navHostFragment.navController)
    }
}