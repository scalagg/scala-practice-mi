package land.esta.plugin.ssb.estapractice

import gg.tropic.practice.Globals.PLAYER_BOT_UNIQUE_ID
import gg.tropic.practice.games.GameImpl
import gg.tropic.practice.kit.feature.FeatureFlag
import land.esta.plugin.ssb.EstaRobotsPlugin.Companion.instance
import land.esta.plugin.ssb.bot.setting.setting.root
import land.esta.plugin.ssb.botevents.Event
import land.esta.plugin.ssb.botevents.events.BotAttackLandEvent
import land.esta.plugin.ssb.botevents.events.BotDamageEvent
import land.esta.plugin.ssb.bots.Bot
import land.esta.plugin.ssb.utils.Color
import land.esta.plugin.ssb.utils.RandomUtil
import land.esta.plugin.ssb.utils.objects.EventRunnable
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import net.evilblock.cubed.ScalaCommonsSpigot
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.logging.Logger

object EstaPracticeIntegrator
{
    fun spawn(
        location: Location,
        game: GameImpl,
        player: Player
    ): Bot?
    {
        val selectedBot = ScalaCommonsSpigot.instance.kvConnection.sync()
            .get(
                "tropicpractice:bot-selection:${game.humanSide().players[0]}"
            )

        if (selectedBot == null)
        {
            Logger.getGlobal().info("No bot configuration was found!")
            return null
        }

        val split = selectedBot.split(":")
        val settings = loadSettingsFromSelection(
            player,
            split[0],
            split.getOrNull(1)
        )?.deepCopy()

        if (settings == null)
        {
            Logger.getGlobal().info("No bot configuration was found!")
            return null
        }

        if (settings.randomizerEnabled == true)
        {
            for (value in settings.settings.values)
            {
                // enable the proxy randomize method when calling getObject()
                value.shouldLocalRandomize = true
            }
        }

        var name = settings.botName
        if (name.length > 16)
        {
            name = name.substring(0, 16)
        }

        val bot = Bot(
            location,
            Color.code(name),
            player,
            game,
            settings,
            game.kit
        )

        val noDamageMatch = game.flagMetaData(FeatureFlag.DoNotTakeDamage, "doDamageTick") == "true"

        if (noDamageMatch)
        {
            Events
                .subscribe(EntityDamageByEntityEvent::class.java)
                .handler { event ->
                    if (event.entity is Player)
                    {
                        if (event.entity == player)
                        {
                            event.damage = 0.0
                        }
                    }
                }
                .bindWith(game)
        }

        bot.damageManager.isAllowGlobalAttacks = false
        bot.damageManager.isNoDamage = noDamageMatch
        bot.movements.isMove = false

        if (game.flag(FeatureFlag.DeathOnLiquidInteraction))
        {
            bot.movements.isEdgeDetector = true
            bot.movements.collision.waterEnterEvent = Runnable { bot.silentKill() }
        }

        if (game.flag(FeatureFlag.DeathBelowYAxis))
        {
            bot.movements.isEdgeDetector = true
        }

        //Boxing support
        if (game.flag(FeatureFlag.WinWhenNHitsReached))
        {
            val robot = game.robotSide()
            val metadata = game.flagMetaData(FeatureFlag.WinWhenNHitsReached, "hits")!!
                .toInt()

            bot.eventListener = (EventRunnable { event: Event? ->
                if (event is BotDamageEvent)
                {
                    if (event.source != null && event.source.entity != null &&
                        event.source.entity.uniqueID == player.uniqueId
                    )
                    {
                        val playerCounter = game.counter(player)
                        game.humanSide().gameLifecycleArbitraryObjectiveProgress += 1
                        game.humanSide().playerCombos.compute(player.uniqueId) { _, combo -> (combo ?: 0) + 1 }

                        playerCounter.increment("totalHits")
                        playerCounter.increment("combo")

                        if (playerCounter.valueOf("combo") > playerCounter.valueOf("highestCombo"))
                        {
                            playerCounter.update(
                                "highestCombo",
                                playerCounter.valueOf("combo")
                            )
                        }

                        val dataPlayer = instance!!.dataManager.getPlayer(player)
                        dataPlayer?.hit()

                        robot.playerCombos[PLAYER_BOT_UNIQUE_ID] = 0

                        if (game.humanSide().gameLifecycleArbitraryObjectiveProgress >= metadata)
                        {
                            bot.kill()
                        }
                    } else
                    {
                        event.isCanceled = true
                    }
                }

                if (event is BotAttackLandEvent)
                {
                    robot.gameLifecycleArbitraryObjectiveProgress += 1
                    game.humanSide().playerCombos.clear()

                    val dataPlayer = instance!!.dataManager.getPlayer(player)
                    dataPlayer?.botHit()

                    if (robot.gameLifecycleArbitraryObjectiveProgress >= metadata)
                    {
                        game.complete(robot, "")
                    }
                }
            })
        }

        if (game.flag(FeatureFlag.WinWhenNHitsReached))
        {
            if (bot.isAlive)
            {
                bot.movements.isMove = true
            }
        } else
        {
            Schedulers
                .sync()
                .runLater({
                    if (bot.isAlive)
                    {
                        bot.movements.isMove = true
                    }
                }, 5 * 20L)
                .bindWith(bot.terminable)
        }

        Events
            .subscribe(PotionSplashEvent::class.java)
            .handler {
                val affectedEntity = it.affectedEntities
                    .firstOrNull { affected -> affected == bot.npc.bukkitEntity }
                    ?: return@handler

                val healEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.HEAL }

                if (healEffect != null)
                {
                    val intensity = it.getIntensity(affectedEntity)
                    val amountHealed = (intensity * (4 shl healEffect.amplifier) + 0.5)
                    bot.healthManager.addHealth(amountHealed)
                }

                val harmEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.HARM }

