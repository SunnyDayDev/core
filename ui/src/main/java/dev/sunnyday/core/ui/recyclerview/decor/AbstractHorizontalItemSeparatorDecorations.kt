package dev.sunnyday.core.ui.recyclerview.decor

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.math.max
import kotlin.math.min

abstract class AbstractDrawableHorizontalItemSeparatorDecoration<T>(
    itemProvider: ItemProvider<T>
) : AbstractHorizontalItemSeparatorDecoration<T>(itemProvider) {

    open fun getTopSeparatorDrawable(
        itemBelow: T,
        parent: RecyclerView,
        state: RecyclerView.State): Drawable? = null

    abstract fun getSeparatorDrawable(
        itemAbove: T,
        itemBelow: T,
        parent: RecyclerView,
        state: RecyclerView.State): Drawable?

    open fun getBottomSeparatorDrawable(
        itemAbove: T,
        parent: RecyclerView,
        state: RecyclerView.State): Drawable? = null

    override fun calculateSeparatorRect(rect: Rect,
                                        itemAbove: T,
                                        itemBelow: T,
                                        parent: RecyclerView,
                                        state: RecyclerView.State) {
        val drawable = getSeparatorDrawable(itemAbove, itemBelow, parent, state) ?: return
        calculateSeparatorRect(rect, drawable, parent)
    }

    override fun drawTopSeparator(rect: Rect,
                                  itemBelow: T,
                                  canvas: Canvas,
                                  parent: RecyclerView,
                                  state: RecyclerView.State) {
        val drawable = getTopSeparatorDrawable(itemBelow, parent, state) ?: return
        drawable.bounds = rect
        drawable.draw(canvas)
    }

    override fun drawSeparator(rect: Rect,
                               itemAbove: T,
                               itemBelow: T,
                               canvas: Canvas,
                               parent: RecyclerView,
                               state: RecyclerView.State) {
        val drawable = getSeparatorDrawable(itemAbove, itemBelow, parent, state) ?: return
        drawable.bounds = rect
        drawable.draw(canvas)
    }

    override fun drawBottomSeparator(rect: Rect,
                                     itemAbove: T,
                                     canvas: Canvas,
                                     parent: RecyclerView,
                                     state: RecyclerView.State) {
        val drawable = getBottomSeparatorDrawable(itemAbove, parent, state) ?: return
        drawable.bounds = rect
        drawable.draw(canvas)
    }

    protected fun calculateSeparatorRect(rect: Rect, drawable: Drawable, parent: RecyclerView) {
        rect.bottom = drawable.intrinsicHeight
        rect.right = parent.width
    }

}

abstract class AbstractHorizontalItemSeparatorDecoration<T>(
    private val itemProvider: ItemProvider<T>
) : AbstractHorizontalSeparatorDecoration() {

    abstract fun calculateSeparatorRect(
        rect: Rect,
        itemAbove: T,
        itemBelow: T,
        parent: RecyclerView,
        state: RecyclerView.State)

    open fun calculateTopSeparatorRect(
        rect: Rect,
        itemBelow: T,
        parent: RecyclerView,
        state: RecyclerView.State) = rect.setEmpty()

    open fun calculateBottomSeparatorRect(
        rect: Rect,
        itemAbove: T,
        parent: RecyclerView,
        state: RecyclerView.State) = rect.setEmpty()

    open fun drawTopSeparator(
        rect: Rect,
        itemBelow: T,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State) { /* no-op */ }

    abstract fun drawSeparator(
        rect: Rect,
        itemAbove: T,
        itemBelow: T,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State)

    open fun drawBottomSeparator(
        rect: Rect,
        itemAbove: T,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State) { /* no-op */ }


    override fun calculateSeparatorRects(top: Rect,
                                         bottom: Rect,
                                         view: View,
                                         parent: RecyclerView,
                                         state: RecyclerView.State) {

        val adapterPosition = parent.getChildAdapterPosition(view)

        if (adapterPosition == 0) {

            calculateTopSeparatorRect(
                top,
                itemProvider.getItem(adapterPosition),
                parent,
                state)

        } else {

            calculateSeparatorRect(
                top,
                itemProvider.getItem(adapterPosition - 1),
                itemProvider.getItem(adapterPosition),
                parent,
                state)

        }

        if (adapterPosition == state.itemCount - 1) {

            calculateBottomSeparatorRect(
                bottom,
                itemProvider.getItem(adapterPosition),
                parent,
                state)

        } else {

            calculateSeparatorRect(
                bottom,
                itemProvider.getItem(adapterPosition),
                itemProvider.getItem(adapterPosition + 1),
                parent,
                state)

        }

    }

    override fun drawTopSeparatorAbove(rect: Rect,
                                       viewBelow: View,
                                       canvas: Canvas,
                                       parent: RecyclerView,
                                       state: RecyclerView.State) {

        val viewAdapterPosition = parent.getChildAdapterPosition(viewBelow)

        if (viewAdapterPosition == 0) {

            drawTopSeparator(
                rect,
                itemProvider.getItem(viewAdapterPosition),
                canvas,
                parent,
                state)

        } else {

            drawSeparator(
                rect,
                itemProvider.getItem(viewAdapterPosition - 1),
                itemProvider.getItem(viewAdapterPosition),
                canvas,
                parent,
                state)

        }

    }

    override fun drawSeparatorBelow(rect: Rect,
                                    viewAbove: View,
                                    canvas: Canvas,
                                    parent: RecyclerView,
                                    state: RecyclerView.State) {

        val viewAdapterPosition = parent.getChildAdapterPosition(viewAbove)

        if (viewAdapterPosition == state.itemCount - 1) {

            drawBottomSeparator(
                rect,
                itemProvider.getItem(viewAdapterPosition),
                canvas,
                parent,
                state)

        } else {

            drawSeparator(
                rect,
                itemProvider.getItem(viewAdapterPosition),
                itemProvider.getItem(viewAdapterPosition + 1),
                canvas,
                parent,
                state)

        }

    }

}

