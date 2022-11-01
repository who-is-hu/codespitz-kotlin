package day3

import kotlinx.datetime.Instant


class User(private val name: String) {
    init {
        Looper.users.add(this)
    }

    private val items = hashSetOf<Item>()

    fun addItem(vararg item: Item) {
        items += item
    }

    fun send(now: Instant) {
        items.forEach { it.send(now) }
    }

    fun search(title: String? = null, content: String? = null): Collection<Item> {
        var target: Collection<Item> = items
        if (title != null) {
            target = target.filter { title in it.title }
        }
        if (content != null) {
            target = target.filter { content in it.content }
        }
        return target
    }
}