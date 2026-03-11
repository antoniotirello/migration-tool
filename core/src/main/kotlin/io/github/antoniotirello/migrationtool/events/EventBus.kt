package io.github.antoniotirello.migrationtool.events

class EventBus<T> {
    private val observers = mutableSetOf<(T) -> Unit>()

    @Synchronized
    fun subscribe(observer: (T) -> Unit) {
        observers.add(observer)
    }

    @Synchronized
    fun unsubscribe(observer: (T) -> Unit) {
        observers.remove(observer)
    }

    @Synchronized
    fun publish(event: T) {
        observers.forEach { it(event) }
    }
}