abstract class AbstractHorizontalSeparatorDecoration : RecyclerView.ItemDecoration() {

    private val viewsDividersRects = WeakHashMap<View, ViewSeparatorsRects>()

    private val drawDividerRect = Rect()

    abstract fun calculateSeparatorRects(
        top: Rect,
        bottom: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State)

    abstract fun drawTopSeparatorAbove(
        rect: Rect,
        viewBelow: View,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State)

    abstract fun drawSeparatorBelow(
        rect: Rect,
        viewAbove: View,
        canvas: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        val firstView = parent.children.firstOrNull() ?: return

        val firstTopSeparatorRect = getViewDividerRects(firstView).top

        if (!firstTopSeparatorRect.isEmpty) {

            drawDividerRect.set(firstTopSeparatorRect)
            drawDividerRect.offsetTo(
                drawDividerRect.left, firstView.topWithMargin - drawDividerRect.height())

            drawTopSeparatorAbove(drawDividerRect, firstView, canvas, parent, state)

        }

        parent.children.drop(1).forEach { view ->

            val previousView = parent.previousChildView(view)!!

            val aboveRect = getViewDividerRects(previousView).bottom

            val belowRect = getViewDividerRects(view).top

            when {
                aboveRect.isEmpty && belowRect.isEmpty -> drawDividerRect.setEmpty()
                belowRect.isEmpty -> drawDividerRect.set(aboveRect)
                aboveRect.isEmpty -> drawDividerRect.set(belowRect)
                else -> drawDividerRect.set(
                    min(aboveRect.left, belowRect.left),
                    min(aboveRect.top, belowRect.top),
                    max(aboveRect.right, belowRect.right),
                    max(aboveRect.bottom, belowRect.bottom)
                )
            }

            drawDividerRect.offsetTo(
                drawDividerRect.left, view.topWithMargin - drawDividerRect.height())
            drawTopSeparatorAbove(drawDividerRect, view, canvas, parent, state)

        }

        val lastView = parent.let {
            if (it.childCount > 0) it.getChildAt(it.childCount - 1)
            else null
        }

        lastView ?: return

        val lastBottomSeparatorRect = getViewDividerRects(lastView).bottom

        if (!lastBottomSeparatorRect.isEmpty) {

            drawDividerRect.set(lastBottomSeparatorRect)
            drawDividerRect.offsetTo(drawDividerRect.left, lastView.bottomWithMargin)

            drawSeparatorBelow(drawDividerRect, lastView, canvas, parent, state)

        }

    }

    override fun getItemOffsets(outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val rects = getViewDividerRects(view)

        calculateSeparatorRects(rects.top, rects.bottom, view, parent, state)

        outRect.top = rects.top.height()
        outRect.bottom = rects.bottom.height()

    }

    private fun getViewDividerRects(view: View): ViewSeparatorsRects =
        viewsDividersRects[view] ?: ViewSeparatorsRects().also {
            viewsDividersRects[view] = it
        }

    protected val View.topWithMargin: Int
        get() =
            top - (layoutParams as ViewGroup.MarginLayoutParams).topMargin


    protected val View.bottomWithMargin: Int
        get() =
            bottom + (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

    protected fun RecyclerView.previousChildView(view: View): View? {


        val index = indexOfChild(view)

        return if (index != 0) this[index - 1] else null


    }

    protected fun RecyclerView.nextChildView(view: View): View? {

        val index = indexOfChild(view)

        return if (index < this.childCount - 1) this[index + 1] else null


    }

    protected data class ViewSeparatorsRects(
        val top: Rect = Rect(),
        val bottom: Rect = Rect()
    )

}