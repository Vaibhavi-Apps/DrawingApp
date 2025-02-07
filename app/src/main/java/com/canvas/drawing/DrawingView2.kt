package com.canvas.drawing

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

class DrawingView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs)       {

    private val paint = Paint()
    private val paths = MutableList(15) { Path() }

    private var lastX = 0f
    private var lastY = 0f

    private val originalPoints = mutableListOf<PointF>()

    init {
        paint.color = Color.BLACK
        paint.isAntiAlias = true
        paint.strokeWidth = 5f
        paint.style = Paint.Style.STROKE
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Clear the original points list when starting a new drawing
                originalPoints.clear()
                originalPoints.add(PointF(x, y))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Add the current touch point to the original points list
                originalPoints.add(PointF(x, y))
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Nothing needed for now
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Clear canvas
        canvas.drawColor(Color.WHITE)

        // Draw the mirrored points for each original point
        originalPoints.forEachIndexed { index, originalPoint ->
            val centerX = width / 2f
            val centerY = height / 2f

            val dx = originalPoint.x - centerX
            val dy = originalPoint.y - centerY

            // Draw the original point
            if (index == 0) {
                // Move to the first point
                paths[0].reset()
                paths[0].moveTo(originalPoint.x, originalPoint.y)
            } else {
                // Connect the points with lines
                paths[0].lineTo(originalPoint.x, originalPoint.y)
            }

            // Calculate mirrored points on one side
            for (i in 1..7) {
                val mirroredX = centerX + dx * cos(Math.PI * i / 4).toFloat() - dy * sin(Math.PI * i / 4).toFloat()
                val mirroredY = centerY + dy * cos(Math.PI * i / 4).toFloat() + dx * sin(Math.PI * i / 4).toFloat()

                // Create paths if needed
                if (index == 0) {
                    paths[i].reset()
                    paths[i].moveTo(mirroredX, mirroredY)
                } else {
                    // Connect the mirrored points with lines
                    paths[i].lineTo(mirroredX, mirroredY)
                }
            }

            // Calculate mirrored points on the other side
            for (i in 1..7) {
                val mirroredX = centerX - dx * cos(Math.PI * i / 4).toFloat() + dy * sin(Math.PI * i / 4).toFloat()
                val mirroredY = centerY - dy * cos(Math.PI * i / 4).toFloat() - dx * sin(Math.PI * i / 4).toFloat()

                // Create paths if needed
                val otherSideIndex = i + 7
                if (index == 0) {
                    paths[otherSideIndex].reset()
                    paths[otherSideIndex].moveTo(mirroredX, mirroredY)
                } else {
                    // Connect the mirrored points with lines
                    paths[otherSideIndex].lineTo(mirroredX, mirroredY)
                }
            }
        }

        // Draw the paths
        paths.forEach { path ->
            canvas.drawPath(path, paint)
        }
    }
}
