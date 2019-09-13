package dev.sunnyday.core.ui.recyclerview.decor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import dev.sunnyday.core.util.Weak
import java.util.*

open class DrawablHorizontalDividerDecoration(
    override val dividerDrawable: Drawable
): AbstractDrawableHorizontalDividerDecoration()

open class DrawableResHorizontalDividerDecoration(
    private val drawableId: Int
): AbstractDrawableHorizontalDividerDecoration() {

    override lateinit var dividerDrawable: Drawable

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        initDrawable(parent.context)

        super.onDraw(canvas, parent, state)

    }

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {

        initDrawable(parent.context)

        super.getItemOffsets(outRect, view, parent, state)

    }

    private fun initDrawable(context: Context) {
        if (!::dividerDrawable.isInitialized) {
            dividerDrawable = ContextCompat.getDrawable(context, drawableId)!!
        }
    }

}

abstract class AbstractDrawableHorizontalDividerDecoration: AbstractHorizontalDividerDecoration() {

    protected abstract val dividerDrawable: Drawable

    override fun calculateDividerRect(rect: Rect,
                                      viewAbove: View?,
                                      viewBelow: View?,
                                      parent: RecyclerView
    ) {

        if (viewAbove == null || viewBelow == null) {
            rect.setEmpty()
        } else {
            rect.set(0, 0, parent.width, dividerDrawable.intrinsicHeight)
        }

    }

    override fun drawDivider(rect: Rect,
                             viewAbove: View?,
                             viewBelow: View?,
                             canvas: Canvas) {

        dividerDrawable.setBounds(rect.left, rect.top, rect.right, rect.bottom)
        dividerDrawable.draw(canvas)

    }

}

abstract class AbstractHorizontalDividerDecoration: RecyclerView.ItemDecoration() {

    private val viewsDividersRects = WeakHashMap<View, ViewDividerRects>()

    private val drawDividerRect = Rect()

    abstract fun calculateDividerRect(rect: Rect,
                                      viewAbove: View?, // null when viewBelow is first item in adapter
                                      viewBelow: View?, // null when viewAbove is last item in adapter
                                      parent: RecyclerView)

    abstract fun drawDivider(rect: Rect,
                             viewAbove: View?,
                             viewBelow: View?,
                             canvas: Canvas)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        parent.forEach { view ->

            val dividerRects = viewsDividersRects[view] ?: return

            if (dividerRects.top.height() > 0) {

                val dividerRect = dividerRects.top

                val viewTop = view.top +
                        (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin

                drawDividerRect.set(
                    dividerRect.left,
                    viewTop - dividerRect.height(),
                    dividerRect.right,
                    viewTop
                )

                drawDivider(
                    drawDividerRect,
                    parent.previousChildView(view),
                    view,
                    canvas
                )

            }

            if (dividerRects.bottom.height() > 0) {

                val dividerRect = dividerRects.bottom

                val viewBottom = view.bottom +
                        (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

                drawDividerRect.set(
                    dividerRect.left,
                    viewBottom,
                    dividerRect.right,
                    viewBottom + dividerRect.height()
                )


                drawDivider(
                    drawDividerRect,
                    view,
                    parent.nextChildView(view),
                    canvas
                )

            }

        }

    }

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val dividerRects = updateViewsDividerRects(view, parent, state)

        outRect.top = dividerRects.top.height()
        outRect.bottom = dividerRects.bottom.height()

    }

    private fun updateViewsDividerRects(view: View,
                                        parent: RecyclerView,
                                        state: RecyclerView.State): ViewDividerRects {

        val itemPosition = parent.getChildAdapterPosition(view)

        val dividerRects = getViewDividerRects(view)

        val isFirst = itemPosition == 0
        val isLast = itemPosition == state.itemCount - 1

        if (isFirst) {

            calculateDividerRect(dividerRects.top, null, view, parent)
            dividerRects.topAnchorView.value = null

        }

        if (isLast) {

            calculateDividerRect(dividerRects.bottom, view, null, parent)
            dividerRects.bottomAnchorView.value = null

        }

        if (!(isFirst && isLast)) {


            val previousView = parent.previousChildView(view)

            if (previousView != null) {

                val previousDividersRects = getViewDividerRects(previousView)

                if (previousDividersRects.bottomAnchorView !== view) {

                    calculateDividerRect(dividerRects.top, previousView, view, parent)
                    dividerRects.topAnchorView.value = previousView

                    previousDividersRects.bottom.setEmpty()
                    previousDividersRects.bottomAnchorView.value = view

                }

            } else {

                dividerRects.topAnchorView.value = null

            }

            val nextView = parent.nextChildView(view)

            if (nextView != null) {

                val nextDividersRects = getViewDividerRects(nextView)

                if (nextDividersRects.topAnchorView !== view) {

                    calculateDividerRect(dividerRects.bottom, view, nextView, parent)
                    dividerRects.bottomAnchorView.value = nextView

                    nextDividersRects.top.setEmpty()
                    nextDividersRects.topAnchorView.value = view

                }

            } else {

                dividerRects.bottomAnchorView.value = null

            }

        }

        return dividerRects

    }

    private fun getViewDividerRects(view: View): ViewDividerRects =
        viewsDividersRects[view] ?: ViewDividerRects().also {
            viewsDividersRects[view] = it
        }

    protected fun RecyclerView.previousChildView(view: View): View? {


        val index = indexOfChild(view)

        return if (index != 0) this[index - 1] else null


    }

    protected fun RecyclerView.nextChildView(view: View): View? {


        val index = indexOfChild(view)

        return if(index < this.childCount - 1) this[index + 1] else null


    }

    private data class ViewDividerRects(
        val top: Rect = Rect(),
        val topAnchorView: Weak<View> = Weak(),
        val bottom: Rect = Rect(),
        val bottomAnchorView: Weak<View> = Weak()
    )

}