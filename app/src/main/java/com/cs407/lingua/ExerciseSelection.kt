package com.cs407.lingua

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cs407.lingua.DataLoader.QInfo
import org.jetbrains.skia.ColorSpace
import kotlin.math.min


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
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_exercise_selection, container, false)
        val quizTitle = view.findViewById<TextView>(R.id.quizTitle)
        val questionCount = view.findViewById<TextView>(R.id.questionCount)
        val difficulty = view.findViewById<TextView>(R.id.difficultyLevel)
        val tagsGrid = view.findViewById<GridView>(R.id.tags)
        val description = view.findViewById<TextView>(R.id.quizDescription)
        val image = view.findViewById<ImageView>(R.id.exerciseIcon)
        val startQuiz = view.findViewById<Button>(R.id.button)
        val infoCard = view.findViewById<CardView>(R.id.infoCard)
        settingsViewModel.primaryColor.value?.let { startQuiz.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }
        val oColor = settingsViewModel.primaryColor.value
        val infoCardRed = oColor?.let { Color.red(it) }
        val infoCardGreen = oColor?.let { Color.green(it) }
        val infoCardBlue = oColor?.let { Color.blue(it) }
        val infoCardAlpha = oColor?.let { Color.alpha(it) }
        val newColor = Color.argb(
            infoCardAlpha!!,
            min(infoCardRed!! + 150,255),
            min(infoCardGreen!! + 150,255),
            min(infoCardBlue!! + 150,255))
        infoCard.setCardBackgroundColor(newColor)
        val args = this.arguments
        // TODO: Can this just be hardcoded?
        when(args?.getInt("exerciseType")) {
            1 -> {
                quizTitle.text = "Simple Phonology"
                questionCount.text = "Questions: 20"
                difficulty.text = "Easy"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Multiple Choice"))
                tagsModelArrayList.add(TagModel("IPA"))
                tagsModelArrayList.add(TagModel("Single"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.simple_phonology_description)
                image.setImageResource(R.drawable.simple_phonology)
                startQuiz.setOnClickListener {startQuiz(1)}
            }
            2 -> {
                quizTitle.text = "Advanced Phonology"
                questionCount.text = "Questions: 15"
                difficulty.text = "Hard"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("PRAAT"))
                tagsModelArrayList.add(TagModel("Nightmares"))
                tagsModelArrayList.add(TagModel("Schwa"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.complex_phonology_description)
                image.setImageResource(R.drawable.complex_phonology)
                startQuiz.setOnClickListener {startQuiz(2)}
            }
            3 -> {
                quizTitle.text = "Simple Syntax"
                questionCount.text = "Questions: 10"
                difficulty.text = "Medium"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Short Answer"))
                tagsModelArrayList.add(TagModel("Syntax Tree"))
                tagsModelArrayList.add(TagModel("Single"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.simple_syntax_description)
                image.setImageResource(R.drawable.simple_syntax)
                startQuiz.setOnClickListener {startQuiz(3)}
            }
            4 -> {
                quizTitle.text = "Advanced Syntax"
                questionCount.text = "Questions: 5"
                difficulty.text = "Hard"
                val tagsModelArrayList = ArrayList<TagModel>();
                tagsModelArrayList.add(TagModel("Creepy"))
                tagsModelArrayList.add(TagModel("Wet"))
                tagsModelArrayList.add(TagModel("???????"))

                val adapter = TagAdapter(requireContext(), tagsModelArrayList)
                tagsGrid.adapter = adapter
                description.text = getString(R.string.complex_syntax_description)
                image.setImageResource(R.drawable.complex_syntax)
                startQuiz.setOnClickListener {startQuiz(4)}
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
            //findNavController().navigateUp()
            findNavController().navigate(R.id.exercise_to_home)
        }
    }

    private fun startQuiz(mode: Int){
        //quizInfo[0] = number of correct answers
        //quizInfo[1] = number of questions completed
        //quizInfo[2] = total number of questions
        //quizInfo[3] = type of quiz
        var quizInfo = IntArray(4)
        quizInfo[0] = 0
        quizInfo[1] = 0
        quizInfo[3] = mode
        var question = QInfo("", "error", "error", emptyArray<String>())
        val dataLoader = settingsViewModel.dataLoader
        when(mode){
            1 -> {
                question = dataLoader.simplePhonetics()
                quizInfo[2] = 15
            }
            2 -> {
                question = dataLoader.advancedPhonetics()
                quizInfo[2] = 10
            }
            3 -> {
                question = dataLoader.simpleSyntax()
                quizInfo[2] = 10
            }
            4 -> {
                question = dataLoader.advancedSyntax()
                quizInfo[2] = 5
            }
        }
        if(question.fragmentID.compareTo("") != 0){
            val bundle = Bundle()
            //bundle.putString("questionText", "This is a tester question designed to test things. Make this long enough.")
            //bundle.putString("correctAnswer", "Correct Answer")
            //bundle.putStringArray("optionList", arrayOf("Option 1", "Option 2", "Option 3"))
            bundle.putString("questionText", question.question)
            bundle.putString("correctAnswer", question.answer)
            bundle.putStringArray("optionList", question.options)

            bundle.putIntArray("quizInfo", quizInfo) // PASS QUIZ INFO

            when(question.fragmentID){
                "mc" -> findNavController().navigate(R.id.selection_to_mc, bundle)
                "fillBlank" -> findNavController().navigate(R.id.selection_to_fillBlank, bundle)
                "syntax" -> findNavController().navigate(R.id.selection_to_syntax, bundle)
            }
        }
        else{
            Toast.makeText(requireContext(), "DataLoader or Mode Assignment Error", Toast.LENGTH_SHORT).show()
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
                }
            }
    }
}