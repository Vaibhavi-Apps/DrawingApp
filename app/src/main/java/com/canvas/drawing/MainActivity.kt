package com.canvas.drawing

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.canvas.drawing.databinding.ActivityMainBinding
import java.lang.String


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var lastView : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.brush?.setOnClickListener{
            selectBrushSize()
        }
        binding?.color?.setOnClickListener{
            selectColor()
        }
    }

    private fun selectColor() {
        val alertDialog = AlertDialog.Builder(this)
        var dialog: Dialog
        alertDialog.setTitle("Select Brush Size")
        var v = LayoutInflater.from(this).inflate(R.layout.color_selector, null)
        alertDialog.setView(v)
        var view = v.findViewById<LinearLayout>(R.id.main_linear)


        val rainbow: IntArray = this.getResources().getIntArray(R.array.rainbow)

        for (i in 0 until rainbow.size) {
            val imageView = ImageView(this)
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(40, 40)
            params.marginStart = 16
            imageView.setLayoutParams(params)
            imageView.setBackgroundColor(rainbow.get(i))
            val hexColor = String.format("#%06X", 0xFFFFFF and rainbow.get(i))
            imageView.setTag(hexColor)
            view.addView(imageView)
            imageView.setOnClickListener{
                Log.e("vaibhavi","lastView"+ lastView)
                var lastv = view[lastView] as ImageView
                lastv.setImageBitmap(null)
                lastView = i
                imageView.setImageResource(R.drawable.image_border)
                binding?.draw?.setColor(it.tag.toString())
                Log.e("vaibhavi","click"+ i)
            }
        }

        alertDialog.setPositiveButton("Ok", { dialogInterface,
                                              i -> dialogInterface.dismiss()
        })

        dialog = alertDialog.show()
        dialog.show()
    }

    private fun selectBrushSize() {
        val dialog = Dialog(this)
        dialog.setTitle("Select Brush Size")
        dialog.setContentView(R.layout.brush_selector)

        var view = dialog.findViewById<BrushView>(R.id.action_image)
        var seek_bar = dialog.findViewById<SeekBar>(R.id.seek_bar)
        var button_size = dialog.findViewById<Button>(R.id.button_size)
        var size = 10
        seek_bar.incrementProgressBy(10)

        seek_bar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar,
                                           progress: Int, fromUser: Boolean) {
                // write custom code for progress is changed

                view.setRadius(progress)
            }

            override fun onStartTrackingTouch(seek: SeekBar) {
                // write custom code for progress is started
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                // write custom code for progress is stopped
                size = seek.progress

               /* view.setScaleType(ImageView.ScaleType.CENTER_INSIDE)
                val params: LinearLayout.LayoutParams =
                    LinearLayout.LayoutParams(size, size)
                view.setLayoutParams(params)*/



                Toast.makeText(this@MainActivity,
                    "Progress is: " + seek.progress + "%",
                    Toast.LENGTH_SHORT).show()
            }
        })

        button_size.setOnClickListener{
            dialog.dismiss()
            binding?.draw?.setSizeForBrush(size.toFloat())
        }

        dialog.show()





    }
}