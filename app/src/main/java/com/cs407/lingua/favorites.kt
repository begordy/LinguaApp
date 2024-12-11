package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class favorites : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookmarked = view.findViewById<ListView>(R.id.bookmarked)
        val uid = Firebase.auth.currentUser?.uid
        val database = Firebase.database.getReference("users/$uid/Favorites")

        database.get(). addOnSuccessListener {

            val favorites = it.getValue<List<String>>() ?: emptyList()
            val adapter = ArrayAdapter(bookmarked.context, android.R.layout.simple_list_item_1, favorites)

            bookmarked.adapter = adapter
        }

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottom_navigation)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }
        // Set a listener to handle item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_settings -> {
                    findNavController().navigate(R.id.action_favorites_to_settings_fragment)
                    true
                }
                R.id.nav_home -> {
                    findNavController().navigate(R.id.action_favorites_to_homePage)
                    true
                }
                R.id.nav_favorites -> {
                    //do nothing
                    true
                }
                R.id.nav_search -> {
                    findNavController().navigate(R.id.action_favorites_to_search)
                    true
                }
                else -> false
            }
        }


    }
}