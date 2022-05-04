package com.goldmedal.hrapp.common.customviews.crescento


import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.palette.graphics.Palette

import com.goldmedal.hrapp.R


class CrescentoImageView : AppCompatImageView {
    var mContext: Context? = null
    var mClipPath: Path? = null
    var mWidth = 0
    var mHeight = 0
    var mBitmap: Bitmap? = null
    var tintPaint: Paint? = null
    var shaderPaint: Paint? = null

    /**
     * @param gravity whether TOP or BOTTOM
     */
    var gravity = Gravity.TOP

    /**
     * @param curvatureHeight changes the amount of curve. Default is 50.
     */
    var curvatureHeight = 50

    /**
     * @param tintAmount varies from 0-255
     */
    var mTintAmount = 0

    /**
     * @param tintMode whether manual or automatic. Default is TintMode.AUTOMATIC.
     */
    var mTintMode = TintMode.MANUAL

    /**
     * @param tintPaint color of tint to be applied
     */
    var mTintColor = 0
    var mGradientDirection = 0
    var mGradientStartColor = Color.TRANSPARENT
    var mGradientEndColor = Color.TRANSPARENT
    var mCurvatureDirection = CurvatureDirection.OUTWARD

    object Gravity {
        const val TOP = 0
        const val BOTTOM = 1
    }

    object TintMode {
        const val AUTOMATIC = 0
        const val MANUAL = 1
    }

    object Gradient {
        const val TOP_TO_BOTTOM = 0
        const val BOTTOM_TO_TOP = 1
        const val LEFT_TO_RIGHT = 2
        const val RIGHT_TO_LEFT = 3
    }

    object CurvatureDirection {
        const val OUTWARD = 0
        const val INWARD = 1
    }

