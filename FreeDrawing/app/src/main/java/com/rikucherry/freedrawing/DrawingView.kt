package com.rikucherry.freedrawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

@SuppressLint("NewApi")
class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null
    private var mBrushSize: Float = 0.toFloat()
    private var color = Color.BLACK
    // store draw paths
    private val mPaths = ArrayList<CustomPath>()
    private val mUndoPath = ArrayList<CustomPath>()
    private var mUndoCountLeft = 10
    private var mRedoCountLeft = 10

    /**
     * A variable for canvas which will be initialized later and used.
     *
     *The Canvas class holds the "draw" calls. To draw something, you need 4 basic components: A Bitmap to hold the pixels, a Canvas to host
     * the draw calls (writing into the bitmap), a drawing primitive (e.g. Rect,
     * Path, text, Bitmap), and a paint (to describe the colors and styles for the
     * drawing)
     */
    private var canvas: Canvas? = null


    init {
        setupDrawing()
    }


    private fun setupDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE //a line
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND //a line shape
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f,mCanvasPaint)
        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when(event.action) {
            // when drag
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize //path thickness is not the same as brush thickness
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(
                    touchX,
                    touchY
                )
            }

            MotionEvent.ACTION_MOVE -> {
                mDrawPath!!.lineTo(touchX, touchY)// Add a line from the last point to the specified point (x,y).
            }

            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
                if (mUndoCountLeft < 10) {
                    mUndoCountLeft++
                }
            }

            else -> return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            newSize, resources.displayMetrics)

        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    fun undoPaths() {
        if (mPaths.size > 0 && mUndoCountLeft > 0) {
            mUndoPath.add(mPaths.removeAt(mPaths.size - 1))
            mUndoCountLeft--
            if (mRedoCountLeft < 10) {
                mRedoCountLeft++
            }
            invalidate()//redraw
        }
    }

    fun redoPaths() {
        if (mUndoPath.size > 0 && mRedoCountLeft > 0) {
            mPaths.add(mUndoPath.removeAt(mUndoPath.size - 1))
            mRedoCountLeft--
            if(mUndoCountLeft < 10) {
                mUndoCountLeft++
            }
            invalidate()
        }
    }

    fun clearPaths() {
        mPaths.clear()
        mUndoPath.clear()
        mUndoCountLeft = 10
        mRedoCountLeft = 10
        invalidate()
    }
    //internal= visible only in the module it was defined
    //inner class = static inner class(java)
    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()
}