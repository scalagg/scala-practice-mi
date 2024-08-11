package gg.tropic.practice.queue

import gg.tropic.practice.kit.Kit
import kotlin.properties.Delegates

/**
 * @author GrowlyX
 * @since 8/10/2024
 */
class DSLQueueID
{
    private var kitID: String? = null
    private lateinit var queueType: QueueType
    private var teamSize by Delegates.notNull<Int>()

    fun kit(kit: Kit) = apply {
        kitID = kit.id
    }

    fun teamSize(size: Int) = apply {
        teamSize = size
    }

    fun queueType(queueType: QueueType) = apply {
        this.queueType = queueType
    }

    fun compose() = "${if (kitID == null) "" else "$kitID:"}$queueType:${teamSize}v$teamSize"
}

fun queueId(dsl: DSLQueueID.() -> Unit) = DSLQueueID().apply(dsl).compose()
