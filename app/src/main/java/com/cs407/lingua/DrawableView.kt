package com.cs407.lingua

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

/**
 * TODO: document your custom view class.
 */
@SuppressLint("AppCompatCustomView")
class DrawableView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    val paint: Paint = Paint()
    val pointsDown = ArrayList<Point>()
    val pointsUp = ArrayList<Point>()

    init {
        paint.isAntiAlias = true
        paint.strokeWidth = 6f
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(i in 0..<pointsDown.size){
            canvas.drawLine(pointsUp[i].x.toFloat(),
                            pointsUp[i].y.toFloat(),
                            pointsDown[i].x.toFloat(),
                            pointsDown[i].y.toFloat(), paint)
        }
    }
}