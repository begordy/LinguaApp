package com.cs407.lingua

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
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
import org.jetbrains.skia.Image
import java.lang.Integer.max
import kotlin.math.min

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
    private var drawableSurface: DrawableView? = null
    private var primaryTintedColor: Int? = null

    private var mBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null

    private var outRect = Rect()
    private var lines = ArrayList<Line>()
    private var linesString = ArrayList<LineString>()

    private var movingLines = ArrayList<Int>()
    //true = moving start, false = moving end
    private var whichEnd = ArrayList<Boolean>()
    private var movingLineDeltas = ArrayList<ArrayList<Int>>()

    private var words: ArrayList<String>? = null
    private var prevCard: CardView? = null
    //0 = grab mode, 1 = connect mode
    private var mode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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
                    workspace!!.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    mRootWidth = workspace!!.width
                    mRootHeight = workspace!!.height
                }
            }
        )
        val elementBar = view.findViewById<LinearLayout>(R.id.elementBarContainer)
        val submitButton = view.findViewById<Button>(R.id.submitButton)
        val connectModeButton = view.findViewById<ImageButton>(R.id.connectModeButton)
        val grabModeButton = view.findViewById<ImageButton>(R.id.grabModeButton)
        val undoButton = view.findViewById<ImageButton>(R.id.undoButton)
        drawableSurface = view.findViewById(R.id.drawableSurface)
        drawableSurface!!.paint.isAntiAlias = true
        drawableSurface!!.paint.strokeWidth = 6f;
        drawableSurface!!.paint.color = Color.BLACK
        drawableSurface!!.paint.style = Paint.Style.STROKE
        drawableSurface!!.paint.strokeJoin = Paint.Join.ROUND
        //setting listeners etc. for the element bar
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
                if(mode == 0){
                    tempCardView.setOnTouchListener(mGrabModeOnTouchListener)
                }else if(mode ==1){
                    tempCardView.setOnTouchListener(mConnectModeOnTouchListener)
                }
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
            val infoCardRed = android.graphics.Color.red(it)
            val infoCardGreen = android.graphics.Color.green(it)
            val infoCardBlue = android.graphics.Color.blue(it)
            val infoCardAlpha = android.graphics.Color.alpha(it)
            primaryTintedColor = android.graphics.Color.argb(
                infoCardAlpha,
                max(infoCardRed - 50,0),
                max(infoCardGreen - 50,0),
                max(infoCardBlue - 50,0)
            )
            submitButton.setBackgroundColor(it)
            connectModeButton.background.setTint(it)
            grabModeButton.background.setTint(primaryTintedColor!!)
        }
        settingsViewModel.secondaryColor.value?.let {
            view.findViewById<HorizontalScrollView>(R.id.elementBar).setBackgroundColor(it)
            view.findViewById<CardView>(R.id.submitBar).setBackgroundColor(it)
        }

        //button listeners
        grabModeButton.setOnClickListener{
            mode = 0
            settingsViewModel.primaryColor.value?.let {
                connectModeButton.background.setTint(it)
                grabModeButton.background.setTint(primaryTintedColor!!)
            }
            for(child in workspace!!.children){
                if(child is CardView){
                    child.setOnTouchListener(mGrabModeOnTouchListener)
                }
            }
        }
        connectModeButton.setOnClickListener {
            mode = 1
            settingsViewModel.primaryColor.value?.let {
                grabModeButton.background.setTint(it)
                connectModeButton.background.setTint(primaryTintedColor!!)
            }
            for(child in workspace!!.children){
                if(child is CardView){
                    child.setOnTouchListener(mConnectModeOnTouchListener)
                }
            }
        }
        undoButton.setOnClickListener {
            //first clear the canvas
            lines = ArrayList<Line>()
            linesString = ArrayList<LineString>()
            drawableSurface!!.pointsUp = ArrayList<Point>()
            drawableSurface!!.pointsDown = ArrayList<Point>()
            val toDelete = ArrayList<Int>()
            for(i in 0..< workspace!!.childCount){
                if(workspace!!.getChildAt(i) is CardView){
                    toDelete.add(i)
                }
            }
            var offset = 0
            for(i in toDelete){
                workspace!!.removeView(workspace!!.getChildAt(i-offset))
                offset++
            }

            //re-spawn in the words
            for(word in words!!){
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
                if(mode == 0) tempCard.setOnTouchListener(mGrabModeOnTouchListener)
                else if(mode == 1) tempCard.setOnTouchListener(mConnectModeOnTouchListener)
                settingsViewModel.primaryColor.value?.let { tempCard.setCardBackgroundColor(it) }
                elementCount++
                if(prevCard != null){
                    val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.addRule(RelativeLayout.END_OF, prevCard!!.id)
                    (lp as ViewGroup.MarginLayoutParams).setMargins(0,0,20,0)
                    workspace!!.addView(tempCard, lp)
                }else{
                    val lp = ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(0,0,20,0)
                    workspace!!.addView(tempCard, lp)
                }
                prevCard = tempCard
            }

            //invalidate drawing surface
            drawableSurface!!.invalidate()
        }
        submitButton.setOnClickListener(mCheckAnswer)

        //setting drawable canvas
        val options = BitmapFactory.Options()
        options.inMutable = true
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.grid_png_43560, options)
        mCanvas = Canvas(mBitmap!!)

        val sentence = arguments?.getString("questionText")
        correctAnswer = arguments?.getString("correctAnswer")
        words = sentence?.split(" ") as ArrayList<String>
        //setting the elements for each word
        if (words != null) {
            for(word in words!!){
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
                tempCard.setOnTouchListener(mGrabModeOnTouchListener)
                settingsViewModel.primaryColor.value?.let { tempCard.setCardBackgroundColor(it) }
                elementCount++
                if(prevCard != null){
                    val lp = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    lp.addRule(RelativeLayout.END_OF, prevCard!!.id)
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
        //(activity as? AppCompatActivity)?.supportActionBar?.setDisplayOptions(R.menu.syntax_menu)
        settingsViewModel.primaryColor.value?.let { toolbar.setBackgroundColor(it) }
        // Clicking back arrow goes back to home
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
            //findNavController().navigate(R.id.action_settings_fragment_to_homePage)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    val mGrabModeOnTouchListener = View.OnTouchListener { cardView, motionEvent ->
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

                //check for which lines need to be moved
                for(line in lines){
                    if(line.startCardInt == cardView.id){
                        movingLines.add(line.drawableIndex)
                        val coords = arrayListOf(xScreenTouch - drawableSurface!!.pointsDown[line.drawableIndex].x,
                                                 yScreenTouch - drawableSurface!!.pointsDown[line.drawableIndex].y)
                        movingLineDeltas.add(coords)
                        whichEnd.add(true)
                    }else if(line.endCardInt == cardView.id){
                        movingLines.add(line.drawableIndex)
                        val coords = arrayListOf(xScreenTouch - drawableSurface!!.pointsUp[line.drawableIndex].x,
                                                 yScreenTouch - drawableSurface!!.pointsUp[line.drawableIndex].y)
                        movingLineDeltas.add(coords)
                        whichEnd.add(false)
                    }
                }
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
                for(i in 0..< movingLines.size){
                    val lineX = 0.coerceAtLeast(
                        mRootWidth.coerceAtMost(xScreenTouch - movingLineDeltas[i][0])
                    )
                    val lineY = 0.coerceAtLeast(
                        mRootHeight.coerceAtMost(yScreenTouch - movingLineDeltas[i][1])
                    )
                    if(whichEnd[i]){
                        drawableSurface!!.pointsDown[movingLines[i]].x = lineX
                        drawableSurface!!.pointsDown[movingLines[i]].y = lineY
                        drawableSurface?.invalidate()
                    }else{
                        drawableSurface!!.pointsUp[movingLines[i]].x = lineX
                        drawableSurface!!.pointsUp[movingLines[i]].y = lineY
                        drawableSurface?.invalidate()
                    }
                }
                Log.i("mOnTouchListener", "Called Action Move: (" + x + ", " + y + ")")
                cardView.layoutParams = lp
            }
            MotionEvent.ACTION_UP -> {
                //clear all line arrays
                movingLines = ArrayList<Int>()
                whichEnd = ArrayList<Boolean>()
                movingLineDeltas = ArrayList<ArrayList<Int>>()
                workspaceContainer?.setHorizontalPanEnabled(true)
                workspaceContainer?.setVerticalPanEnabled(true)
            }
        }
        return@OnTouchListener true
    }

    @SuppressLint("ClickableViewAccessibility")
    val mConnectModeOnTouchListener = View.OnTouchListener { cardView, motionEvent ->
        if(drawableSurface != null){
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    Log.i("ACTION_DOWN", "setting origin id: " + cardView.id)
                    workspaceContainer?.setHorizontalPanEnabled(false)
                    workspaceContainer?.setVerticalPanEnabled(false)
                    drawableSurface!!.pointsDown.add(Point())
                    drawableSurface!!.pointsDown[drawableSurface!!.pointsDown.size-1].x = motionEvent.x.toInt() + cardView.left
                    drawableSurface!!.pointsDown[drawableSurface!!.pointsDown.size-1].y = motionEvent.y.toInt() + cardView.top
                }
                MotionEvent.ACTION_MOVE -> {
                    if(drawableSurface!!.pointsUp.size < drawableSurface!!.pointsDown.size){
                        drawableSurface!!.pointsUp.add(Point())
                    }
                    drawableSurface!!.pointsUp[drawableSurface!!.pointsUp.size-1].x = motionEvent.x.toInt() + cardView.left
                    drawableSurface!!.pointsUp[drawableSurface!!.pointsUp.size-1].y = motionEvent.y.toInt() + cardView.top
                    drawableSurface?.invalidate()
                }
                MotionEvent.ACTION_UP -> {
                    Log.i("ACTION_UP", "it happened on card " + cardView.id)

                    var onOtherCard = false
                    var otherCardId = -1
                    var otherCardText = ""
                    for(child in workspace!!.children){
                        if(child is CardView && viewInBounds(child, motionEvent.x.toInt() + cardView.left, motionEvent.y.toInt() + cardView.top)){
                            if(child.id != cardView.id){
                                onOtherCard = true
                                otherCardId = child.id
                                otherCardText = (child.getChildAt(0) as TextView).text.toString()
                                break
                            }
                        }
                    }
                    if(onOtherCard) { //TODO: replace this with bound box checking
                        drawableSurface!!.pointsUp[drawableSurface!!.pointsUp.size-1].x = motionEvent.x.toInt() + cardView.left
                        drawableSurface!!.pointsUp[drawableSurface!!.pointsUp.size-1].y = motionEvent.y.toInt() + cardView.top
                        drawableSurface?.invalidate()
                        lines.add(Line(cardView.id, otherCardId, drawableSurface!!.pointsUp.size-1))
                        linesString.add(LineString(
                            ((cardView as CardView).getChildAt(0) as TextView).text.toString(),
                            otherCardText))
                    }else{
                        drawableSurface!!.pointsUp.removeAt(drawableSurface!!.pointsUp.size-1)
                        drawableSurface!!.pointsDown.removeAt(drawableSurface!!.pointsDown.size-1)
                        drawableSurface?.invalidate()
                    }
                    workspaceContainer?.setHorizontalPanEnabled(true)
                    workspaceContainer?.setVerticalPanEnabled(true)
                }
            }
        }
        return@OnTouchListener true
    }

    private val mCheckAnswer = View.OnClickListener {
        val hierarchy = ArrayList<String>()
        var currElement = ""
        var wordTrack = 0
        var correct = true
        for(i in 0 ..< correctAnswer?.length!!){
            if(correctAnswer?.get(i) == '['){
                if(currElement.isNotEmpty()){
                    if(hierarchy.size > 0){
                        if(!checkInUserAnswer(LineString(hierarchy[hierarchy.size-1],currElement))){
                            //answer is wrong, navigate away
                            Toast.makeText(requireContext(), "Wrong Answer", Toast.LENGTH_SHORT).show()
                            //TODO: remove (only for testing)
                            correct = false
                            break
                        }
                    }
                    hierarchy.add(currElement)
                    currElement = ""
                }
            }else if(correctAnswer?.get(i) == ']'){
                if(currElement.isNotEmpty()){
                    if(!checkInUserAnswer(LineString(hierarchy[hierarchy.size-1],currElement))){
                        //answer is wrong
                        Toast.makeText(requireContext(), "Wrong Answer", Toast.LENGTH_SHORT).show()
                        //TODO: remove (only for testing)
                        correct = false
                        break
                    }
                    if(!checkInUserAnswer(LineString(currElement, words!![wordTrack]))){
                        //answer is wrong
                        Toast.makeText(requireContext(), "Wrong Answer", Toast.LENGTH_SHORT).show()
                        //TODO: remove (only for testing)
                        correct = false
                        break
                    }
                    wordTrack++
                }else {
                    hierarchy.removeAt(hierarchy.size-1)
                }

                currElement = ""
            }else{
                currElement += correctAnswer?.get(i)
            }
        }
        //TODO: remove (only for testing)
        if(correct){
            Toast.makeText(requireContext(), "Right Answer", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkInUserAnswer(line: LineString): Boolean{
        for(i in 0 ..< linesString.size){
            if((line.startCard.compareTo(linesString[i].startCard) == 0) &&
               (line.endCard.compareTo(linesString[i].endCard) == 0)){
                linesString.removeAt(i)
                return true
            }else if((line.startCard.compareTo(linesString[i].endCard) == 0) &&
                     (line.endCard.compareTo(linesString[i].startCard) == 0)){
                linesString.removeAt(i)
                return true
            }
        }
        return false
    }

    private fun viewInBounds(view: View, x: Int, y: Int): Boolean {
        view.getDrawingRect(outRect)
        //workspace!!.offsetDescendantRectToMyCoords(view, outRect)
        outRect.offset(view.left, view.top)
        return outRect.contains(x,y)
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

data class Line(val startCardInt: Int, val endCardInt: Int, val drawableIndex: Int)

data class LineString(val startCard: String, val endCard: String)