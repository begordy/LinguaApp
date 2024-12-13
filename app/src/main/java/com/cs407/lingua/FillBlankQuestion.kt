package com.cs407.lingua

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

private var correctAnswer: String? = ""

/**
 * A simple [Fragment] subclass.
 * Use the [FillBlankQuestion.newInstance] factory method to
 * create an instance of this fragment.
 */
class FillBlankQuestion : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_fill_blank_question, container, false)
        val questionText = view.findViewById<TextView>(R.id.fillBlankQuestionText)
        val questionEntry = view.findViewById<EditText>(R.id.fillBlankText)
        val submitButton = view.findViewById<Button>(R.id.submitButton2)
        settingsViewModel.primaryColor.value?.let { submitButton.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let { view.setBackgroundColor(it) }

        questionText.text = arguments?.getString("questionText")
        correctAnswer = arguments?.getString("correctAnswer")

        submitButton.setOnClickListener {
            val bundle = Bundle()
            bundle.putIntArray("quizInfo", arguments?.getIntArray("quizInfo")) // PASS QUIZ INFO
            bundle.putString("correctAnswer", correctAnswer) // for QuestionResult to display
            if(correctAnswer?.compareTo(questionEntry.text.toString()) == 0){
                //Toast.makeText(requireContext(), "Entered Correct Answer", Toast.LENGTH_SHORT).show()
                bundle.putBoolean("correct", true)
            }else{
                //Toast.makeText(requireContext(), "Your entered answer of \"" + questionEntry.text.toString() + "\" is incorrect", Toast.LENGTH_SHORT).show()
                bundle.putBoolean("correct", false)
            }
            findNavController().navigate(R.id.fillBlank_to_questionResult, bundle)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar4)


        // Set the toolbar as the action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        // TODO: Clicking back arrow prompts user if they want to leave the quiz before navigating to home
        toolbar.setNavigationOnClickListener {
            //findNavController().navigateUp()
            findNavController().navigate(R.id.fillBlank_to_home)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FillBlankQuestion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FillBlankQuestion().apply {
                arguments = Bundle().apply {
                }
            }
    }
}