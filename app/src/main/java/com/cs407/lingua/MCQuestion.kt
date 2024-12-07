package com.cs407.lingua

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlin.random.Random

private var selectedOption = -1
private var correctOption = -1

/**
 * A simple [Fragment] subclass.
 * Use the [MCQuestion.newInstance] factory method to
 * create an instance of this fragment.
 */
class MCQuestion : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_mc_question, container, false)
        val questionText = view.findViewById<TextView>(R.id.question)
        val questionOptions = view.findViewById<RadioGroup>(R.id.questionOptions)
        val submitButton = view.findViewById<Button>(R.id.submitBar)
        settingsViewModel.primaryColor.value?.let { submitButton.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }

        questionText.text = arguments?.getString("questionText")
        val options = arguments?.getStringArray("optionList")
        val correctAnswer = arguments?.getString("correctAnswer")
        if (options != null) {
            val correctOptionIndex = Random.nextInt(0,options.size+1)
            Log.i("MCQuestion", correctOptionIndex.toString())
            var correctAnswerAdded = false
            for(i in 0..options.size){
                val tempOption = RadioButton(requireContext())
                if(correctAnswerAdded){
                    tempOption.text = options[i-1]
                }else if(i == correctOptionIndex){
                    tempOption.text = correctAnswer
                    correctAnswerAdded = true
                }else{
                    tempOption.text = options[i]
                }
                questionOptions.addView(tempOption)
                if(i == correctOptionIndex){
                    correctOption = tempOption.id
                }
            }
            questionOptions.setOnCheckedChangeListener { radioGroup, i ->
                selectedOption = i
                Log.i("MCQuestion", "Selected Option " + i.toString())
                Log.i("MCQuestion", "Correct Option: " + correctOption.toString())
            }
            submitButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putIntArray("quizInfo", arguments?.getIntArray("quizInfo")) // PASS QUIZ INFO
                bundle.putString("correctAnswer", correctAnswer) // for QuestionResult to display
                if(selectedOption == correctOption){
                    bundle.putBoolean("correct", true)
                }else{
                    bundle.putBoolean("correct", false)
                }
                findNavController().navigate(R.id.MCQuestion_to_questionResult, bundle)
            }
        }else{
            Toast.makeText(requireContext(), "Error loading options", Toast.LENGTH_SHORT).show()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar3)


        // Set the toolbar as the action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        // TODO: Clicking back arrow prompts user if they want to leave the quiz before navigating to home
        toolbar.setNavigationOnClickListener {
            //findNavController().navigateUp()
            findNavController().navigate(R.id.mc_to_home)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MCQuestion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MCQuestion().apply {
                arguments = Bundle().apply {
                }
            }
    }
}