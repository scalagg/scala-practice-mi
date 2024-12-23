package gg.scala.practice.games.manager

import com.github.benmanes.caffeine.cache.Caffeine
import gg.scala.practice.application.api.DPSRedisService
import gg.scala.practice.application.api.DPSRedisShared
import gg.scala.practice.games.GameStatus
import gg.scala.practice.games.GameStatusIndexes
import gg.scala.practice.namespace
import gg.scala.practice.suffixWhenDev
import net.evilblock.cubed.serializers.Serializers
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 9/25/2023
 */
object GameManager
{
    private val redis = DPSRedisService("gamemanager")
        .apply(DPSRedisService::start)

    private val gameListingCache = Caffeine.newBuilder()
        .expireAfterWrite(2L, TimeUnit.SECONDS)
        .removalListener<String, String> { _, _, _ -> syncStatusIndexes() }
        .build<String, String>()

    private val dpsCache = DPSRedisShared.keyValueCache

    fun allGames() = allGameStatuses()
        .thenApply { it ->
            it.values
                .flatMap(GameStatus::games)
        }

    fun allGameStatuses() = CompletableFuture
        .supplyAsync {
            gameListingCache
                .asMap().mapValues { (_, value) ->
                    dpsCache
                        .sync()
                        .get(value)
                        .let {
                            Serializers.gson.fromJson(
                                it, GameStatus::class.java
                            )
                        }
                }
        }

    private fun syncStatusIndexes()
    {
        ForkJoinPool.commonPool().submit {
            dpsCache.sync().set(
                "${namespace().suffixWhenDev()}:gamemanager:status-indexes",
                Serializers.gson.toJson(
                    GameStatusIndexes(gameListingCache.asMap())
                )
            )
        }
    }

    fun load()
    {
        redis.configure {
            listen("status") {
                val server = retrieve<String>("server")
                val status = retrieve<GameStatus>("status")

                val key = "${namespace().suffixWhenDev()}:gamemanager:status:$server"

                dpsCache.sync().psetex(
                    key, 1000 * 2,
                    Serializers.gson.toJson(status)
                )

                gameListingCache.put(server, key)
                syncStatusIndexes()
            }
        }
    }
}
