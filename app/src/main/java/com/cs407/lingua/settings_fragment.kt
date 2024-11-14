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
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat.recreate
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView


class settings_fragment : Fragment() {



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
        val primary_spinner: Spinner = view.findViewById(R.id.primary_spinner)
        val secondary_spinner: Spinner = view.findViewById(R.id.secondary_spinner)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)


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

        primary_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position) as String
                //implement for each primary color :)
                if(selectedItem.equals("Blue")){
                    Toast.makeText(context, "Blue Selected", Toast.LENGTH_SHORT).show()

                }
                // Do something with the selected item
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