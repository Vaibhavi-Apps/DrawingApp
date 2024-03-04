package com.canvas.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import java.lang.Math.cos
import java.lang.Math.sin
import java.util.*


class DrawingView(context: Context,attrs:AttributeSet): View(context,attrs)  {
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0f
    private var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)

        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND

        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
        super.onSizeChanged(w, h, wprev, hprev)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, mCanvasPaint)
        }

        for (p in mPaths) {
            mDrawPaint?.strokeWidth = p.brushThickness
            mDrawPaint?.color = p.color
            canvas.drawPath(p, mDrawPaint!!)
            mirrorDraw(canvas, p)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath?.color = color
                mDrawPath?.brushThickness = mBrushSize

                mDrawPath?.reset()
                mDrawPath?.moveTo(touchX, touchY)
            }
            MotionEvent.ACTION_MOVE -> {
                mDrawPath?.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        postInvalidate()
        return true
    }

    private fun mirrorDraw(canvas: Canvas, path: CustomPath) {
        val centerX = width / 2f
        val centerY = height / 2f

        // Mirror path horizontally
        val matrixHorizontal = Matrix()
        matrixHorizontal.setScale(-1f, 1f, centerX, centerY)
        val mirroredPathHorizontal = Path()
        path.transform(matrixHorizontal, mirroredPathHorizontal)
        canvas.drawPath(mirroredPathHorizontal, mDrawPaint!!)

        // Mirror path vertically
        val matrixVertical = Matrix()
        matrixVertical.setScale(1f, -1f, centerX, centerY)
        val mirroredPathVertical = Path()
        path.transform(matrixVertical, mirroredPathVertical)
        canvas.drawPath(mirroredPathVertical, mDrawPaint!!)

        // Mirror path diagonally
        val matrixDiagonal = Matrix()
        matrixDiagonal.setScale(-1f, -1f, centerX, centerY)
        val mirroredPathDiagonal = Path()
        path.transform(matrixDiagonal, mirroredPathDiagonal)
        canvas.drawPath(mirroredPathDiagonal, mDrawPaint!!)
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, newSize,
            resources.displayMetrics
        )
        mDrawPaint?.strokeWidth = mBrushSize
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint?.color = color
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path()
}
