package com.goldmedal.hrapp.common.customviews.crescento


import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import com.goldmedal.hrapp.R


class CrescentoContainer : RelativeLayout {
    var mContext: Context? = null
    var mClipPath: Path? = null
    var mOutlinePath: Path? = null
    var mWidth = 0
    var mHeight = 0

    /**
     * @param curvatureHeight changes the amount of curve. Default is 50.
     */
    var curvatureHeight = 50
    var mPaint: Paint? = null
    private var porterDuffXfermode: PorterDuffXfermode? = null
    private val TAG = "CRESCENTO_CONTAINER"

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mContext = context
        porterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = Color.WHITE
        mClipPath = Path()
        mOutlinePath = Path()
        val styledAttributes = mContext!!.obtainStyledAttributes(attrs, R.styleable.CrescentoImageView, 0, 0)
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_curvature)) {
            curvatureHeight = styledAttributes.getDimension(R.styleable.CrescentoImageView_curvature, getDpForPixel(curvatureHeight).toFloat()).toInt()
        }
        styledAttributes.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mClipPath = PathProvider.getClipPath(mWidth, mHeight, curvatureHeight, 0, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setElevation(this, ViewCompat.getElevation(this))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                setOutlineProvider(outlineProvider)
            } catch (e: Exception) {
                Log.d(TAG, e.message!!)
            }
        }
    }


    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                try {
                    outline.setConvexPath(PathProvider.getOutlinePath(mWidth, mHeight, curvatureHeight, 0, 0))
                } catch (e: Exception) {
                    Log.d("Outline Path", e.message!!)
                }
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.saveLayer(0f, 0f, getWidth().toFloat(), getHeight().toFloat(), null, Canvas.ALL_SAVE_FLAG)
        super.dispatchDraw(canvas)
        mPaint!!.xfermode = porterDuffXfermode
        canvas.drawPath(mClipPath!!, mPaint!!)
        canvas.restoreToCount(saveCount)
        mPaint!!.xfermode = null
    }

    private fun getDpForPixel(pixel: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel.toFloat(), mContext!!.resources.displayMetrics).toInt()
    }
}