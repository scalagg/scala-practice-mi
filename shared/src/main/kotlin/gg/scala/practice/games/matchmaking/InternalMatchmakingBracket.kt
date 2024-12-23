package gg.scala.practice.games.matchmaking

/**
 * @author GrowlyX
 * @since 8/24/2024
 */
enum class InternalMatchmakingBracket(val range: IntRange)
{
    Pre1000(-Int.MAX_VALUE..1049),
    One(1050..1149),
    Two(1150..1249),
    Three(1250..1349),
    Four(1350..1449),
    Five(1450..1549),
    Six(1550..1649),
    Seven(1650..1749),
    Eight(1750..1849),
    Nine(1850..1949),
    Ten(1950..2049),
    Eleven(2050..2149),
    Twelve(2150..2249),
    Thirteen(2250..2349),
    Fourteen(2350..2449),
    Fifteen(2450..2500),
    Post2500(2501..Int.MAX_VALUE);

    companion object
    {
        @JvmStatic
        fun of(rating: Int) = entries.first { rating in it.range }
    }
}
