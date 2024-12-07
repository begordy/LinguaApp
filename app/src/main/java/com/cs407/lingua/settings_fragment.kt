package com.cs407.lingua

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.concurrent.TimeUnit


class settings_fragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Handle granted permission (e.g., enable notifications)
            } else {
                // Handle denied permission (e.g., disable notifications)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val notificationSwitch: Switch? = view?.findViewById(R.id.notification_switch)

        if (notificationSwitch != null) {
            notificationSwitch.isChecked = context?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() } == true
        }

        fun scheduleDailyNotification(context: Context) {
            val dailyNotificationRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                1, TimeUnit.DAYS // Periodic task, repeat each day
            )
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork("studyReminder", ExistingPeriodicWorkPolicy.KEEP,dailyNotificationRequest)
        }

        if(NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()){
            scheduleDailyNotification(requireContext())
        }else {
            context?.let { WorkManager.getInstance(it).cancelUniqueWork("studyReminder") }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.settings_menu, menu)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        val primary_spinner: Spinner = view.findViewById(R.id.primary_spinner)
        val secondary_spinner: Spinner = view.findViewById(R.id.secondary_spinner)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val easy: Switch = view.findViewById(R.id.easy_switch)
        val medium: Switch = view.findViewById(R.id.medium_switch)
        val hard: Switch = view.findViewById(R.id.hard_switch)
        val simple: Switch = view.findViewById(R.id.simple_switch)
        val complex: Switch = view.findViewById(R.id.complex_switch)
        val compound: Switch = view.findViewById(R.id.compound_switch)
        val vibSwitch: Switch = view.findViewById(R.id.vib_switch)
        val notificationSwitch: Switch = view.findViewById(R.id.notification_switch)

        easy.isChecked = settingsViewModel.easy.value == true
        medium.isChecked = settingsViewModel.medium.value == true
        hard.isChecked = settingsViewModel.hard.value == true
        simple.isChecked = settingsViewModel.simple.value == true
        complex.isChecked = settingsViewModel.complex.value == true
        compound.isChecked = settingsViewModel.compound.value == true
        vibSwitch.isChecked = settingsViewModel.vibAllowed.value == true
        notificationSwitch.isChecked = context?.let { NotificationManagerCompat.from(it).areNotificationsEnabled() } == true



        fun scheduleDailyNotification(context: Context) {
            val dailyNotificationRequest = PeriodicWorkRequest.Builder(
                NotificationWorker::class.java,
                1, TimeUnit.DAYS // Periodic task, repeat each day
            )
                .setInitialDelay(1, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork("studyReminder", ExistingPeriodicWorkPolicy.KEEP,dailyNotificationRequest)
        }

        if(NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()){
            scheduleDailyNotification(requireContext())
        }

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Check if notification permission is granted
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Request permission if not granted
                    Snackbar.make(requireView(), "Notification permission is Disabled.\nGo to settings?", Snackbar.LENGTH_LONG)
                        .setAction("Settings") {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }
                        .show()
                } else {
                    //notifications already granted
                    Snackbar.make(
                        requireView(),
                        "Notification permission is Enabled.\nGo to settings?",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }
                        .show()
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ){
                    Snackbar.make(requireView(), "Notification permission is Disabled.\nGo to settings?", Snackbar.LENGTH_LONG)
                        .setAction("Settings") {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }
                        .show()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Notification permission is Enabled.\nGo to settings?",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Settings") {
                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                            }
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }


        easy.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveEasySelection(true)
            } else{
                settingsViewModel.saveEasySelection(false)
            }
        }

        medium.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveMediumSelection(true)
            } else{
                settingsViewModel.saveMediumSelection(false)
            }
        }
        hard.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveHardSelection(true)
            } else {
                settingsViewModel.saveHardSelection(false)
            }
        }

        simple.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveSimpleSelection(true)
            } else {
                settingsViewModel.saveSimpleSelection(false)
            }
        }

        complex.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveComplexSelection(true)
            } else {
                settingsViewModel.saveComplexSelection(false)
            }
        }

        compound.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveCompoundSelection(true)
            } else {
                settingsViewModel.saveCompoundSelection(false)
            }
        }

        vibSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveVibSelection(true)
            } else {
                settingsViewModel.saveVibSelection(false)
            }
        }


        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }

        // Set the toolbar as the action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)

        // Change the toolbar title
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Settings"
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)

        // Clicking back arrow goes back
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //findNavController().navigate(R.id.action_settings_fragment_to_homePage)
        }

        // Creating dropdown options for colors
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.primary_options,  // Array defined in strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            primary_spinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.secondary_options,  // Array defined in strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            secondary_spinner.adapter = adapter
        }

        settingsViewModel.primaryColor.value?.let { color ->
            val selectedPosition = when (color) {
                Color.parseColor("#673AB7")-> 0 // Purple
                Color.RED -> 1 // Red
                Color.BLUE -> 2 // Blue
                else -> 0 // Default to Purple if color is not found
            }
            primary_spinner.setSelection(selectedPosition)
        }

        settingsViewModel.secondaryColor.value?.let { color ->
            val selectedPosition = when (color) {
                Color.WHITE-> 0 // White
                Color.LTGRAY -> 1 // Grey
                Color.YELLOW -> 2 // Yellow
                else -> 0 // Default to White if color is not found
            }
            secondary_spinner.setSelection(selectedPosition)
        }


        primary_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position) as String
                //implement for each primary color :)
                if(selectedItem.equals("Blue")){
                    settingsViewModel.savePrimaryColor(Color.BLUE)
                }
                if (selectedItem.equals("Purple")){
                    settingsViewModel.savePrimaryColor(Color.parseColor("#673AB7"))
                }
                if (selectedItem.equals("Red")){
                    settingsViewModel.savePrimaryColor(Color.RED)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where no item is selected
            }
        }
        secondary_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position) as String
                //implement for each primary color :)
                if(selectedItem.equals("White")){
                    settingsViewModel.saveSecondaryColor(Color.WHITE)
                }
                if (selectedItem.equals("Grey")){
                    settingsViewModel.saveSecondaryColor(Color.LTGRAY)
                }
                if (selectedItem.equals("Yellow")){
                    settingsViewModel.saveSecondaryColor(Color.YELLOW)
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // Handle case where no item is selected
            }
        }

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)

        // Set a listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    //do nothing
                    true
                }
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_settings_fragment_to_homePage)
                    true
                }
                R.id.nav_favorites -> {
                    findNavController().navigate(R.id.action_settings_fragment_to_favorites)
                    true
                }
                R.id.nav_search -> {
                    findNavController().navigate(R.id.action_settings_fragment_to_search)
                    true
                }
                else -> false
            }
        }
    }
}