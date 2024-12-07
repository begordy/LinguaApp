package com.cs407.lingua

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.children
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.otaliastudios.zoom.ZoomLayout
import org.jetbrains.skia.Color

/**
 * A simple [Fragment] subclass.
 * Use the [SyntaxAdvancedQuestion.newInstance] factory method to
 * create an instance of this fragment.
 */
class SyntaxAdvancedQuestion : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private var correctAnswer: String? = ""

    private var mXDelta = 0
    private var mYDelta = 0
    private var mRootWidth: Int = 0
    private var mRootHeight: Int = 0
    private var displayCard: CardView? = null
    private var elementCount = 1
    private var lastAddedElement: CardView? = null
    private var workspaceContainer: ZoomLayout? = null
    private var workspace: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_syntax_advanced_question, container, false)
        workspace = view.findViewById(R.id.workspaceEditable)
        workspaceContainer = view.findViewById(R.id.workspace)
        workspace!!.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener{
                override fun onGlobalLayout() {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        workspace!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }

                    mRootWidth = workspace!!.width
                    mRootHeight = workspace!!.height
                }
            }
        )
        val elementBar = view.findViewById<LinearLayout>(R.id.elementBarContainer)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        for(child in elementBar.children){
            settingsViewModel.primaryColor.value?.let {
                (child as CardView).setCardBackgroundColor(
                    it
                )
            }
            ((child as CardView).getChildAt(0) as TextView).setTextColor(Color.WHITE)
            child.setOnClickListener {
                //add a new card to workspace
                val tempCardView = CardView(requireContext())
                tempCardView.id = elementCount
                elementCount++
                tempCardView.setCardBackgroundColor(child.cardBackgroundColor)
                val tempText = TextView(requireContext())
                tempText.text = (child.getChildAt(0) as TextView).text
                tempText.setTextColor((child.getChildAt(0) as TextView).currentTextColor)
                tempText.layoutParams = (child.getChildAt(0) as TextView).layoutParams
                tempCardView.addView(tempText)
                tempCardView.setOnTouchListener(mOnTouchListener)
                val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                if(lastAddedElement != null){
                    lp.addRule(RelativeLayout.END_OF, lastAddedElement!!.id)
                }else{
                    lp.addRule(RelativeLayout.ALIGN_PARENT_START)
                }
                lp.setMargins(0,0,20,0)
                workspace!!.addView(tempCardView, lp)
                lastAddedElement = tempCardView
            }
        }
        settingsViewModel.primaryColor.value?.let {
            submitButton.setBackgroundColor(it)
        }
        settingsViewModel.secondaryColor.value?.let {
            view.findViewById<HorizontalScrollView>(R.id.elementBar).setBackgroundColor(
                it
            )
            view.findViewById<CardView>(R.id.submitBar).setBackgroundColor(it)
        }


        val sentence = arguments?.getString("questionText")
        correctAnswer = arguments?.getString("correctAnswer")
        val words = sentence?.split(" ")
        var prevCard: CardView? = null
        if (words != null) {
            for(word in words){
                val tempCard = CardView(requireContext())
                val tempText = TextView(requireContext())
                tempText.text = word
                tempText.setTextColor(Color.WHITE)
                val lpText = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                val r = requireContext().resources
                val px = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    10F,
                    r.displayMetrics
                )
                lpText.setMargins(px.toInt())
                tempCard.addView(tempText, lpText)
                tempCard.id = elementCount
                tempCard.setOnTouchListener(mOnTouchListener)
                settingsViewModel.primaryColor.value?.let { tempCard.setCardBackgroundColor(it) }
                elementCount++
                if(prevCard != null){
                    val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.addRule(RelativeLayout.END_OF, prevCard.id)
                    (lp as ViewGroup.MarginLayoutParams).setMargins(0,0,20,0)
                    workspace!!.addView(tempCard, lp)
                }else{
                    val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(0,0,20,0)
                    workspace!!.addView(tempCard, lp)
                }
                prevCard = tempCard
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar5)


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

    val mOnTouchListener = View.OnTouchListener { cardView, motionEvent ->
        val xScreenTouch = motionEvent.rawX.toInt()
        val yScreenTouch = motionEvent.rawY.toInt()
        when(motionEvent.action){
            MotionEvent.ACTION_DOWN -> {
                val lp = cardView.layoutParams as RelativeLayout.LayoutParams
                val oldX = cardView.x
                val oldY = cardView.y
                lp.removeRule(RelativeLayout.END_OF)
                lp.leftMargin = oldX.toInt()
                lp.topMargin = oldY.toInt()
                mXDelta = xScreenTouch - lp.leftMargin
                mYDelta = yScreenTouch - lp.topMargin
                workspaceContainer?.setHorizontalPanEnabled(false)
                workspaceContainer?.setVerticalPanEnabled(false)
                Log.i("mOnTouchListener", "Called Action Down: (" + mXDelta + ", " + mYDelta + ")")
            }
            MotionEvent.ACTION_MOVE -> {
                val lp = cardView.layoutParams as RelativeLayout.LayoutParams
                val x = 0.coerceAtLeast(
                    (mRootWidth - cardView.width).coerceAtMost(xScreenTouch - mXDelta)
                )
                lp.leftMargin = x
                val y = 0.coerceAtLeast(
                    (mRootHeight - cardView.height).coerceAtMost(yScreenTouch - mYDelta)
                )
                lp.topMargin = y
                Log.i("mOnTouchListener", "Called Action Move: (" + x + ", " + y + ")")
                cardView.layoutParams = lp
            }
            MotionEvent.ACTION_UP -> {
                workspaceContainer?.setHorizontalPanEnabled(true)
                workspaceContainer?.setVerticalPanEnabled(true)
            }
        }
        return@OnTouchListener true
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SyntaxAdvancedQuestion.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SyntaxAdvancedQuestion().apply {
                arguments = Bundle().apply {
                }
            }
    }
}