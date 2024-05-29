package gg.tropic.practice.map.rating

import com.mongodb.client.model.Filters
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.tropic.practice.map.Map
import gg.tropic.practice.map.MapService
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.FancyMessage
import net.evilblock.cubed.util.bukkit.Tasks
import net.jodah.expiringmap.ExpiringMap
import net.md_5.bungee.api.chat.ClickEvent
import org.bson.Document
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Class created on 1/12/2024

 * @author 98ping
 * @project tropic-practice
 * @website https://solo.to/redis
 */
@Service
object MapRatingService
{
    val averageRatings = mutableMapOf<String, Double>()

    @Configure
    fun configure()
    {
        Tasks.asyncTimer(0L, 30 * 20L) {
            MapService.cached().maps.values.forEach {
                averageRatings[it.name] = loadAverageRating(it)
            }
        }
    }

    val pendingRatings = ExpiringMap.builder()
        .expiration(1L, TimeUnit.MINUTES)
        .build<UUID, String>()

    fun sendMapRatingRequest(player: Player, map: Map)
    {
        findExistingMapRating(player.uniqueId, map)
            .thenAccept { rating ->
                if (rating == null)
                {
                    val message = FancyMessage()
                        .withMessage(
                            "${CC.AQUA}Rate the ${CC.BOLD}${map.name}${CC.AQUA} map:"
                        )

                    for (entry in StarRating.entries)
                    {
                        with(message) {
                            withMessage("${entry.color}[${entry.ordinal + 1}] ")
                            andCommandOf(ClickEvent.Action.RUN_COMMAND, "rate ${map.name} ${entry.name}")
                        }
                    }

                    pendingRatings[player.uniqueId] = map.name
                    message.sendToPlayer(player)
                }
            }
    }

    fun findExistingMapRating(player: UUID, map: Map) = DataStoreObjectControllerCache
        .findNotNull<MapRating>()
        .mongo()
        .loadWithFilter(
            Filters.and(
                Filters.eq("mapID", map.name),
                Filters.eq("rater", player)
            )
        )

    fun loadAverageRating(map: Map) = DataStoreObjectControllerCache
        .findNotNull<MapRating>()
        .mongo()
        .aggregate(
            listOf(
                Document(
                    "\$match",
                    Document(
                        "mapID", map.name
                    )
                ),
                Document(
                    "\$group",
                    Document(
                        mapOf(
                            "_id" to "\$_id",
                            "average" to Document(
                                "\$avg", "\$rating"
                            )
                        )
                    )
                )
            )
        )
        .first()
        ?.getDouble("average")
        ?: 0.0

    fun create(rating: MapRating) = DataStoreObjectControllerCache
        .findNotNull<MapRating>()
        .save(rating)
}
