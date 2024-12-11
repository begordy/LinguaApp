package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // Set a listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    findNavController().navigate(R.id.action_homePage_to_settings_fragment)
                    true
                }
                R.id.nav_home -> {
                    //do nothing
                    true
                }
                R.id.nav_favorites -> {
                    findNavController().navigate(R.id.action_homePage_to_favorites)
                    true
                }
                R.id.nav_search -> {
                    findNavController().navigate(R.id.action_homePage_to_search)
                    true
                }
                else -> false
            }
        }


        val samples: ArrayList<String> = ArrayList()

        for (i in 1..4) {
            samples.add("Card $i")
        }

        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }

        //Set listener for selecting each activity category
        val simplePhonologyButton = view.findViewById<Button>(R.id.simple_phonology)
        settingsViewModel.primaryColor.value?.let { simplePhonologyButton.setBackgroundColor(it) }
        simplePhonologyButton.setOnClickListener {
            //navigate to the problem selection page with simple phonology data (passed int = 1)
            findNavController().navigate(R.id.home_to_simple_phonology_exercise)
        }

        val complexPhonologyButton = view.findViewById<Button>(R.id.complex_phonology)
        settingsViewModel.primaryColor.value?.let { complexPhonologyButton.setBackgroundColor(it) }
        complexPhonologyButton.setOnClickListener {
            //navigate to the problem selection page with complex phonology data (passed int = 2)
            findNavController().navigate(R.id.home_to_complex_phonology_exercise)
        }

        val simpleSyntaxButton = view.findViewById<Button>(R.id.simple_syntax)
        settingsViewModel.primaryColor.value?.let { simpleSyntaxButton.setBackgroundColor(it) }
        simpleSyntaxButton.setOnClickListener {
            //navigate to the problem selection page with simple syntax data (passed int = 3)
            findNavController().navigate(R.id.home_to_simple_syntax_exercise)
        }

        val complexSyntaxButton = view.findViewById<Button>(R.id.complex_syntax)
        settingsViewModel.primaryColor.value?.let { complexSyntaxButton.setBackgroundColor(it) }
        complexSyntaxButton.setOnClickListener {
            //navigate to the problem selection page with complex syntax data (passed int = 4)
            findNavController().navigate(R.id.home_to_complex_syntax_exercise)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false)
    }

}