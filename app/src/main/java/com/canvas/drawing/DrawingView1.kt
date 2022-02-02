package com.canvas.drawing

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView1(context: Context, attrs:AttributeSet): View(context,attrs) {

    var paint: Paint? = null
    var paintText: Paint? = null
    var paintTriangle: Paint? = null
    var paint8: Paint? = null

    init {
        setUpDrawing()
    }

    private fun setUpDrawing() {
        paint = Paint()
        paintText = Paint()
        paintTriangle = Paint()
        paint8 = Paint()
    }

    override fun onSizeChanged(w: Int, h: Int, wprev: Int, hprev: Int) {
        super.onSizeChanged(w, h, wprev, hprev)

    }


    /**
     * This method is called when a stroke is drawn on the canvas
     * as a part of the painting.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //paint?.color = Color.MAGENTA
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 5f
        paint!!.color = Color.BLUE
        canvas.drawRect(250F,250F,400F,400F, paint!!)

        paintText!!.textSize = 20F
        paintText!!.style = Paint.Style.FILL
        paintText!!.color = Color.BLUE
        canvas.drawText("Hello", 150F,200F, paintText!!)

        canvas.drawARGB( 24,2,4,55)
        var ctx1 = Path()
        ctx1.moveTo(125F, 125F);
        ctx1.lineTo(125F, 45F);
        ctx1.lineTo(45F, 125F);
        canvas.drawPath(ctx1,paint8!!)

        paintTriangle!!.isAntiAlias = true
        paintTriangle!!.style = Paint.Style.STROKE
        paintTriangle!!.strokeWidth = 5f
        paintTriangle!!.color = Color.BLUE
        var ctx = Path()
        ctx.moveTo(400F, 250F);
        ctx.lineTo(300F, 400F);
        ctx.lineTo(300F, 250F);
        canvas.drawPath(ctx,paintTriangle!!)
    }

    /**
     * This method acts as an event listener when a touch
     * event is detected on the device.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x // Touch event of X coordinate
        val touchY = event.y // touch event of Y coordinate

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_MOVE -> {

            }

            MotionEvent.ACTION_UP -> {

            }
            else -> return false
        }

        //invalidate()
        postInvalidate()
        return true
    }

}
