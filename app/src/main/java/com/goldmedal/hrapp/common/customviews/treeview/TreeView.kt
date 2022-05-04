package com.goldmedal.hrapp.common.customviews.treeview


import android.content.Context
import android.database.DataSetObserver
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.RequiresApi
import com.goldmedal.hrapp.R


class TreeView : AdapterView<TreeAdapter<*>?>, GestureDetector.OnGestureListener {
    var mLinePath = Path()
    var mLinePaint = Paint()
    private var mLineThickness = 0
    private var mLineColor = 0
    private var mLevelSeparation = 0

    /**
     * @return `true` if using same size for each node, `false` otherwise.
     */
    var isUsingMaxSize = false
        private set
    private var mAdapter: TreeAdapter<*>? = null
    private var mMaxChildWidth = 0
    private var mMaxChildHeight = 0
    private var mMinChildHeight = 0
    private var mRect: Rect? = null
    private val mBoundaries = Rect()
    private var mDataSetObserver: DataSetObserver? = null
    private var mGestureDetector: GestureDetector? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun initAttrs(context: Context, attrs: AttributeSet) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TreeView, 0, 0)
        try {
            mLevelSeparation = a.getDimensionPixelSize(R.styleable.TreeView_levelSeparation, DEFAULT_LINE_LENGTH)
            mLineThickness = a.getDimensionPixelSize(R.styleable.TreeView_lineThickness, DEFAULT_LINE_THICKNESS)
            mLineColor = a.getColor(R.styleable.TreeView_lineColor, DEFAULT_LINE_COLOR)
            isUsingMaxSize = a.getBoolean(R.styleable.TreeView_useMaxSize, DEFAULT_USE_MAX_SIZE)
        } finally {
            a.recycle()
        }
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        mGestureDetector = GestureDetector(context, this)
        attrs?.let { initAttrs(context, it) }
        initPaint()
    }

    private fun initPaint() {
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.strokeWidth = mLineThickness.toFloat()
        mLinePaint.color = mLineColor
        mLinePaint.style = Paint.Style.STROKE
        mLinePaint.strokeJoin = Paint.Join.ROUND // set the join to round you want
        //        mLinePaint.setStrokeCap(Paint.Cap.ROUND);      // set the paint cap to round too
        mLinePaint.pathEffect = CornerPathEffect(10f) // set the path effect when they join.
    }

    private fun positionItems() {
        var maxLeft = Int.MAX_VALUE
        var maxRight = Int.MIN_VALUE
        var maxTop = Int.MAX_VALUE
        var maxBottom = Int.MIN_VALUE
        var globalPadding = 0
        var localPadding = 0
        var currentLevel = 0
        for (index in 0 until mAdapter!!.count) {
            val child = mAdapter!!.getView(index, null, this)
            addAndMeasureChild(child)
            val width = child.measuredWidth
            val height = child.measuredHeight
            val screenPosition = mAdapter!!.getScreenPosition(index)
            val node: TreeNode? = mAdapter!!.getNode(index)
            if (height > mMinChildHeight) {
                localPadding = Math.max(localPadding, height - mMinChildHeight)
            }
            if (currentLevel != node!!.level) {
                globalPadding += localPadding
                localPadding = 0
                currentLevel = node.level
            }

            // calculate the size and position of this child
            val left = screenPosition!!.x + screenXCenter
            val top = screenPosition.y * mMinChildHeight + node.level * mLevelSeparation + globalPadding
            val right = left + width
            val bottom = top + height
            child.layout(left, top, right, bottom)
            node.x = left
            node.y = top
            maxRight = Math.max(maxRight, right)
            maxLeft = Math.min(maxLeft, left)
            maxBottom = Math.max(maxBottom, bottom)
            maxTop = Math.min(maxTop, top)
        }
        mBoundaries[maxLeft - (width - Math.abs(maxLeft)) - Math.abs(maxLeft), -height, maxRight] = maxBottom
    }

    /**
     * Returns the index of the child that contains the coordinates given.
     *
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return The index of the child that contains the coordinates. If no child
     * is found then it returns INVALID_INDEX
     */
    private fun getContainingChildIndex(x: Int, y: Int): Int {
        if (mRect == null) {
            mRect = Rect()
        }
        for (index in 0 until childCount) {
            getChildAt(index).getHitRect(mRect)
            if (mRect!!.contains(x, y)) {
                return index
            }
        }
        return INVALID_INDEX
    }

    private fun clickChildAt(x: Int, y: Int) {
        val index = getContainingChildIndex(x, y)
        // no child found at this position
        if (index == INVALID_INDEX) {
            return
        }
        val itemView = getChildAt(index)
        val id = mAdapter!!.getItemId(index)
        performItemClick(itemView, index, id)
    }

    private fun addAndMeasureChild(child: View) {
        var params = child.layoutParams
        if (params == null) {
            params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }
        addViewInLayout(child, -1, params, false)
        var widthSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        var heightSpec = MeasureSpec.makeMeasureSpec(
                MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        if (isUsingMaxSize) {
            widthSpec = MeasureSpec.makeMeasureSpec(
                    mMaxChildWidth, MeasureSpec.EXACTLY)
            heightSpec = MeasureSpec.makeMeasureSpec(
                    mMaxChildHeight, MeasureSpec.EXACTLY)
        }
        child.measure(widthSpec, heightSpec)
    }

    private fun longClickChildAt(x: Int, y: Int) {
        val index = getContainingChildIndex(x, y)
        // no child found at this position
        if (index == INVALID_INDEX) {
            return
        }
        val itemView = getChildAt(index)
        val id = mAdapter!!.getItemId(index)
        val listener = onItemLongClickListener
        listener?.onItemLongClick(this, itemView, index, id)
    }

    private fun drawLines(canvas: Canvas, treeNode: TreeNode) {
        if (treeNode.hasChildren()) {
            for (child in treeNode.children) {
                drawLines(canvas, child)
            }
        }
        if (treeNode.hasParent()) {
            mLinePath.reset()
            val parent = treeNode.parent
            mLinePath.moveTo((treeNode.x + treeNode.width / 2).toFloat(), treeNode.y.toFloat())
            mLinePath.lineTo((treeNode.x + treeNode.width / 2).toFloat(), (treeNode.y - mLevelSeparation / 2).toFloat())
            mLinePath.lineTo((parent!!.x + parent.width / 2).toFloat(), (
                    treeNode.y - mLevelSeparation / 2).toFloat())
            canvas.drawPath(mLinePath, mLinePaint)
            mLinePath.reset()
            mLinePath.moveTo((parent.x + parent.width / 2).toFloat(), (
                    treeNode.y - mLevelSeparation / 2).toFloat())
            mLinePath.lineTo((parent.x + parent.width / 2).toFloat(), (
                    parent.y + parent.height).toFloat())
            canvas.drawPath(mLinePath, mLinePaint)
        }
    }
    /**
     * @return Returns the value of how thick the lines between the nodes are.
     */
    /**
     * Sets a new value for the thickness of the lines between the nodes.
     *
     * @param lineThickness new value for the thickness
     */
    var lineThickness: Int
        get() = mLineThickness
        set(lineThickness) {
            mLineThickness = lineThickness
            initPaint()
            invalidate()
        }
    /**
     * @return Returns the color of the lines between the nodes.
     */
    /**
     * Sets a new color for the lines between the nodes.A change to this value
     * invokes a re-drawing of the tree.
     *
     * @param lineColor the new color
     */
    @get:ColorInt
    var lineColor: Int
        get() = mLineColor
        set(lineColor) {
            mLineColor = lineColor
            initPaint()
            invalidate()
        }
    /**
     * Returns the value of how much space should be used between two levels.
     *
     * @return level separation value
     */
    /**
     * Sets a new value of how much space should be used between two levels. A change to this value
     * invokes a re-drawing of the tree.
     *
     * @param levelSeparation new value for the level separation
     */
    @get:Px
    var levelSeparation: Int
        get() = mLevelSeparation
        set(levelSeparation) {
            mLevelSeparation = levelSeparation
            invalidate()
            requestLayout()
        }

    /**
     * Whether to use the max available size for each node, so all nodes have the same size. A
     * change to this value invokes a re-drawing of the tree.
     *
     * @param useMaxSize `true` if using same size for each node, `false` otherwise.
     */
    fun setUseMaxSize(useMaxSize: Boolean) {
        isUsingMaxSize = useMaxSize
        invalidate()
        requestLayout()
    }

    override fun onDown(event: MotionEvent): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        clickChildAt(e.x.toInt() + scrollX, e.y.toInt() + scrollY)
        return true
    }

    override fun onScroll(downEvent: MotionEvent, event: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val newScrollX = scrollX + distanceX
        val newScrollY = scrollY + distanceY
        if (mBoundaries.contains(newScrollX.toInt(), newScrollY.toInt())) {
            scrollBy(distanceX.toInt(), distanceY.toInt())
        }
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        longClickChildAt(event.x.toInt() + scrollX, event.y.toInt() + scrollY)
    }

    override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return true
    }

    override fun getAdapter(): TreeAdapter<*>? {
        return mAdapter
    }

    override fun setAdapter(adapter: TreeAdapter<*>?) {
        if (mAdapter != null && mDataSetObserver != null) {
            mAdapter!!.unregisterDataSetObserver(mDataSetObserver)
        }
        mAdapter = adapter
        mDataSetObserver = TreeDataSetObserver()
        mAdapter!!.registerDataSetObserver(mDataSetObserver)
        requestLayout()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int,
                          bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mAdapter == null) {
            return
        }
        removeAllViewsInLayout()
        positionItems()
        invalidate()
    }

    override fun getSelectedView(): View? {
        return null
    }

    override fun setSelection(position: Int) {}
    private val screenXCenter: Int
        private get() = pivotX.toInt() - getChildAt(0).measuredWidth / 2

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        val rootNode: TreeNode? = mAdapter!!.getNode(0)
        rootNode?.let { drawLines(canvas, it) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector!!.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mAdapter == null) {
            return
        }
        var maxWidth = 0
        var maxHeight = 0
        var minHeight = Int.MAX_VALUE
        for (i in 0 until mAdapter!!.count) {
            val child = mAdapter!!.getView(i, null, this)
            var params = child.layoutParams
            if (params == null) {
                params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            }
            addViewInLayout(child, -1, params, true)
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            val node: TreeNode? = mAdapter!!.getNode(i)
            val measuredWidth = child.measuredWidth
            val measuredHeight = child.measuredHeight
            node!!.setSize(measuredWidth, measuredHeight)
            maxWidth = Math.max(maxWidth, measuredWidth)
            maxHeight = Math.max(maxHeight, measuredHeight)
            minHeight = Math.min(minHeight, measuredHeight)
        }
        mMaxChildWidth = maxWidth
        mMaxChildHeight = maxHeight
        mMinChildHeight = minHeight
        if (isUsingMaxSize) {
            removeAllViewsInLayout()
            for (i in 0 until mAdapter!!.count) {
                val child = mAdapter!!.getView(i, null, this)
                var params = child.layoutParams
                if (params == null) {
                    params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
                }
                addViewInLayout(child, -1, params, true)
                val widthSpec = MeasureSpec.makeMeasureSpec(mMaxChildWidth, MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(mMaxChildHeight, MeasureSpec.EXACTLY)
                child.measure(widthSpec, heightSpec)
                val node: TreeNode? = mAdapter!!.getNode(i)
                node!!.setSize(child.measuredWidth, child.measuredHeight)
            }
        }
        mAdapter!!.notifySizeChanged()
    }

    private inner class TreeDataSetObserver : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            refresh()
        }

        override fun onInvalidated() {
            super.onInvalidated()
            refresh()
        }

        private fun refresh() {
            invalidate()
            requestLayout()
        }
    }

    companion object {
        private const val DEFAULT_LINE_LENGTH = 100
        private const val DEFAULT_LINE_THICKNESS = 5
        private const val DEFAULT_LINE_COLOR = Color.BLACK
        const val DEFAULT_USE_MAX_SIZE = false
        private const val INVALID_INDEX = -1
    }
}