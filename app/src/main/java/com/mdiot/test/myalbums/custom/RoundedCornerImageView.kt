package com.mdiot.test.myalbums.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.mdiot.test.myalbums.R

class RoundedCornerImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_RADIUS = 0.0f
    }

    private val cornerRadius: Float

    private val path: Path = Path()
    private lateinit var rectF: RectF

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedCornerImageView)
        cornerRadius = typedArray.getDimension(R.styleable.RoundedCornerImageView_corner_radius, DEFAULT_RADIUS)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        rectF = RectF(0f, 0f, this.width.toFloat(), this.height.toFloat())
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas?.clipPath(path)
        super.onDraw(canvas)
    }

    /**
     * Make the view always squared
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val tmp = if (widthMeasureSpec > heightMeasureSpec) heightMeasureSpec else widthMeasureSpec
        super.onMeasure(tmp, tmp)
        val size = MeasureSpec.getSize(tmp)
        setMeasuredDimension(size, size)
    }
}