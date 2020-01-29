package dev.sunnyday.core.ui.util

import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 2019-04-01.
 * mail: mail@sunnydaydev.me
 */

object ExDiffUtil {

    fun <T> calculateDiff(
        old: List<T>,
        new: List<T>,
        differ: DiffUtilDiffer<T> = DiffUtilDiffer.default(),
        detectMoves: Boolean = true
    ): DiffUtil.DiffResult {
        val callback = ListDiffUtilCallback(old, new, differ)
        return DiffUtil.calculateDiff(callback, detectMoves)
    }

    fun <T> calculateDiffByUniqueId(
        old: List<T>,
        new: List<T>,
        detectMoves: Boolean = true,
        key: (T) -> Any
    ): DiffUtil.DiffResult {
        val callback = ListDiffUtilCallback(old, new, DiffUtilDiffer.byUniqueKey(key))
        return DiffUtil.calculateDiff(callback, detectMoves)
    }

}

class ListDiffUtilCallback<T>(
    private val old: List<T>,
    private val new: List<T>,
    differ: DiffUtilDiffer<T> = DiffUtilDiffer.default()
) : DifferDiffUtilCallback<T>(differ) {

    companion object {

        fun <T> byUniqueKey(old: List<T>, new: List<T>, key: (T) -> Any): ListDiffUtilCallback<T> =
            ListDiffUtilCallback(old, new, DiffUtilDiffer.byUniqueKey(key))

    }

    override fun getOldItem(position: Int): T = old[position]

    override fun getNewItem(position: Int): T = new[position]

    override fun getOldListSize(): Int = old.size

    override fun getNewListSize(): Int = new.size

}

abstract class DifferDiffUtilCallback<T>(private val differ: DiffUtilDiffer<T>) :
    DiffUtil.Callback() {

    abstract fun getOldItem(position: Int): T

    abstract fun getNewItem(position: Int): T

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        differ.areItemsTheSame(getOldItem(oldItemPosition), getNewItem(newItemPosition))

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        differ.areContentsTheSame(getOldItem(oldItemPosition), getNewItem(newItemPosition))

}

interface DiffUtilDiffer<T> {

    fun areItemsTheSame(old: T, new: T): Boolean

    fun areContentsTheSame(old: T, new: T): Boolean

    companion object {

        fun <T> default(): DiffUtilDiffer<T> = object : DiffUtilDiffer<T> {

            override fun areItemsTheSame(old: T, new: T) = old == new

            override fun areContentsTheSame(old: T, new: T) = true

        }

        fun <T> byUniqueKey(key: (T) -> Any): DiffUtilDiffer<T> = object : DiffUtilDiffer<T> {

            override fun areItemsTheSame(old: T, new: T) = key(old) == key(new)

            override fun areContentsTheSame(old: T, new: T) = old == new

        }

        fun <T : Differable> differable(): DiffUtilDiffer<T> = object : DiffUtilDiffer<T> {

            override fun areItemsTheSame(old: T, new: T) = old.isItemTheSame(new)

            override fun areContentsTheSame(old: T, new: T) = old.isItemContentTheSame(new)

        }

    }

}

interface Differable {

    fun isItemTheSame(new: Any): Boolean

    fun isItemContentTheSame(new: Any): Boolean

}