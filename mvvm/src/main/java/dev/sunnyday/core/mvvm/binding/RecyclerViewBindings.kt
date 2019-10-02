package dev.sunnyday.core.mvvm.binding

import androidx.core.view.ViewCompat
import androidx.core.view.get
import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.github.nitrico.lastadapter.BaseType
import com.github.nitrico.lastadapter.LastAdapter
import com.github.nitrico.lastadapter.TypeHandler
import dev.sunnyday.core.mvvm.R
import dev.sunnyday.core.mvvm.observable.Command
import dev.sunnyday.core.util.Weak
import kotlin.reflect.KClass

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnyday.dev
 */

object RecyclerViewBindings: Bindings() {

    private val coreTag by lazy { ViewCompat.generateViewId() }

    // region Commands

    @JvmStatic
    @BindingAdapter("scrollToPosition")
    fun bindScrollToPosition(view: RecyclerView, position: Command<Int>) {
        position.handle(view::scrollToPosition)
    }

    // endregion

    @JvmStatic
    @BindingAdapter("resetAdapterCommand")
    fun bindResetAdapter(view: RecyclerView, command: Command<Unit>) {
        command.handle { view.adapter = view.adapter }
    }

    // region Items

    @JvmStatic
    @BindingAdapter("items")
    fun <T: Any> bindItems(view: RecyclerView, items: List<T>) = view.adapterCore.setItems(items)

    @JvmStatic
    @BindingAdapter("itemsLayoutMap")
    fun bindItemsMap(view: RecyclerView, map: ItemsMap) = view.adapterCore.setItemsMap(map)

    @JvmStatic
    @BindingAdapter("itemsStableId")
    fun bindItemsStableId(view: RecyclerView, stableId: Boolean) = view.adapterCore.setStableId(stableId)

    @JvmStatic
    @BindingAdapter("recreateAdapterOnItemsChanges")
    fun bindRecreateAdapterOnItemsChanges(view: RecyclerView, recreate: Boolean) {
        view.adapterCore.recreateAdapterOnItemsChanges = recreate
    }

    @JvmStatic
    @BindingAdapter(
            value = [
                "onItemMoved",
                "onItemActionStateChanged",
                "canItemDropOver"
            ],
            requireAll = false
    )
    fun bindItemMovedListener(
            view: RecyclerView,
            movedCallback: OnTouchItemMovedCallback?,
            actionStateCallback: OnTouchItemActionStateCallback?,
            dropOverCallback: OnTouchItemCanDropOverCallback?
    ) {

        ListenerUtil.getListener<ItemTouchHelper>(
                view, R.id.binding_recyclerview_touch_item_helper
        )?.attachToRecyclerView(null)
        ListenerUtil.trackListener(view, null, R.id.binding_recyclerview_touch_item_helper)

        if (movedCallback == null && actionStateCallback == null && dropOverCallback == null) {
            return
        }

        val itemTouchCallback = object : ItemTouchHelper.Callback() {

            override fun getMovementFlags(recyclerView: RecyclerView,
                                          viewHolder: RecyclerView.ViewHolder): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(recyclerView: RecyclerView,
                                viewHolder: RecyclerView.ViewHolder,
                                target: RecyclerView.ViewHolder): Boolean =
                    movedCallback?.onItemMoved(
                            viewHolder.adapterPosition, target.adapterPosition) ?: true

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }

            override fun canDropOver(recyclerView: RecyclerView,
                                     current: RecyclerView.ViewHolder,
                                     target: RecyclerView.ViewHolder): Boolean =
                    dropOverCallback?.canDropOver(
                            current.adapterPosition, target.adapterPosition) ?: true

            override fun isLongPressDragEnabled(): Boolean = true

            override fun isItemViewSwipeEnabled(): Boolean = false

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                viewHolder ?: return

                actionStateCallback?.onItemSelectionChanged(
                        viewHolder.adapterPosition,
                        ItemTouchActionState.parse(actionState)
                )

            }

