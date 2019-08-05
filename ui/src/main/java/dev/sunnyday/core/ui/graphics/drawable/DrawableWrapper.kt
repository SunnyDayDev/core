package dev.sunnyday.core.ui.graphics.drawable

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

class DrawableWrapper(drawable: Drawable? = null) : Drawable(), Drawable.Callback {
    
    private val fallbackDrawable = ColorDrawable(Color.TRANSPARENT)
    private val values = State()

    var wrappedDrawable: Drawable? = null
        set(drawable) {

            field?.callback = null

            field = drawable

            drawable?.also {
                it.callback = this
                updateDrawableState(it)
            }

            invalidateSelf()

        }

    init {
        wrappedDrawable = drawable
    }

    override fun draw(canvas: Canvas) {
        wrappedDrawable?.draw(canvas) ?: fallbackDrawable.draw(canvas)
    }

    override fun onBoundsChange(bounds: Rect) {
        values.boundsValue = bounds
        wrappedDrawable?.bounds = bounds
    }

    override fun setChangingConfigurations(configs: Int) {
        values.changingConfigurations = configs
        wrappedDrawable?.changingConfigurations = configs
    }

    override fun getChangingConfigurations(): Int {
        return wrappedDrawable?.changingConfigurations
            ?: values.changingConfigurations
            ?: fallbackDrawable.changingConfigurations
    }

    override fun setDither(dither: Boolean) {
        values.isDitherValue = dither
        @Suppress("DEPRECATION")
        wrappedDrawable?.setDither(dither)
    }

    override fun setFilterBitmap(filter: Boolean) {
        values.isFilterBitmapValue = filter
        wrappedDrawable?.isFilterBitmap = filter
    }

    override fun setAlpha(alpha: Int) {
        values.alpha = alpha
        wrappedDrawable?.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        values.colorFilterValue = cf
        wrappedDrawable?.colorFilter = cf
    }

    override fun isStateful(): Boolean {
        return wrappedDrawable?.isStateful ?: false
    }

    override fun setState(stateSet: IntArray): Boolean {
        values.state = stateSet
        return wrappedDrawable?.setState(stateSet) ?: false
    }

    override fun getState(): IntArray {
        return wrappedDrawable?.state ?: values.state ?: fallbackDrawable.state
    }

    override fun jumpToCurrentState() {
        val drawable = wrappedDrawable ?: return
        @Suppress("DEPRECATION")
        DrawableCompat.jumpToCurrentState(drawable)
    }

    override fun getCurrent(): Drawable {
        return wrappedDrawable?.current ?: fallbackDrawable.current
    }

    override fun setVisible(visible: Boolean, restart: Boolean): Boolean {
        values.isVisibleValue = visible
        return super.setVisible(visible, restart) || wrappedDrawable?.setVisible(visible, restart) ?: false
    }

    override fun getOpacity(): Int {
        @Suppress("DEPRECATION")
        return wrappedDrawable?.opacity ?: fallbackDrawable.opacity
    }

    override fun getTransparentRegion(): Region? {
        return wrappedDrawable?.transparentRegion ?: fallbackDrawable.transparentRegion
    }

    override fun getIntrinsicWidth(): Int {
        return wrappedDrawable?.intrinsicWidth ?: fallbackDrawable.intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return wrappedDrawable?.intrinsicHeight ?: fallbackDrawable.intrinsicHeight
    }

    override fun getMinimumWidth(): Int {
        return wrappedDrawable?.minimumWidth ?: fallbackDrawable.minimumWidth
    }

    override fun getMinimumHeight(): Int {
        return wrappedDrawable?.minimumHeight ?: fallbackDrawable.minimumHeight
    }

    override fun getPadding(padding: Rect): Boolean {
        return wrappedDrawable?.getPadding(padding) ?: fallbackDrawable.getPadding(padding)
    }

    /**
     * {@inheritDoc}
     */
    override fun invalidateDrawable(who: Drawable) {
        invalidateSelf()
    }

    /**
     * {@inheritDoc}
     */
    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
        scheduleSelf(what, `when`)
    }

    /**
     * {@inheritDoc}
     */
    override fun unscheduleDrawable(who: Drawable, what: Runnable) {
        unscheduleSelf(what)
    }

    override fun onLevelChange(level: Int): Boolean {
        values.level = level
        return wrappedDrawable?.setLevel(level) ?: false
    }

    override fun setAutoMirrored(mirrored: Boolean) {
        values.isAutoMirrored = mirrored
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setAutoMirrored(drawable, mirrored)
    }

    override fun isAutoMirrored(): Boolean {
        val drawable = wrappedDrawable ?: fallbackDrawable
        return DrawableCompat.isAutoMirrored(drawable)
    }

    override fun setTint(tint: Int) {
        values.tintValue = tint
        values.tintListValue = null
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setTint(drawable, tint)
    }

    override fun setTintList(tint: ColorStateList?) {
        values.tintListValue = tint
        values.tintValue = null
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setTintList(drawable, tint)
    }

    override fun setTintMode(tintMode: PorterDuff.Mode?) {
        values.tintModeValue = tintMode
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setTintMode(drawable, tintMode ?: PorterDuff.Mode.SRC_IN)
    }

    override fun setHotspot(x: Float, y: Float) {
        values.hotspotValue = Pair(x, y)
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setHotspot(drawable, x, y)
    }

    override fun setHotspotBounds(left: Int, top: Int, right: Int, bottom: Int) {
        values.hotspotBoundsValue = Rect(left, top, right, bottom)
        val drawable = wrappedDrawable ?: return
        DrawableCompat.setHotspotBounds(drawable, left, top, right, bottom)
    }

    private fun updateDrawableState(drawable: Drawable) {

        values.level?.let { drawable.level = it }
        values.changingConfigurations?.let { drawable.changingConfigurations = it }
        values.alpha?.let { drawable.alpha = it }
        values.state?.let { drawable.state = it }
        values.isVisibleValue?.let { drawable.setVisible(it, true) }
        values.boundsValue?.let { drawable.bounds = it }
        values.colorFilterValue?.let { values.colorFilterValue = it }
        values.isDitherValue?.let {
            @Suppress("DEPRECATION")
            drawable.setDither(it)
        }
        values.isFilterBitmapValue?.let { drawable.isFilterBitmap = it }
        values.tintValue?.let { DrawableCompat.setTint(drawable, it) }
        values.tintListValue?.let { DrawableCompat.setTintList(drawable, it) }
        values.hotspotValue?.let { (x, y) -> DrawableCompat.setHotspot(drawable, x, y) }
        values.hotspotBoundsValue?.let {
            DrawableCompat.setHotspotBounds(drawable, it.left, it.top, it.right, it.bottom)
        }
        values.isAutoMirrored?.let { DrawableCompat.setAutoMirrored(drawable, it) }

    }

    private class State {

        var level: Int? = null
        var alpha: Int? = null
        var state: IntArray? = null
        var changingConfigurations: Int? = null
        var tintListValue: ColorStateList? = null
        var colorFilterValue: ColorFilter? = null
        var tintValue: Int? = null
        var tintModeValue: PorterDuff.Mode? = null
        var hotspotValue: Pair<Float, Float>? = null
        var hotspotBoundsValue: Rect? = null
        var isDitherValue: Boolean? = null
        var isFilterBitmapValue: Boolean? = null
        var boundsValue: Rect? = null
        var isVisibleValue: Boolean? = null
        var isAutoMirrored: Boolean? = null

    }

}