package gg.solara.plugin.robots.bot.behavior

import gg.solara.plugin.robots.bots.Bot
import java.time.Duration
import java.util.LinkedList

/**
 * @author GrowlyX
 * @since 8/13/2024
 */
class ActionController(val bot: Bot)
{
    private var queuedActions = LinkedList<QueueableAction>()
    private var currentAction: QueueableAction? = null

    init
    {
        bot.terminable.with {
            currentAction?.closeAndReportException()
            queuedActions.toList().forEach { it.closeAndReportException() }
            queuedActions.clear()
        }
    }

    fun current() = currentAction

    fun runAction(behavior: ActionBehavior, configure: (ActionConfiguration) -> Unit)
    {
        val actionConfiguration = ActionConfiguration()
        configure(actionConfiguration)

        val queueableAction = QueueableAction(this, behavior = behavior, configuration = actionConfiguration)
        if (actionConfiguration.executionBehavior == ExecutionBehavior.Override)
        {
            currentAction?.closeAndReportException()
            currentAction = queueableAction
            queueableAction.behavior.initialize(queueableAction, bot)
            return
        }

        queuedActions += queueableAction
    }

    fun tick()
    {
        queuedActions.removeIf {
            if (it.isClosed)
            {
                return@removeIf true
            }

            if (it.configuration.evictFromQueueAfterDuration != Duration.ZERO)
            {
                if (it.initialization + it.configuration.evictFromQueueAfterDuration.toMillis() < System.currentTimeMillis())
                {
                    it.closeAndReportException()
                    return@removeIf true
                }
            }

            return@removeIf false
        }

        if (currentAction != null)
        {
            if (currentAction!!.isClosed)
            {
                currentAction = null
            } else
            {
                currentAction!!.behavior.tick(currentAction!!, bot)
                return
            }
        }

        val nextQueueEntry = queuedActions.poll()
            ?: return

        currentAction = nextQueueEntry
        nextQueueEntry.behavior.initialize(nextQueueEntry, bot)
        nextQueueEntry.behavior.tick(nextQueueEntry, bot)
    }
}
