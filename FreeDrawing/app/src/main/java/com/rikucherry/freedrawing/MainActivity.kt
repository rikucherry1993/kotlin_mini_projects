package com.rikucherry.freedrawing

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_brush_size.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private var mImageButtonCurrentPaint: ImageButton? = null

    companion object {
        private const val PERMISSION_CODE_EXTERNAL_STORAGE = 1
    }

    private fun requestPermission() {
        //when ContextCompat.checkSelfPermission() returned PERMISSION_DENIED
        //and either of read/write external storage permission is not granted
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).toString()
            )
        ) {
            Toast.makeText(this, "应用需要您添加读取图片的权限", Toast.LENGTH_LONG).show()

        }

        // request permissions
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            PERMISSION_CODE_EXTERNAL_STORAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "权限已开启。您可以自定义背景图片。", Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "应用需要您添加读取图片的权限", Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isReadPermissionGranted(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //NOTE: new way to handle activity result
        val galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    try {
                        if (intent!!.data != null) {
                            // set background image visible
                            iv_background.visibility = View.VISIBLE
                            iv_background.setImageURI(intent!!.data)
                        } else {
                            // something went wrong
                            Toast.makeText(this, "图片未能加载", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        drawing_view.setSizeForBrush(15.toFloat())
        //access elements of linear layout like accessing an arrayList
        mImageButtonCurrentPaint = ll_palette[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            //different from getting from resource
            ContextCompat.getDrawable(this, R.drawable.palette_selected)
        )

        ib_brush.setOnClickListener {
            showBrushSizeChooser()
        }

        ib_gallery.setOnClickListener {
            if (isReadPermissionGranted()) {
                // read photo from gallery
                val pickPhotoIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                galleryLauncher.launch(pickPhotoIntent)
            } else {
                //permission denied
                requestPermission()
            }
        }

        ib_undo.setOnClickListener {
            drawing_view.undoPaths()
        }

        ib_redo.setOnClickListener {
            drawing_view.redoPaths()
        }

        ib_clear.setOnClickListener {
            drawing_view.clearPaths()
        }

        ib_save.setOnClickListener {
            if (isReadPermissionGranted()){
                BitmapAsyncTask(getBitmapFromView(fl_draw_view_container)).execute()
            } else {
                requestPermission()
            }
        }
    }

    private fun showBrushSizeChooser() {
        val brushDialog = Dialog(this)
        brushDialog.setContentView(R.layout.dialog_brush_size)
        brushDialog.setTitle("画笔大小:")

        val smallBtn = brushDialog.ib_small_brush
        val mediumBtn = brushDialog.ib_medium_brush
        val largeBtn = brushDialog.ib_large_brush

        smallBtn.setOnClickListener {
            drawing_view.setSizeForBrush(8.toFloat())
            brushDialog.dismiss()
        }

        mediumBtn.setOnClickListener {
            drawing_view.setSizeForBrush(15.toFloat())
            brushDialog.dismiss()
        }
        largeBtn.setOnClickListener {
            drawing_view.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }

        brushDialog.show()
    }

    fun paintClicked(view: View) {
        //按下的颜色不是当前颜色
        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            // tag!!!!!
            val colorTag = imageButton.tag.toString()
            drawing_view.setColor(colorTag)

            // change button appearance
            imageButton.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.palette_selected)
            )

            mImageButtonCurrentPaint!!.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.palette_normal)
            )

            mImageButtonCurrentPaint = view
        }

    }

    private fun getBitmapFromView(view: View): Bitmap {
        val returnBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnBitmap)
        canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return returnBitmap
    }

    private inner class BitmapAsyncTask(val mBitmap: Bitmap) : AsyncTask<Any, Void, String>() {

        private lateinit var mProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgress()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result = ""

            if (mBitmap != null) {
                try {
                    val bytes = ByteArrayOutputStream()
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    val file = File(
//                        externalCacheDir!!.absoluteFile.toString()
                        "/storage/emulated/0/DCIM/Camera"
                                + File.separator + "FreeDrawingApp_"
                                + System.currentTimeMillis() / 1000 + ".png"
                    )
                    val fo = FileOutputStream(file)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    result = file.absolutePath
                } catch(e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            dismissProgress()
            super.onPostExecute(result)
            if (!result!!.isEmpty()) {
                Toast.makeText(this@MainActivity,
                "您的作品已成功导出至：$result"
                , Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@MainActivity,
                    "导出失败！"
                    , Toast.LENGTH_LONG).show()
            }

            //share our image!
            MediaScannerConnection.scanFile(this@MainActivity, arrayOf(result), null) {
                path, uri ->
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "image/png"

                startActivity(
                    Intent.createChooser(shareIntent, "Share")
                )
            }
        }

        private fun showProgress() {
            mProgressDialog = Dialog(this@MainActivity)
            mProgressDialog.setContentView(R.layout.dialog_custom_progress)
            mProgressDialog.show()
        }

        private fun dismissProgress() {
            mProgressDialog.dismiss()
        }
    }
}