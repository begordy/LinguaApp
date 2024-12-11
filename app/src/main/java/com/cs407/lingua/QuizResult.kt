package com.cs407.lingua

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.progressindicator.CircularProgressIndicator
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import kotlin.math.min

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizResult.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizResult : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_quiz_result, container, false)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        val homeButton = view.findViewById<Button>(R.id.homeButton)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val completeText = view.findViewById<TextView>(R.id.completeText)
        val correctIndicator = view.findViewById<CircularProgressIndicator>(R.id.circle)
        val correctText = view.findViewById<TextView>(R.id.correctText)
        val toolbar = view.findViewById<Toolbar>(R.id.toolbar6)

        val quizInfo = arguments?.getIntArray("quizInfo") as IntArray

        val type = when(quizInfo[3]) {
            1 -> "Simple Phonetics"
            2 -> "Advanced Phonetics"
            3 -> "Simple Syntax"
            4 -> "Advanced Syntax"
            else -> "error"
        }
        completeText.text = getString(R.string.complete_text, type)

        correctIndicator.progress = (((quizInfo[0].toDouble()/quizInfo[2])) * 100).toInt()
        correctText.text = getString(R.string.progress_text, quizInfo[0], quizInfo[2])

        homeButton.setOnClickListener() {
            findNavController().navigate(R.id.quizResult_to_homePage)
        }
        saveButton.setOnClickListener {
            //TODO: connect this with the favorites backend code
        }
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        settingsViewModel.secondaryColor.value?.let{view.setBackgroundColor(it)}
        settingsViewModel.primaryColor.value?.let { homeButton.setBackgroundColor(it) }
        val oColor = settingsViewModel.primaryColor.value
        val progressIndicatorRed = oColor?.let { Color.red(it) }
        val progressIndicatorGreen = oColor?.let { Color.green(it) }
        val progressIndicatorBlue = oColor?.let { Color.blue(it) }
        val progressIndicatorAlpha = oColor?.let { Color.alpha(it) }
        val newColor = Color.argb(
            progressIndicatorAlpha!!,
            min(progressIndicatorRed!! + 150,255),
            min(progressIndicatorGreen!! + 150,255),
            min(progressIndicatorBlue!! + 150,255)
        )
        correctIndicator.trackColor = newColor
        correctIndicator.setIndicatorColor(oColor)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QuizResult.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizResult().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}