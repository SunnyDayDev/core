package dev.sunnyday.core.ui.recyclerview.decor


interface ItemProvider<T> {

    fun getItem(position: Int): T

}

class ListItemProvider<T>(var items: List<T>) : ItemProvider<T> {

    override fun getItem(position: Int): T = items[position]

}

class ValueItemProvider<T>(val item: T) : ItemProvider<T> {

    override fun getItem(position: Int): T = item

}

class PositionItemProvider: ItemProvider<Int> {

    override fun getItem(position: Int): Int = position

}