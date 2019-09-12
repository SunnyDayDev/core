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
                                      topView: View?,
                                      bottomView: View?,
                                      parent: RecyclerView
    ) {

        if (topView == null || bottomView == null) {
            rect.setEmpty()
        } else {
            rect.set(0, 0, parent.width, dividerDrawable.intrinsicHeight)
        }

    }

    override fun drawDivider(left: Int, top: Int, right: Int, bottom: Int, canvas: Canvas) {

        dividerDrawable.setBounds(left, top, right, bottom)
        dividerDrawable.draw(canvas)

    }

}

abstract class AbstractHorizontalDividerDecoration: RecyclerView.ItemDecoration() {

    private val viewsDividersRects = WeakHashMap<View, ViewDividerRects>()

    abstract fun calculateDividerRect(rect: Rect,
                                      topView: View?, // null when bottomView is first item in adapter
                                      bottomView: View?, // null when topView is last item in adapter
                                      parent: RecyclerView
    )

    abstract fun drawDivider(left: Int, top: Int, right: Int, bottom: Int, canvas: Canvas)

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        parent.forEach { view ->

            val dividerRects = viewsDividersRects[view] ?: return

            if (dividerRects.top.height() > 0) {

                val dividerRect = dividerRects.top

                val viewTop = view.top +
                        (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin

                drawDivider(
                    dividerRect.left,
                    viewTop - dividerRect.height(),
                    dividerRect.right,
                    viewTop,
                    canvas
                )

            }

            if (dividerRects.bottom.height() > 0) {

                val dividerRect = dividerRects.bottom

                val viewBottom = view.bottom +
                        (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin

                drawDivider(
                    dividerRect.left,
                    viewBottom,
                    dividerRect.right,
                    viewBottom + dividerRect.height(),
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

            val index = parent.indexOfChild(view)

            val previousView = if (index != 0) parent[index - 1] else null
            val nextView = if(index < parent.childCount - 1) parent[index + 1] else null

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

    private data class ViewDividerRects(
        val top: Rect = Rect(),
        val topAnchorView: Weak<View> = Weak(),
        val bottom: Rect = Rect(),
        val bottomAnchorView: Weak<View> = Weak()
    )

}