                if (harmEffect != null)
                {
                    val intensity = it.getIntensity(affectedEntity)
                    val amountHarmed = (intensity * (4 shl harmEffect.amplifier) + 0.5)
                    bot.healthManager.removeHealth(amountHarmed)
                    bot.damageManager.playSound()
                    bot.damageManager.damageEffect()
                }

                val poisonEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.POISON }

                if (poisonEffect != null)
                {
                    bot.healthManager.addPoisonTicks(poisonEffect.amplifier)
                }

                val slownessEffect = it.potion.effects
                    .firstOrNull { effect -> effect.type == PotionEffectType.SLOW }

                if (slownessEffect != null)
                {
                    bot.healthManager.addSlownessTicks(slownessEffect.amplifier)
                }

                val remainingEffects = it.potion.effects
                    .filter { effect ->
                        effect.type != PotionEffectType.HARM &&
                            effect.type != PotionEffectType.HEAL
                    }

                if (remainingEffects.isNotEmpty())
                {
                    bot.effectsManager.addEffects(remainingEffects)
                }
            }
            .bindWith(bot.terminable)

        val inventoryManager = bot.inventoryManager
        inventoryManager.applyItemList(game.kit.contents.toList())

        game.kit.armorContents
            .forEachIndexed { index, itemStack ->
                if (itemStack == null)
                {
                    return@forEachIndexed
                }

                inventoryManager.updateArmorContents(itemStack.clone(), index + 1)
            }

        Schedulers
            .sync()
            .runLater({
                bot.effectsManager.addEffects(
                    game.kit
                        .potionEffects
                        .entries
                        .stream()
                        .map { pair: Map.Entry<String?, Int?> ->
                            PotionEffect(
                                PotionEffectType.getByName(pair.key), Int.MAX_VALUE, pair.value!!
                            )
                        }
                        .toList()
                )
            }, 1L)
            .bindWith(bot.terminable)
        return bot
    }
}
