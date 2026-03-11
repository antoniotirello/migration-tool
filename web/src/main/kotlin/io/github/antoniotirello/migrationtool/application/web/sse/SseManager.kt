package io.github.antoniotirello.migrationtool.application.web.sse

import io.github.antoniotirello.migrationtool.context.AppContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArraySet

@Component
class SseManager(
    appContext: AppContext
) {

    private val emitters = CopyOnWriteArraySet<SseEmitter>()

    init {
        println("EventBus subscribe: ${appContext.eventBus.hashCode()}")
        appContext.eventBus.subscribe { event ->
            broadcast(event)
        }
    }

    fun register(): SseEmitter {

        val emitter = SseEmitter(Long.MAX_VALUE)

        emitters.add(emitter)

        emitter.onCompletion { emitters.remove(emitter) }
        emitter.onTimeout { emitter.complete(); emitters.remove(emitter) }
        emitter.onError { emitter.complete(); emitters.remove(emitter) }

        return emitter
    }

    private fun broadcast(event: Any) {
        emitters.forEach { emitter ->
            try {
                emitter.send(event)
            } catch (e: Exception) {
                emitter.complete()
                emitters.remove(emitter)
            }
        }
    }
}