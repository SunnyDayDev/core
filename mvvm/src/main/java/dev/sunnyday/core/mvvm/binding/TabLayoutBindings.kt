package dev.sunnyday.core.mvvm.binding

import android.graphics.drawable.Drawable
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
    internal val text: BindableSource<String>,
    internal val icon: BindableSource<Drawable>?,
    val value: T
) {

    class Builder<T>(private val value: T) {

        private var textSource: BindableSource<String> = StringSource.Raw("")
        private var iconSource: BindableSource<Drawable>? = null

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

        fun create(): Tab<T> = Tab(textSource, iconSource, value)

    }

}
 
object TabLayoutBindings: Bindings() {

    @JvmStatic
    @BindingAdapter("tabs")
    fun <T> bindTabsTabs(view: TabLayout, tabs: List<Tab<T>>?) =
        view.core<T>().setTabs(tabs)

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedTab")
    fun <T> inverseSelectedTab(view: TabLayout): Tab<T> =
        view.core<T>().selectedTab

    @JvmStatic
    @BindingAdapter("selectedTab", "selectedTabAttrChanged", requireAll = false)
    fun <T> bindTabsSelected(view: TabLayout, tab: Tab<T>, inverse: InverseBindingListener?) =
        view.core<T>().setSelected(tab.value, inverse)

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedValue")
    fun <T> inverseSelectedValue(view: TabLayout): T =
        view.core<T>().selectedTab.value

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

        val selectedTab: Tab<T>
            get() {
                val tabs = this.tabs ?: error("Tabs is null")
                return getSelectedTab(tabs, selectedValue)
            }

        private var tabs: List<Tab<T>>? = null
        private var onTabSelected: ((Int, Tab<T>) -> Unit)? = null
        private var onTabUnselected: ((Int, Tab<T>) -> Unit)? = null
        private var onTabReselected: ((Int, Tab<T>) -> Unit)? = null

        private var selectedInverse: InverseBindingListener? = null

        private var selectedValue: T? = null

        private var isTabsInChanging = false

        init {
            view.addOnTabSelectedListener(this)
        }

        fun setTabs(tabs: List<Tab<T>>?) {

            if (tabs?.isEmpty() == true) {
                error("Tabs should be not empty.")
            }

            if (tabs != null && tabs.distinctBy { it.value } .size != tabs.size) {
                error("Tabs should have unique values.")
            }

            if (this.tabs === tabs) return
            this.tabs = tabs

            notifyChanges(Changes.TABS)

        }

        fun setSelected(value: T, inverse: InverseBindingListener?) {

            if (this.selectedValue == value && this.selectedInverse === inverse) return

            if (this.selectedValue != value) {
                this.selectedValue = value
                notifyChanges(Changes.SELECTED)
            }

            if (this.selectedInverse !== inverse) {
                this.selectedInverse = inverse
                notifyChanges(Changes.SELECTED_INVERSE)
            }

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
            updateSelectedByTab(tab)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) = notifyListener(onTabUnselected, tab)

        override fun onTabSelected(tab: TabLayout.Tab) {
            notifyListener(onTabSelected, tab)
            updateSelectedByTab(tab)
        }

        override fun applyChanges(changes: List<Changes>) {
            changes.forEach {
                Do exhaustive when(it) {
                    Changes.TABS -> applyChangedTabs()
                    Changes.SELECTED -> applyChangedSelected()
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

        private fun updateSelectedByTab(tab: TabLayout.Tab) {

            if (isTabsInChanging) return

            val tabs = this.tabs ?: return

            val newSelected = tabs[tab.position]

            if (selectedValue == newSelected.value) return

            selectedValue = newSelected.value
            selectedInverse?.onChange()

        }

        private fun applyChangedTabs() {

            isTabsInChanging = true

            view.removeAllTabs()

            val tabs = this.tabs ?: return

            val context = view.context

            tabs.forEach {
                view.addTab(view.newTab().apply {
                    text = it.text.get(context)
                    icon = it.icon?.get(context)
                })
            }

            val selectedValue = this.selectedValue
            val selectedTabIndex = tabs.indexOfFirst { it.value == selectedValue }

            if (selectedTabIndex != -1) {
                view.getTabAt(selectedTabIndex)?.select()
            } else {
                this.selectedValue = tabs.first().value
                view.getTabAt(0)?.select()
                selectedInverse?.onChange()
            }

            isTabsInChanging = false

        }

        private fun applyChangedSelected() {

            val tabs = this.tabs ?: return
            val tab = view.getTabAt(tabs.indexOf(getSelectedTab(tabs, selectedValue))) ?: return

            if (!tab.isSelected) {
                tab.select()
            }

        }

        private fun getSelectedTab(tabs: List<Tab<T>>, selectedValue: T?): Tab<T> =
            tabs.find { it.value == selectedValue } ?: tabs.first()

        internal enum class Changes(override val applyingOrder: Int): Change {
            TABS(1), SELECTED(2), SELECTED_INVERSE(3), LISTENERS(4)
        }

    }

}