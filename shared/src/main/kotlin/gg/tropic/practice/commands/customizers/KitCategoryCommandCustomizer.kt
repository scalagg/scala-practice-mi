package gg.tropic.practice.commands.customizers

import gg.scala.commons.acf.ConditionFailedException
import gg.scala.commons.annotations.commands.customizer.CommandManagerCustomizer
import gg.scala.commons.command.ScalaCommandManager
import gg.tropic.practice.kit.category.KitCategory
import gg.tropic.practice.kit.category.KitCategoryService
import net.evilblock.cubed.util.CC

/**
 * @author GrowlyX
 * @since 2/18/2023
 */
object KitCategoryCommandCustomizer
{
    @CommandManagerCustomizer
    fun customize(manager: ScalaCommandManager)
    {
        manager.commandContexts
            .registerContext(KitCategory::class.java) {
                val arg = it.popFirstArg()

                KitCategoryService.cached()
                    .findById(arg)
                    ?: throw ConditionFailedException(
                        "No kit category matching ${CC.YELLOW}$arg${CC.RED} exists."
                    )
            }

        manager.commandCompletions
            .registerAsyncCompletion("categories") {
                KitCategoryService.cached().categories.map(KitCategory::id)
            }
    }
}