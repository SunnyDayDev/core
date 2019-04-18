package dev.sunnyday.core.mvvm.binding

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.tabs.TabLayout
import dev.sunnyday.core.mvvm.R
import dev.sunnyday.core.runtime.Do
import dev.sunnyday.core.runtime.noop

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-18.
 * mail: mail@sunnydaydev.me
 */

class Tab<T> constructor(
    internal val text: StringSource,
    internal val icon: DrawableSource?,
    val value: T
) {

    class Builder<T>(private val value: T) {

        private lateinit var textSource: StringSource
        private var iconSource: DrawableSource? = null

        fun text(@StringRes resId: Int) = apply {
            textSource = StringSource.Res(resId)
        }

        fun text(text: String) = apply {
            textSource = StringSource.Raw(text)
        }

        fun icon(@DrawableRes resId: Int) = apply {
            iconSource = DrawableSource.Res(resId)
        }

        fun icon(icon: Drawable) = apply {
            iconSource = DrawableSource.Raw(icon)
        }

        fun create(): Tab<T> {

            if (!this::textSource.isInitialized) error("Text should be setted.")

            return Tab(textSource, iconSource, value)

        }


    }

}

fun Context.findActivity(): Activity? {

    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }

    return null

}
 
object TabLayoutBindings: Bindings() {

    @JvmStatic
    @BindingAdapter("tabs")
    fun <T> bindTabsTabs(view: TabLayout, tabs: List<Tab<T>>?) =
        view.core<T>().setTabs(tabs)

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedTab")
    fun <T> inverseSelectedTab(view: TabLayout): Tab<T> =
        view.core<T>().selected

    @JvmStatic
    @BindingAdapter("selectedTab", "selectedTabAttrChanged", requireAll = false)
    fun <T> bindTabsSelected(view: TabLayout, tab: Tab<T>, inverse: InverseBindingListener?) =
        view.core<T>().setSelected(tab, inverse)

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedValue")
    fun <T> inverseSelectedValue(view: TabLayout): T =
        view.core<T>().selected.value


    @JvmStatic
    @BindingAdapter("selectedValue", "selectedValueAttrChanged", requireAll = false)
    fun <T> bindTabsValueSelected(view: TabLayout, value: T, inverse: InverseBindingListener?) =
        view.core<T>().setSelected(value, inverse)

    @JvmStatic
    @BindingAdapter("onTabSelected")
    fun <T> bindTabsOnTabSelected(
        view: TabLayout,
        listener: ((Int, Tab<T>) -> Unit)?
    ) = view.core<T>().setOnTabSelected(listener)

    @JvmStatic
    @BindingAdapter("onTabReselected")
    fun <T> bindTsetOnTabReselected(
        view: TabLayout,
        listener: ((Int, Tab<T>) -> Unit)?
    ) = view.core<T>().setOnTabReselected(listener)

    @JvmStatic
    @BindingAdapter("onTabUnselected")
    fun <T> bindTsetOnTabUnselected(
        view: TabLayout,
        listener: ((Int, Tab<T>) -> Unit)?
    ) = view.core<T>().setOnTabUnselected(listener)

    private fun <T> TabLayout.core(): TabsCore<T> =
        getListener(R.id.binding_tabLayout_tabs_core) ?: TabsCore<T>(this).also {
            setListener(R.id.binding_tabLayout_tabs_core, it)
        }

    private class TabsCore<T>(view: TabLayout): BindableCore<TabLayout, TabsCore.Changes>(view),
        TabLayout.OnTabSelectedListener {

        lateinit var selected: Tab<T>

        private val uiHandler = Handler(Looper.getMainLooper())

        private var tabs: List<Tab<T>>? = null
        private var onTabSelected: ((Int, Tab<T>) -> Unit)? = null
        private var onTabUnselected: ((Int, Tab<T>) -> Unit)? = null
        private var onTabReselected: ((Int, Tab<T>) -> Unit)? = null

        private var selectedInverse: InverseBindingListener? = null

        init {
            view.addOnTabSelectedListener(this)
        }

        fun setTabs(tabs: List<Tab<T>>?) {
            if (this.tabs === tabs) return
            this.tabs = tabs
            notifyChanges(Changes.TABS, Changes.SELECTED)
        }

        fun setSelected(tab: Tab<T>, inverse: InverseBindingListener?) {

            if ((this::selected.isInitialized && this.selected === tab) &&
                this.selectedInverse === inverse) return

            if (!this::selected.isInitialized || this.selected !== tab) {
                this.selected = tab
                notifyChanges(Changes.SELECTED)
            }

            if (this.selectedInverse !== inverse) {
                this.selectedInverse = inverse
                notifyChanges(Changes.SELECTED_INVERSE)
            }

        }

        fun setSelected(value: T, inverse: InverseBindingListener?) {

            val tab = tabs?.find { it.value == value } ?: return
            setSelected(tab, inverse)

        }

        fun setOnTabSelected(listener: ((Int, Tab<T>) -> Unit)?) {
            if (this.onTabSelected === tabs) return
            this.onTabSelected = listener
            notifyChanges(Changes.LISTENERS)
        }

        fun setOnTabUnselected(listener: ((Int, Tab<T>) -> Unit)?) {
            if (this.onTabUnselected === tabs) return
            this.onTabUnselected = listener
            notifyChanges(Changes.LISTENERS)
        }


        fun setOnTabReselected(listener: ((Int, Tab<T>) -> Unit)?) {
            if (this.onTabReselected === tabs) return
            this.onTabReselected = listener
            notifyChanges(Changes.LISTENERS)
        }

        override fun onTabReselected(tab: TabLayout.Tab) {
            notifyListener(onTabReselected, tab)
            updateSelected(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) = notifyListener(onTabUnselected, tab)

        override fun onTabSelected(tab: TabLayout.Tab) {
            notifyListener(onTabSelected, tab)
            updateSelected(tab)
        }

        override fun applyChanges(changes: List<Changes>) {
            changes.forEach {
                Do exhaustive when(it) {
                    Changes.TABS -> onTabsChanged()
                    Changes.SELECTED -> onSelectedChanged()
                    Changes.LISTENERS -> noop()
                    Changes.SELECTED_INVERSE -> noop()
                }
            }
        }

        private fun notifyListener(listener: ((Int, Tab<T>) -> Unit)?, tab: TabLayout.Tab) {

            listener ?: return
            val tabValue = tabs?.getOrNull(tab.position) ?: return

            listener(tab.position, tabValue)

        }

        private fun updateSelected(tab: TabLayout.Tab) {

            val tabs = this.tabs ?: return

            val newSelected = tabs[tab.position]

            if (this::selected.isInitialized && selected === newSelected) return

            selected = newSelected
            selectedInverse?.onChange()

        }

        private fun onTabsChanged() {

            view.removeAllTabs()

            val tabs = this.tabs ?: return

            val context = view.context

            tabs.forEach {
                view.addTab(view.newTab().apply {
                    text = it.text.get(context)
                    icon = it.icon?.get(context)
                })
            }

            if (this::selected.isInitialized) {


            }

        }

        private fun onSelectedChanged() {

            val tabs = this.tabs ?: return

            view.getTabAt(tabs.indexOf(selected))?.select()

        }

        internal enum class Changes(override val applyingOrder: Int): BindableCore.Change {
            TABS(1), SELECTED(2), SELECTED_INVERSE(3), LISTENERS(4)
        }

    }

}