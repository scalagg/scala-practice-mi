package gg.solara.plugin.robots.bot.menu

import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.group.KitGroup
import gg.tropic.practice.kit.group.KitGroupService
import gg.tropic.practice.map.Map
import gg.tropic.practice.menu.TemplateMapMenu
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

/**
 * @author GrowlyX
 * @since 8/9/2024
 */
class BotMapMenu(
    private val kitGroups: Set<String>,
    private val selectionCallback: (Map?) -> Unit
)
