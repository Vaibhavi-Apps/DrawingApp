package com.canvas.drawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BrushView(context: Context,attrs:AttributeSet) : View(context,attrs)  {
    private var radius = 0
    private var circle: Paint? = null
    private var square: Paint? = null
    private var color = Color.BLUE
    private var alphaValue = 255

    init {
        circle = Paint()
        square = Paint()
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BrushView)
        radius = typedArray.getInt(R.styleable.BrushView_brush_size, 5)
        alphaValue = typedArray.getInt(R.styleable.BrushView_brush_alpha, 255)
        color = typedArray.getInt(R.styleable.BrushView_brush_color, Color.RED)
        typedArray.recycle()

   }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        circle!!.isAntiAlias = true
        circle!!.style = Paint.Style.FILL
        circle!!.color = color
        circle!!.alpha = alphaValue
        square!!.isAntiAlias = true
        square!!.style = Paint.Style.STROKE
        square!!.strokeWidth = 5f
        square!!.color = Color.BLUE
        canvas.drawCircle(
            (width / 2).toFloat(), (height / 2).toFloat(), getRadius().toFloat(),
            circle!!
        )
       /* canvas.drawRect(
            leftv.toFloat(), topv.toFloat(), (width - leftv).toFloat(), (height - topv).toFloat(),
            square!!
        )*/
    }

    fun setRadius(radius: Int) {
        this.radius = radius
        invalidate()
    }

    fun getRadius(): Int {
        return radius
    }

    fun setColor(color: Int) {
        this.color = color
        circle!!.color = color
        invalidate()
    }

    fun getColor(): Int {
        return color
    }

    fun setAlphaValue(alphaValue: Int) {
        this.alphaValue = alphaValue
        circle!!.alpha = alphaValue
        invalidate()
    }

    fun getAlphaValue(): Int {
        return alphaValue
    }
}