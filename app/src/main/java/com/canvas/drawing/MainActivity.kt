package com.canvas.drawing

import android.Manifest
import android.R.attr.x
import android.R.attr.y
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.canvas.drawing.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    var lastView : Int = 0

    val requestPermission: ActivityResultLauncher<Array<kotlin.String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permissions ->
        permissions.entries.forEach {
            val perMissionName = it.key
            val isGranted = it.value
            //if permission is granted show a toast and perform operation
            if (isGranted ) {
                Toast.makeText(
                    this@MainActivity,
                    "Permission granted now you can read the storage files.",
                    Toast.LENGTH_LONG
                ).show()
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                lifecycleScope.launch{
                    //reference the frame layout
                   // val flDrawingView:FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    //Save the image to the device
                    saveBitmapFile(binding?.linearLayout?.let { it1 -> getBitmapFromView(it1) })
                }
            } else {
                //Displaying another toast if permission is not granted and this time focus on
                //    Read external storage
                if (perMissionName == Manifest.permission.READ_EXTERNAL_STORAGE)
                    Toast.makeText(
                        this@MainActivity,
                        "Oops you just denied the permission.",
                        Toast.LENGTH_LONG
                    ).show()
            }
        }
    }

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
        binding?.save?.setOnClickListener{
           requestStoragePermission()
        }
        binding?.remove?.setOnClickListener{
            binding?.draw?.setColor("#FFFFFF")
        }
        binding?.undo?.setOnClickListener{
            binding?.draw?.onClickUndo()
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
                Log.e("vaibhavi","click"+ it.tag.toString())
            }
        }

        alertDialog.setPositiveButton("Ok") { dialogInterface, i ->
            dialogInterface.dismiss()
        }

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

/*
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val p1 = Point()
        p1.x = x //x co-ordinate where the user touches on the screen
        p1.y = y

        val image = binding?.draw?.let { getBitmapFromView(it) }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {

            }

            MotionEvent.ACTION_MOVE -> {

                if (image != null) {
                    binding?.draw?.floodFill(image, p1, R.color.purple_200,R.color.purple_200)
                }
            }

            MotionEvent.ACTION_UP -> {

            }
            else -> return false
        }
        return true
    }
*/

    //create a method to requestStorage permission
    private fun requestStoragePermission(){
        // Check if the permission was denied and show rationale
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            //call the rationale dialog to tell the user why they need to allow permission request
            showRationaleDialog("Kids Drawing App","Kids Drawing App needs to Access Your External Storage")
        }
        else {
            // You can directly ask for the permission.
            //if it has not been denied then request for permission
            //  The registered ActivityResultCallback gets the result of this request.
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }
    /**  create rationale dialog
     * Shows rationale dialog for displaying why the app needs permission
     * Only shown if the user has denied the permission request previously
     */
    private fun showRationaleDialog(
        title: String,
        message: String,
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    // TODO(Step 4 : Getting and bitmap Exporting the image to your phone storage.)
    /**
     * Create bitmap from view and returns it
     */
    private fun getBitmapFromView(view: View): Bitmap {

        //Define a bitmap with the same size as the view.
        // CreateBitmap : Returns a mutable bitmap with the specified width and height
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.background
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas)
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        }
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }


    // TODO(Step 2 : A method to save the image.)
    private suspend fun saveBitmapFile(mBitmap: Bitmap?):String{
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {

                try {
                    val bytes = ByteArrayOutputStream() // Creates a new byte array output stream.
                    // The buffer capacity is initially 32 bytes, though its size increases if necessary.

                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    /**
                     * Write a compressed version of the bitmap to the specified outputstream.
                     * If this returns true, the bitmap can be reconstructed by passing a
                     * corresponding inputstream to BitmapFactory.decodeStream(). Note: not
                     * all Formats support all bitmap configs directly, so it is possible that
                     * the returned bitmap from BitmapFactory could be in a different bitdepth,
                     * and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque
                     * pixels).
                     *
                     * @param format   The format of the compressed image
                     * @param quality  Hint to the compressor, 0-100. 0 meaning compress for
                     *                 small size, 100 meaning compress for max quality. Some
                     *                 formats, like PNG which is lossless, will ignore the
                     *                 quality setting
                     * @param stream   The outputstream to write the compressed data.
                     * @return true if successfully compressed to the specified stream.
                     */

                    val f = File(
                       /* externalCacheDir?.absoluteFile.toString()*/
                                "/storage/emulated/0/Android/media"+ File.separator + "KidDrawingApp_" + System.currentTimeMillis() / 1000 + ".jpg"
                    )
                    // Here the Environment : Provides access to environment variables.
                    // getExternalStorageDirectory : returns the primary shared/external storage directory.
                    // absoluteFile : Returns the absolute form of this abstract pathname.
                    // File.separator : The system-dependent default name-separator character. This string contains a single character.

                    val fo = FileOutputStream(f) // Creates a file output stream to write to the file represented by the specified object.
                    fo.write(bytes.toByteArray()) // Writes bytes from the specified byte array to this file output stream.
                    fo.close() // Closes this file output stream and releases any system resources associated with this stream. This file output stream may no longer be used for writing bytes.
                    result = f.absolutePath // The file absolute path is return as a result.
                    //We switch from io to ui thread to show a toast
                    runOnUiThread {
                        if (!result.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "File saved successfully :$result",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Something went wrong while saving the file.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                    Log.e("vaibhavi==>","catch"+e.message);
                }
            }
        }
        return result
    }


}