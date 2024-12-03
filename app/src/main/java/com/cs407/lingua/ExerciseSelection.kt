package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ExerciseSelection.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExerciseSelection : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exercise_selection, container, false)
        val quizTitle = view.findViewById<TextView>(R.id.quizTitle)
        val questionCount = view.findViewById<TextView>(R.id.questionCount)
        val difficulty = view.findViewById<TextView>(R.id.difficultyLevel)
        val tagsGrid = view.findViewById<GridView>(R.id.tags)
        val description = view.findViewById<TextView>(R.id.quizDescription)
        val image = view.findViewById<ImageView>(R.id.exerciseIcon)
        val startQuiz = view.findViewById<Button>(R.id.button)
        settingsViewModel.primaryColor.value?.let { startQuiz.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }
        val args = this.arguments
        // TODO: Can this just be hardcoded?
        when(args?.getInt("exerciseType")) {
            1 -> {
                quizTitle.text = "Simple Phonology"
                questionCount.text = "Questions: 10"
                difficulty.text = "Easy"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Multiple Choice"))
                tagsModelArrayList.add(TagModel("IPA"))
                tagsModelArrayList.add(TagModel("Single"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.simple_phonology_description)
                image.setImageResource(R.drawable.simple_phonology)
            }
            2 -> {
                quizTitle.text = "Complex Phonology"
                questionCount.text = "Questions: 20"
                difficulty.text = "Hard"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("PRAAT"))
                tagsModelArrayList.add(TagModel("Nightmares"))
                tagsModelArrayList.add(TagModel("Schwa"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.complex_phonology_description)
                image.setImageResource(R.drawable.complex_phonology)
            }
            3 -> {
                quizTitle.text = "Simple Syntax"
                questionCount.text = "Questions: 15"
                difficulty.text = "Medium"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Short Answer"))
                tagsModelArrayList.add(TagModel("Syntax Tree"))
                tagsModelArrayList.add(TagModel("Single"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.simple_syntax_description)
                image.setImageResource(R.drawable.simple_syntax)
            }
            4 -> {
                quizTitle.text = "Complex Syntax"
                questionCount.text = "Questions: 30"
                difficulty.text = "Hard"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Creepy"))
                tagsModelArrayList.add(TagModel("Wet"))
                tagsModelArrayList.add(TagModel("???????"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.complex_syntax_description)
                image.setImageResource(R.drawable.complex_syntax)
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar2)


        // Set the toolbar as the action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        // Clicking back arrow goes back to home
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //findNavController().navigate(R.id.action_settings_fragment_to_homePage)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExerciseSelection.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ExerciseSelection().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}