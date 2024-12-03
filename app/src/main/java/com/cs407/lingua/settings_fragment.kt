package com.cs407.lingua

import android.content.res.ColorStateList
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

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class settings_fragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

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
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val primary_spinner: Spinner = view.findViewById(R.id.primary_spinner)
        val secondary_spinner: Spinner = view.findViewById(R.id.secondary_spinner)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        val easy: Switch = view.findViewById(R.id.easy_switch)
        val medium: Switch = view.findViewById(R.id.medium_switch)
        val hard: Switch = view.findViewById(R.id.hard_switch)
        val simple: Switch = view.findViewById(R.id.simple_switch)
        val complex: Switch = view.findViewById(R.id.complex_switch)
        val compound: Switch = view.findViewById(R.id.compound_switch)
        val toastSwitch: Switch = view.findViewById(R.id.toast_switch)
        val notificationSwitch: Switch = view.findViewById(R.id.notification_switch)

        easy.isChecked = settingsViewModel.easy.value == true
        medium.isChecked = settingsViewModel.medium.value == true
        hard.isChecked = settingsViewModel.hard.value == true
        simple.isChecked = settingsViewModel.simple.value == true
        complex.isChecked = settingsViewModel.complex.value == true
        compound.isChecked = settingsViewModel.compound.value == true
        toastSwitch.isChecked = settingsViewModel.toastAllowed.value == true
        notificationSwitch.isChecked = settingsViewModel.notificationAllowed.value == true

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

        toastSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveToastSelection(true)
            } else {
                settingsViewModel.saveToastSelection(false)
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

        // Clicking back arrow goes back to home
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
                else -> 0 // Default to Red if color is not found
            }
            primary_spinner.setSelection(selectedPosition)
        }

        settingsViewModel.secondaryColor.value?.let { color ->
            val selectedPosition = when (color) {
                Color.WHITE-> 0 // White
                Color.LTGRAY -> 1 // Grey
                Color.YELLOW -> 2 // Yellow
                else -> 0 // Default to White
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