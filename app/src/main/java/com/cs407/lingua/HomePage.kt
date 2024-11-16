package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomePage : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)

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

        //Set listener for selecting each activity category
        val simpleSyntaxButton = view.findViewById<Button>(R.id.simple_syntax)
        simpleSyntaxButton.setOnClickListener {
            //navigate to the problem selection page with simple syntax data (passed int = 1)
            findNavController().navigate(R.id.home_to_simple_syntax_exercise)
        }

        val compoundSyntaxButton = view.findViewById<Button>(R.id.compound_syntax)
        compoundSyntaxButton.setOnClickListener {
            //navigate to the problem selection page with compound syntax data (passed int = 2)
            findNavController().navigate(R.id.home_to_compound_syntax_exercise)
        }

        val complexSyntaxButton = view.findViewById<Button>(R.id.complex_syntax)
        complexSyntaxButton.setOnClickListener {
            //navigate to the problem selection page with complex syntax data (passed int = 3)
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