    var mPaint: Paint? = null
    private var porterDuffXfermode: PorterDuffXfermode? = null
    private val TAG = "CRESCENTO_IMAGE_VIEW"

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
        shaderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mClipPath = Path()
        val styledAttributes = mContext!!.obtainStyledAttributes(attrs, R.styleable.CrescentoImageView, 0, 0)
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_curvature)) {
            curvatureHeight = styledAttributes.getDimension(R.styleable.CrescentoImageView_curvature, getDpForPixel(curvatureHeight).toFloat()).toInt()
        }
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_crescentoTintAlpha)) {
            if (styledAttributes.getInt(R.styleable.CrescentoImageView_crescentoTintAlpha, 0) <= 255
                    && styledAttributes.getInt(R.styleable.CrescentoImageView_crescentoTintAlpha, 0) >= 0) {
                mTintAmount = styledAttributes.getInt(R.styleable.CrescentoImageView_crescentoTintAlpha, 0)
            }
        }
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_gravity)) {
            gravity = if (styledAttributes.getInt(R.styleable.CrescentoImageView_gravity, 0) == Gravity.BOTTOM) {
                Gravity.BOTTOM
            } else {
                Gravity.TOP
            }
        }
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_crescentoTintMode)) {
            mTintMode = if (styledAttributes.getInt(R.styleable.CrescentoImageView_crescentoTintMode, 0) == TintMode.AUTOMATIC) {
                TintMode.AUTOMATIC
            } else {
                TintMode.MANUAL
            }
        }
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_gradientDirection)) {
            mGradientDirection = styledAttributes.getInt(R.styleable.CrescentoImageView_gradientDirection, 0)
        }

        /* Default start color is transparent*/mGradientStartColor = styledAttributes.getColor(R.styleable.CrescentoImageView_gradientStartColor, Color.TRANSPARENT)

        /* Default end color is transparent*/mGradientEndColor = styledAttributes.getColor(R.styleable.CrescentoImageView_gradientEndColor, Color.TRANSPARENT)
        if (styledAttributes.hasValue(R.styleable.CrescentoImageView_crescentoTintColor)) {
            mTintColor = styledAttributes.getColor(R.styleable.CrescentoImageView_crescentoTintColor, 0)
        }

        /* Default curvature direction would be outward*/mCurvatureDirection = styledAttributes.getInt(R.styleable.CrescentoImageView_curvatureDirection, 0)
        styledAttributes.recycle()
        if (drawable != null) {
            val mBitmapDrawable = drawable as BitmapDrawable
            mBitmap = mBitmapDrawable.bitmap
            pickColorFromBitmap(mBitmap)
        } else {
            if (background != null) {
                if (background !is ColorDrawable) {
                    val mBitmapDrawable = background as BitmapDrawable
                    mBitmap = mBitmapDrawable.bitmap
                    pickColorFromBitmap(mBitmap)
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mClipPath = PathProvider.getClipPath(mWidth, mHeight, curvatureHeight, mCurvatureDirection, gravity)
        ViewCompat.setElevation(this, ViewCompat.getElevation(this))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                outlineProvider = outlineProvider
            } catch (e: Exception) {
                Log.d(TAG, e.message!!)
            }
        }
    }

    private fun pickColorFromBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            Palette.from(it).generate(fun(palette: Palette?) {
            if (mTintMode == TintMode.AUTOMATIC) {
                val defaultColor = 0x000000
                tintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                when {
                    palette?.getDarkMutedColor(defaultColor) !== 0 -> {
                        System.out.println(palette?.getMutedColor(defaultColor))
                        tintPaint!!.color = Color.parseColor("#" + Math.abs(palette!!.getDarkVibrantColor(defaultColor)))
                    }
                    palette?.getDarkVibrantColor(defaultColor) !== 0 -> {
                        System.out.println(palette.getMutedColor(defaultColor))
                        tintPaint!!.color = Color.parseColor("#" + Math.abs(palette.getDarkMutedColor(defaultColor)))
                    }
                    else -> {
                        tintPaint!!.color = Color.WHITE
                    }
                }
                tintPaint!!.alpha = mTintAmount
            } else {
                tintPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                tintPaint!!.color = mTintColor
                tintPaint!!.alpha = mTintAmount
            }
        })
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                try {
                    outline.setConvexPath(PathProvider.getOutlinePath(mWidth, mHeight, curvatureHeight, mCurvatureDirection, gravity))
                } catch (e: Exception) {
                    Log.d("Outline Path", e.message!!)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.saveLayer(0f, 0f, getWidth().toFloat(), getHeight().toFloat(), null, Canvas.ALL_SAVE_FLAG)
        super.onDraw(canvas)
        mPaint!!.xfermode = porterDuffXfermode
        if (tintPaint != null) {
            canvas.drawColor(tintPaint!!.color)
        }
        val mShader: Shader = GradientProvider.getShader(mGradientStartColor, mGradientEndColor, mGradientDirection, canvas.width.toFloat(), canvas.height.toFloat())
        shaderPaint!!.shader = mShader
        canvas.drawPaint(shaderPaint!!)
        canvas.drawPath(mClipPath!!, mPaint!!)
        canvas.restoreToCount(saveCount)
        mPaint!!.xfermode = null
    }

    private fun getDpForPixel(pixel: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixel.toFloat(), mContext!!.resources.displayMetrics).toInt()
    }

    fun setCurvature(height: Int) {
        curvatureHeight = getDpForPixel(height)
    }

    fun setTintColor(tintColor: Int) {
        this.mTintColor = tintColor
    }

    fun setTintMode(tintMode: Int) {
        this.mTintMode = tintMode
    }

    fun setTintAmount(tintAmount: Int) {
        this.mTintAmount = tintAmount
    }

    fun setGradientDirection(direction: Int) {
        mGradientDirection = direction
    }

    fun setGradientStartColor(startColor: Int) {
        mGradientStartColor = startColor
    }

    fun setGradientEndColor(endColor: Int) {
        mGradientEndColor = endColor
    }

    fun setCurvatureDirection(direction: Int) {
        mCurvatureDirection = direction
    }
}