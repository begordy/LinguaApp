package com.cs407.lingua

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cs407.lingua.DataLoader.QInfo
import com.google.android.material.progressindicator.CircularProgressIndicator

/**
 * A simple [Fragment] subclass.
 * Use the [QuestionResult.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionResult : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_question_result, container, false)
        val resultImage = view.findViewById<ImageView>(R.id.resultImage)
        val correctAnswerDisplay = view.findViewById<TextView>(R.id.correctAnswerDisplay)
        val nextQButton = view.findViewById<Button>(R.id.nextQuestionButton)
        val progressIndicator = view.findViewById<CircularProgressIndicator>(R.id.circle)
        val progressText = view.findViewById<TextView>(R.id.progressText)

        val quizInfo = arguments?.getIntArray("quizInfo") as IntArray

        if(arguments?.getBoolean("correct") == true){
            resultImage.setImageResource(R.drawable.green_check)
            quizInfo[0] += 1 // increment # of questions correct
            correctAnswerDisplay.visibility = View.INVISIBLE
        }
        else if(arguments?.getBoolean("correct") == false){
            resultImage.setImageResource(R.drawable.red_x)
            if( quizInfo[3] == 3 || quizInfo[3] == 4 ) { // ie. syntax
                correctAnswerDisplay.visibility = View.INVISIBLE
            }
            else { // display correct answer
                correctAnswerDisplay.text = getString(R.string.correct_answer,
                    arguments?.getString("correctAnswer"))
            }
        }
        else{
            Toast.makeText(requireContext(),"Error getting correctness", Toast.LENGTH_SHORT).show()
        }

        quizInfo[1] += 1 // increment # of questions completed

        progressIndicator.progress = (((quizInfo[1].toDouble()/quizInfo[2])) * 100).toInt()
        progressText.text = getString(R.string.progress_text, quizInfo[1], quizInfo[2])

        nextQButton.setOnClickListener() {
            val bundle = Bundle()
            bundle.putIntArray("quizInfo", quizInfo) // PASS QUIZ INFO

            if(quizInfo[1] == quizInfo[2]) { // all questions completed
                findNavController().navigate(R.id.questionResult_to_quizResult, bundle)
            }
            else {
                val dataLoader = settingsViewModel.dataLoader
                val question = when(quizInfo[3]){
                    1 -> dataLoader.simplePhonetics()
                    2 -> dataLoader.advancedPhonetics()
                    3 -> dataLoader.simpleSyntax()
                    4 -> dataLoader.advancedSyntax()
                    else -> QInfo("", "error", "error", emptyArray<String>())
                }
                bundle.putString("questionText", question.question)
                bundle.putString("correctAnswer", question.answer)
                bundle.putStringArray("optionList", question.options)
                when(question.fragmentID){
                    "mc" -> findNavController().navigate(R.id.questionResult_to_MCQuestion, bundle)
                    "fillBlank" -> findNavController().navigate(R.id.questionResult_to_fillBlankQuestion, bundle)
                    "syntax" -> findNavController().navigate(R.id.questionResult_to_syntax, bundle)
                }
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar6)

        // Set the toolbar as the action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as? AppCompatActivity)?.supportActionBar?.setHomeAsUpIndicator(R.drawable.back_arrow)
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        // TODO: Clicking back arrow prompts user if they want to leave the quiz before navigating to home
        toolbar.setNavigationOnClickListener {
            //findNavController().navigateUp()
            findNavController().navigate(R.id.questionResult_to_home)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QuestionResult.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuestionResult().apply {
                arguments = Bundle().apply {
                }
            }
    }
}