            override fun clearView(recyclerView: RecyclerView,
                                   viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                actionStateCallback?.onItemSelectionChanged(
                        viewHolder.adapterPosition,
                        ItemTouchActionState.IDLE
                )

            }

        }

        val helper = ItemTouchHelper(itemTouchCallback)

        ListenerUtil.trackListener(view, helper, R.id.binding_recyclerview_touch_item_helper)

        helper.attachToRecyclerView(view)

    }

    // endregion

    // region ItemDecoration

    @JvmStatic
    @BindingAdapter("itemsDecoration")
    fun bindItemDecoration(view: RecyclerView, decoration: ItemDecoration) =
        bindItemDecorations(view, listOf(decoration))

    @JvmStatic
    @BindingAdapter("itemsDecoration")
    fun bindItemDecorations(view: RecyclerView, decoration: List<ItemDecoration>) {

        view.getListener<List<ItemDecoration>>(R.id.binding_recyclerview_items_decoration)
            ?.forEach(view::removeItemDecoration)

        decoration.forEach(view::addItemDecoration)

        view.setListener(R.id.binding_recyclerview_items_decoration, decoration)

    }

    // endregion

    @JvmStatic
    @BindingAdapter(
            value = [
                "onVisibleItemsPositionsChanged",
                "onFirstVisibleItemsPositionsChanged",
                "onLastVisibleItemPositionsChanged"
            ],
            requireAll = false
    )
    fun bindLastVisiblePosition(view: RecyclerView,
                                callback: AdapterPositionListener.Callback?,
                                first: AdapterPositionListener.SingleCallback?,
                                last: AdapterPositionListener.SingleCallback?) {

        val current: AdapterPositionListener? =
                ListenerUtil.getListener(view, R.id.binding_recyclerview_binding_visible_position)

        if (current?.isSame(callback, first, last) == true) return

        current?.also(view::removeOnScrollListener)

        if (callback == null && first == null && last == null) return

        val newListener = AdapterPositionListener(callback, first, last)

        view.addOnScrollListener(newListener)

        ListenerUtil.trackListener(view,
                newListener, R.id.binding_recyclerview_binding_visible_position)

    }

    private val RecyclerView.firstVisibleItemPosition: Int get() {
        if (childCount == 0) return -1
        val firstView = this[0]
        return getChildAdapterPosition(firstView)
    }

    private val RecyclerView.lastVisibleItemPosition: Int get() {
        if (childCount == 0) return -1
        val lastView = this[childCount - 1]
        return getChildAdapterPosition(lastView)
    }

    class AdapterPositionListener(
            private val callback: Callback?,
            private val firstCallback: SingleCallback?,
            private val lastCallback: SingleCallback?
    ): RecyclerView.OnScrollListener() {

        private var handledFirst: Int? = null
        private var handledLast: Int? = null

        fun isSame(callback: Callback?,
                   first: SingleCallback?,
                   last: SingleCallback?): Boolean =
                callback == this.callback && first == firstCallback && last == lastCallback

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val first = recyclerView.firstVisibleItemPosition
            val last = recyclerView.lastVisibleItemPosition

            val firstChanged = handledFirst != first
            val lastChanged = handledLast != last

            if (firstCallback != null && firstChanged) {
                recyclerView.post {
                    firstCallback.onItemVisiblePositionChanged(first)
                }
            }

            if (lastCallback != null && lastChanged) {
                recyclerView.post {
                    lastCallback.onItemVisiblePositionChanged(last)
                }
            }

            if (callback != null && (firstChanged || lastChanged)) {
                recyclerView.post {
                    callback.onItemsVisiblePositionChanged(first, last)
                }
            }

            if (firstChanged) handledFirst = first
            if (lastChanged) handledLast = last

        }

        interface Callback {

            fun onItemsVisiblePositionChanged(first: Int, last: Int)

        }

        interface SingleCallback {

            fun onItemVisiblePositionChanged(position: Int)

        }

    }

    private val RecyclerView.adapterCore get() =
        getOrSetListener(R.id.binding_recyclerview_adapter_core) { AdapterCore(this) }

    // region Classes

    private class AdapterCore(view: RecyclerView): BindableCore<RecyclerView, BindableCore.Change.Simple>(view) {

        private var items = Weak<List<Any>>()
        private var stableId: Boolean = false
        private var itemsMap: ItemsMap? = null
        var recreateAdapterOnItemsChanges = false

        fun <T: Any> setItems(items: List<T>) {
            if (this.items.value === items && !recreateAdapterOnItemsChanges) return
            this.items.value = items
            notifyChanges(post = itemsMap == null)
        }

        fun setItemsMap(map: ItemsMap?) {
            if (this.itemsMap?.id == map?.id) return
            this.itemsMap = map
            notifyChanges()
        }

        fun setStableId(stableId: Boolean) {
            if (this.stableId == stableId) return
            this.stableId = stableId
            notifyChanges()
        }

        override fun applyChanges(changes: List<Change.Simple>) {

            val items = this.items.value
            val itemsMap = this.itemsMap

            if (items == null || itemsMap == null) {
                if (view.adapter != null) {
                    view.adapter = null
                }
                return
            }

            val handler = object : TypeHandler {

                private val alternate = mutableMapOf<KClass<out Any>, ItemsMap.Item>()

                override fun getItemType(item: Any, position: Int): BaseType? {

                    val klazz = item::class

                    val itemMapping = itemsMap.map[klazz] ?: alternate[klazz] ?: itemsMap.map.keys
                        .single { it.java.isAssignableFrom(klazz.java) }
                        .also { alternate[klazz] = itemsMap.map[it]!! }
                        .let { alternate[klazz]!! }

                    return BaseType(itemMapping.layout, itemMapping.variable)

                }

            }

            view.adapter = LastAdapter(items, stableId)
                .handler(handler)
                .into(view)

        }

    }

    interface OnTouchItemMovedCallback {

        fun onItemMoved(fromIndex: Int, toIndex: Int): Boolean

    }

    interface OnTouchItemActionStateCallback {

        fun onItemSelectionChanged(position: Int, type: ItemTouchActionState)

    }

    interface OnTouchItemCanDropOverCallback {

        fun canDropOver(current: Int, target: Int): Boolean

    }

    enum class ItemTouchActionState {

        DRAG, SWIPE, IDLE;

        internal companion object {

            fun parse(action: Int): ItemTouchActionState = when(action) {
                ItemTouchHelper.ACTION_STATE_DRAG -> DRAG
                ItemTouchHelper.ACTION_STATE_SWIPE -> SWIPE
                else -> IDLE
            }

        }

    }

    class ItemsMap (internal val id: Int = 0) {

        internal val map = mutableMapOf<KClass<out Any>, Item>()

        inline fun <reified T: Any> map(variable: Int, layout: Int): ItemsMap {
            map(T::class, variable = variable, layout = layout)
            return this
        }

        fun <T: Any> map(clazz: KClass<T>, variable: Int, layout: Int): ItemsMap {
            map[clazz] = Item(variable = variable, layout = layout)
            return this
        }

        data class Item(
                val variable: Int,
                val layout: Int
        )

    }

    // endregion

}