package com.simple.meditrack.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import com.simple.coreapp.ui.view.round.RoundViewDelegate
import kotlin.math.max

class RoundNestedScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : NestedScrollView(context, attrs) {

    private var delegate: RoundViewDelegate = RoundViewDelegate(this, context, attrs)

    fun getDelegate(): RoundViewDelegate {
        return this.delegate
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (this.delegate.isWidthHeightEqual && (this.width > 0) && (this.height > 0)) {

            val max: Int = max(this.width, this.height)
            val measureSpec = MeasureSpec.makeMeasureSpec(max, MeasureSpec.EXACTLY)

            super.onMeasure(measureSpec, measureSpec)
        } else {

            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {

        super.onLayout(changed, left, top, right, bottom)

        if (this.delegate.isRadiusHalfHeight) {
            this.delegate.cornerRadius = this.height / 2
            this.delegate.setBgSelector()
        } else {
            this.delegate.setBgSelector()
        }
    }
}