package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class search : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    lateinit var searchView: SearchView
    lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchView = view.findViewById(R.id.search_bar)
        listView = view.findViewById(R.id.list)

        listView.visibility = View.GONE

        val arr = ArrayList<String>()
        arr.add("Monday")
        arr.add("Tuesday")
        arr.add("Wednesday")
        arr.add("Thursday")
        arr.add("Friday")
        arr.add("Saturday")
        arr.add("Sunday")



        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arr)
        listView.adapter = adapter

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String?): Boolean {
                    listView.visibility = View.VISIBLE
                    adapter.filter.filter(s)
                    return false
                }
            }
        )

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = adapter.getItem(position)

            val exerciseType = when (selectedItem) {
                "Monday" -> 1
                "Tuesday" -> 2
                "Wednesday" -> 3
                "Thursday" -> 4
                "Friday" -> 5
                "Saturday" -> 6
                "Sunday" -> 7
                else -> 0
            }

            // Create a Bundle and pass the argument
            val bundle = Bundle().apply {
                putInt("exerciseType", exerciseType)
            }

            // Navigate using the action ID
            findNavController().navigate(R.id.action_search_to_exerciseSelection, bundle)
        }


        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // Set a listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    findNavController().navigate(R.id.action_search_to_settings_fragment)
                    true
                }
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_search_to_homePage)
                    true
                }
                R.id.nav_favorites -> {
                    findNavController().navigate(R.id.action_search_to_favorites)
                    true
                }
                R.id.nav_search -> {
                    //do nothing
                    true
                }
                else -> false
            }
        }
    }

}