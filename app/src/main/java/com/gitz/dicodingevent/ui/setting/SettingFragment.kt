package com.gitz.dicodingevent.ui.setting

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gitz.dicodingevent.DailyEventWorker
import com.gitz.dicodingevent.data.local.pref.SettingPreferences
import com.gitz.dicodingevent.data.local.pref.dataStore
import com.gitz.dicodingevent.databinding.FragmentSettingBinding
import com.gitz.dicodingevent.di.Injection
import com.gitz.dicodingevent.viewmodel.ViewModelFactory
import com.gitz.dicodingevent.viewmodel.setting.SettingViewModel

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels {
        ViewModelFactory.getInstance(
            Injection.provideRepository(requireContext()),
            Injection.provideSettingPreferences(requireContext()),
            Injection.provideWorkManager(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        viewModel.getReminderSettings().observe(viewLifecycleOwner) { isActive ->
            binding.switchReminder.isChecked = isActive
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                checkNotificationPermission()
            } else {
                viewModel.saveReminderSetting(false)
            }
        }

        binding.btnTestNotification.setOnClickListener {
            testNotification()
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            viewModel.saveThemeSetting(isChecked)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.saveReminderSetting(true)
            } else {
                binding.switchReminder.isChecked = false
                Toast.makeText(
                    requireContext(),
                    "Permission denied. Notifications won't work.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.saveReminderSetting(true)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            viewModel.saveReminderSetting(true)
        }
    }

    private fun testNotification() {
        val testWorkRequest = OneTimeWorkRequestBuilder<DailyEventWorker>().build()
        WorkManager.getInstance(requireContext()).enqueue(testWorkRequest)
        Toast.makeText(requireContext(), "Notification test triggered!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}