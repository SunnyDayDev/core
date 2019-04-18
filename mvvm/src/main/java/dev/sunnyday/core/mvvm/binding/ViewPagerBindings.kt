package dev.sunnyday.core.mvvm.binding

import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.children
import androidx.databinding.BindingAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dev.sunnyday.core.mvvm.R

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-18.
 * mail: mail@sunnydaydev.me
 */
 
object ViewPagerBindings: Bindings() {

    const val PAGE_TAG = "page"

    @JvmStatic
    val autoPage: PagerAdapter get() = AutoPagerAdapter()

    @JvmStatic
    @BindingAdapter("adapter")
    fun bindAdapter(view: ViewPager, adapter: PagerAdapter) {
        if (adapter is BindableViewPagerAdapter) {
            adapter.setupByViewPager(view)
        }
        view.adapter = adapter
    }

    @JvmStatic
    @BindingAdapter("pageTitle")
    fun bindPageTitle(view: View, title: String) {
        view.setTag(R.id.binding__autopageradapter_page_title, title)
    }

    @JvmStatic
    @BindingAdapter("pageTitle")
    fun bindPageTitle(view: View, @StringRes title: Int) {
        view.setTag(R.id.binding__autopageradapter_page_title, title)
    }

    private class AutoPagerAdapter : PagerAdapter(), BindableViewPagerAdapter {

        private var views: List<View> = emptyList()

        override fun setupByViewPager(pager: ViewPager) {

            views = pager.children
                .filter { it.tag == PAGE_TAG }
                .toList()

            views.forEach(pager::removeView)

        }

        override fun instantiateItem(collection: ViewGroup, position: Int): Any =
            views[position].also(collection::addView)

        override fun getCount(): Int = views.size

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean = arg0 === arg1

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) =
            container.removeView(`object` as View)

        override fun getPageTitle(position: Int): CharSequence? {

            val view = views[position]

            return when (val titleTag = view.getTag(R.id.binding__autopageradapter_page_title)) {
                is Int -> view.context.getString(titleTag)
                is String -> titleTag
                else -> null
            }

        }

    }

}

interface BindableViewPagerAdapter {

    fun setupByViewPager(pager: ViewPager)

}