package gg.tropic.practice.kit.administration.specific

import com.cryptomorin.xseries.XMaterial
import gg.tropic.practice.kit.Kit
import gg.tropic.practice.kit.KitService
import gg.tropic.practice.kit.feature.FeatureFlag
import gg.tropic.practice.kit.feature.GameLifecycle
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/28/2024
 */
class KitMetadataEditMenu(private val kit: Kit) : PaginatedMenu()
{
    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons = mutableMapOf<Int, Button>()
        val lifecycle = kit.lifecycleType ?: GameLifecycle.SoulBound
        for (flag in FeatureFlag.entries)
        {
            val isIncompatible = flag.incompatibleWith().filter { kit.features(it) }
            val notApplicableYet = flag.requires.filter { !kit.features(it) }

            buttons[buttons.size] = ItemBuilder
                .of(when (true)
                {
                    (lifecycle !in flag.availableLifecycles) -> XMaterial.COAL_BLOCK
                    isIncompatible.isNotEmpty() -> XMaterial.RED_WOOL
                    notApplicableYet.isNotEmpty() -> XMaterial.LIGHT_GRAY_WOOL
                    (kit.features(flag)) -> XMaterial.LIME_WOOL
                    else -> XMaterial.GRAY_WOOL
                })
                .name("${CC.RED}${flag.name}")
                .apply {
                    if (lifecycle !in flag.availableLifecycles)
                    {
                        addToLore(
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
                            "${CC.B_RED}Incompatible:",
                            "${CC.RED}$lifecycle not supported!",
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
                        )
                    } else if (isIncompatible.isNotEmpty())
                    {
                        addToLore(
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
                            "${CC.B_GOLD}Incompatible Features:",
                            "${CC.RED}Remove the following features",
                            *isIncompatible
                                .map { "${CC.WHITE}- ${it.name}" }
                                .toTypedArray(),
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
                        )
                    } else if (notApplicableYet.isNotEmpty())
                    {
                        addToLore(
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
                            "${CC.B_PINK}Required Features Missing:",
                            "${CC.PINK}Add the following features",
                            *notApplicableYet
                                .map { "${CC.WHITE}- ${it.name}" }
                                .toTypedArray(),
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
                        )
                    } else if (kit.features(flag))
                    {
                        addToLore(
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
                            "${CC.GREEN}Enabled",
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
                        )
                    } else
                    {
                        addToLore(
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------",
                            "${CC.GRAY}Available",
                            "${CC.GRAY}${CC.STRIKE_THROUGH}------------"
                        )
                    }
                }
                .apply {
                    if (flag.description.isNotEmpty())
                    {
                        flag.description
                            .onEach {
                                addToLore("${CC.GRAY}$it")
                            }
                        addToLore("")
                    }
                }
                .apply {
                    if (flag.schema.isNotEmpty())
                    {
                        addToLore("${CC.GREEN}Metadata:")

                        flag.schema.forEach { (k, v) ->
                            val configured = kit.featureConfigNullable(flag, k)
                            addToLore("${CC.WHITE}$k: ${
                                if (configured != null) "$configured ${CC.GRAY}($v)" else v
                            }")
                        }

                        addToLore("")
                    }
                }
                .toButton { _, _ ->
                    if (lifecycle !in flag.availableLifecycles)
                    {
                        Button.playFail(player)
                        return@toButton
                    }
                    if (isIncompatible.isNotEmpty())
                    {
                        Button.playFail(player)
                        return@toButton
                    }

                    if (notApplicableYet.isNotEmpty())
                    {
                        Button.playFail(player)
                        return@toButton
                    }

                    if (kit.features(flag))
                    {
                        Button.playSuccess(player)
                        kit.features.remove(flag)

                        with(KitService.cached()) {
                            KitService.cached().kits[kit.id] = kit
                            KitService.sync(this)

                            openMenu(player)
                        }
                        return@toButton
                    }

                    Button.playSuccess(player)
                    kit.features[flag] = mutableMapOf()

                    with(KitService.cached()) {
                        KitService.cached().kits[kit.id] = kit
                        KitService.sync(this)

                        openMenu(player)
                    }
                }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) = "Editing features of ${kit.id}..."
    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                SpecificKitAdminMenu(kit).openMenu(player)
            }
        }
    }
}
