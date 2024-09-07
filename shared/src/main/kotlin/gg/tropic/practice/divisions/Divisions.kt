package gg.tropic.practice.divisions

import net.evilblock.cubed.util.Color

/**
 * @author TastyCake
 * @date 8/28/2024
 */
enum class Divisions(
    var id: String,
    var title: String,
    var requiredWins: Int
)
{
    NONE("None", Color.translate(""), 0),
    BRONZE_I("Bronze I", Color.translate("&cBronze I "), 5),
    BRONZE_II("Bronze II", Color.translate("&cBronze II "), 50),
    BRONZE_III("Bronze III", Color.translate("&cBronze III "), 75),
    BRONZE_IV("Bronze IV", Color.translate("&cBronze IV "), 100),
    BRONZE_V("Bronze V", Color.translate("&cBronze V "), 125),
    SILVER_I("Silver I", Color.translate("&fSilver I "), 150),
    SILVER_II("Silver II", Color.translate("&fSilver II "), 175),
    SILVER_III("Silver III", Color.translate("&fSilver III "), 200),
    SILVER_IV("Silver IV", Color.translate("&fSilver IV "), 225),
    SILVER_V("Silver V", Color.translate("&fSilver V "), 250),
    GOLD_I("Gold I", Color.translate("&6Gold I "), 275),
    GOLD_II("Gold II", Color.translate("&6Gold II "), 300),
    GOLD_III("Gold III", Color.translate("&6Gold III "), 325),
    GOLD_IV("Gold IV", Color.translate("&6Gold IV "), 350),
    GOLD_V("Gold V", Color.translate("&6Gold V "), 375),
    DIAMOND_I("Diamond I", Color.translate("&bDiamond I "), 400),
    DIAMOND_II("Diamond II", Color.translate("&bDiamond II "), 425),
    DIAMOND_III("Diamond III", Color.translate("&bDiamond III "), 450),
    DIAMOND_IV("Diamond IV", Color.translate("&bDiamond IV "), 475),
    DIAMOND_V("Diamond V", Color.translate("&bDiamond V "), 500),
    MASTER_I("Master I", Color.translate("&3Master I "), 525),
    MASTER_II("Master II", Color.translate("&3Master II "), 550),
    MASTER_III("Master III", Color.translate("&3Master III "), 575),
    MASTER_IV("Master IV", Color.translate("&3Master IV "), 600),
    MASTER_V("Master V", Color.translate("&3Master V "), 625),
    ELYSIAN_I("Elysian I", Color.translate("&2Elysian I "), 650),
    ELYSIAN_II("Elysian II", Color.translate("&2Elysian II "), 675),
    ELYSIAN_III("Elysian III", Color.translate("&2Elysian III "), 700),
    ELYSIAN_IV("Elysian IV", Color.translate("&2Elysian IV "), 725),
    ELYSIAN_V("Elysian V", Color.translate("&2Elysian V "), 750),
    ASTRAL_I("Astral I", Color.translate("&1Astral I "), 800),
    ASTRAL_II("Astral II", Color.translate("&1Astral II "), 850),
    ASTRAL_III("Astral III", Color.translate("&1Astral III "), 900),
    ASTRAL_IV("Astral IV", Color.translate("&1Astral IV "), 950),
    ASTRAL_V("Astral V", Color.translate("&1Astral V "), 1000),
    GLORIOUS_I("Glorious I", Color.translate("&f&k|&r &f&lGlorious I &f&k|&r "), 1050),
    GLORIOUS_II("Glorious II", Color.translate("&f&k|&r &f&lGlorious II &f&k|&r "), 1100),
    GLORIOUS_III("Glorious III", Color.translate("&f&k|&r &f&lGlorious III &f&k|&r "), 1150),
    GLORIOUS_IV("Glorious IV", Color.translate("&f&k|&r &f&lGlorious IV &f&k|&r "), 1200),
    GLORIOUS_V("Glorious V", Color.translate("&f&k|&r &f&lGlorious V &f&k|&r "), 1250),
    GODLIKE_I("Godlike I", Color.translate("&4&k|&r &c&lGodlike I &4&k|&r "), 1500),
    GODLIKE_II("Godlike II", Color.translate("&4&k|&r &c&lGodlike II &4&k|&r "), 1750),
    GODLIKE_III("Godlike III", Color.translate("&4&k|&r &c&lGodlike III &4&k|&r "), 2000),
    GODLIKE_IV("Godlike IV", Color.translate("&4&k|&r &c&lGodlike IV &4&k|&r "), 2250),
    GODLIKE_V("Godlike V", Color.translate("&4&k|&r &c&lGodlike V &4&k|&r "), 2500),
    DEITY_I("Deity I", Color.translate("&1&l&k|&r &9&lDeity I &1&l&k|&r "), 3000),
    DEITY_II("Deity II", Color.translate("&1&l&k|&r &9&lDeity II &1&l&k|&r "), 3500),
    DEITY_III("Deity III", Color.translate("&1&l&k|&r &9&lDeity III &1&l&k|&r "), 4000),
    DEITY_IV("Deity IV", Color.translate("&1&l&k|&r &9&lDeity IV &1&l&k|&r "), 4500),
    DEITY_V("Deity V", Color.translate("&1&l&k|&r &9&lDeity V &1&l&k|&r "), 5000),
    CELESTIAL_I("Celestial I", Color.translate("&5&l&k|&r &d&lCelestial I &5&l&k|&r "), 6000),
    CELESTIAL_II("Celestial II", Color.translate("&5&l&k|&r &d&lCelestial II &5&l&k|&r "), 7000),
    CELESTIAL_III("Celestial III", Color.translate("&5&l&k|&r &d&lCelestial III &5&l&k|&r "), 8000),
    CELESTIAL_IV("Celestial IV", Color.translate("&5&l&k|&r &d&lCelestial IV &5&l&k|&r "), 9000),
    CELESTIAL_V("Celestial V", Color.translate("&5&l&k|&r &d&lCelestial V &5&l&k|&r "), 10000),
    DIVINE_I("Divine I", Color.translate("&4&l&k|&r &d&lDivine I &4&l&k|&r "), 12000),
    DIVINE_II("Divine II", Color.translate("&4&l&k|&r &d&lDivine II &4&l&k|&r "), 14000),
    DIVINE_III("Divine III", Color.translate("&4&l&k|&r &d&lDivine III &4&l&k|&r "), 16000),
    DIVINE_IV("Divine IV", Color.translate("&4&l&k|&r &d&lDivine IV &4&l&k|&r "), 18000),
    DIVINE_V("Divine V", Color.translate("&4&l&k|&r &d&lDivine V &4&l&k|&r "), 20000),
    ANGELIC_I("Angelic I", Color.translate("&f&l&k||&r &f&lAngelic I &f&l&k||&r "), 25000),
    ANGELIC_II("Angelic II", Color.translate("&f&l&k||&r &f&lAngelic II &f&l&k||&r "), 30000),
    ANGELIC_III("Angelic III", Color.translate("&f&l&k||&r &f&lAngelic III &f&l&k||&r "), 35000),
    ANGELIC_IV("Angelic IV", Color.translate("&f&l&k||&r &f&lAngelic IV &f&l&k||&r "), 40000),
    ANGELIC_V("Angelic V", Color.translate("&f&l&k||&r &f&lAngelic V &f&l&k||&r "), 50000),
    ASCENDED_I("Ascended I", Color.translate("&5&l&k|&d&l&k|&r &d&lAscended I &d&l&k|&5&l&k|&r "), 60000),
    ASCENDED_II("Ascended II", Color.translate("&5&l&k|&d&l&k|&r &d&lAscended II &d&l&k|&5&l&k|&r "), 70000),
    ASCENDED_III("Ascended III", Color.translate("&5&l&k|&d&l&k|&r &d&lAscended III &d&l&k|&5&l&k|&r "), 80000),
    ASCENDED_IV("Ascended IV", Color.translate("&5&l&k|&d&l&k|&r &d&lAscended IV &d&l&k|&5&l&k|&r "), 90000),
    ASCENDED_V("Ascended V", Color.translate("&5&l&k|&d&l&k|&r &d&lAscended V &d&l&k|&5&l&k|&r "), 100000),
    DESCENDED_I(
        "Descended I",
        Color.translate("&c&l&k|&4&l&k|&c&l&k|&r &c&lDescended I &c&l&k|&4&l&k|&c&l&k|&r "),
        150000
    ),
    DESCENDED_II(
        "Descended II",
        Color.translate("&c&l&k|&4&l&k|&c&l&k|&r &c&lDescended II &c&l&k|&4&l&k|&c&l&k|&r "),
        200000
    ),
    DESCENDED_III(
        "Descended III",
        Color.translate("&c&l&k|&4&l&k|&c&l&k|&r &c&lDescended III &c&l&k|&4&l&k|&c&l&k|&r "),
        250000
    ),
    DESCENDED_IV(
        "Descended IV",
        Color.translate("&c&l&k|&4&l&k|&c&l&k|&r &c&lDescended IV &c&l&k|&4&l&k|&c&l&k|&r "),
        300000
    ),
    DESCENDED_V(
        "Descended V",
        Color.translate("&c&l&k|&4&l&k|&c&l&k|&r &c&lDescended V &c&l&k|&4&l&k|&c&l&k|&r "),
        350000
    ),
    PSYCHOPATH_I(
        "Psychopath I",
        Color.translate("&f&l&k|&c&lP&f&l&k|&c&ls&f&l&k|&c&ly&f&l&k|&c&lc&f&l&k|&c&lh&f&l&k|&c&lo&f&l&k|&c&lp&f&l&k|&c&la&f&l&k|&c&lt&f&l&k|&c&lh&f&l&k|&f &l&k|&c&lI&l&k|&r "),
        500000
    ),
    PSYCHOPATH_II(
        "Psychopath II",
        Color.translate("&f&l&k|&c&lP&f&l&k|&c&ls&f&l&k|&c&ly&f&l&k|&c&lc&f&l&k|&c&lh&f&l&k|&c&lo&f&l&k|&c&lp&f&l&k|&c&la&f&l&k|&c&lt&f&l&k|&c&lh&f&l&k|&f &l&k|&c&lII&l&k|&r "),
        600000
    ),
    PSYCHOPATH_III(
        "Psychopath III",
        Color.translate("&f&l&k|&c&lP&f&l&k|&c&ls&f&l&k|&c&ly&f&l&k|&c&lc&f&l&k|&c&lh&f&l&k|&c&lo&f&l&k|&c&lp&f&l&k|&c&la&f&l&k|&c&lt&f&l&k|&c&lh&f&l&k|&f &l&k|&c&lIII&l&k|&r "),
        700000
    ),
    PSYCHOPATH_IV(
        "Psychopath IV",
        Color.translate("&f&l&k|&c&lP&f&l&k|&c&ls&f&l&k|&c&ly&f&l&k|&c&lc&f&l&k|&c&lh&f&l&k|&c&lo&f&l&k|&c&lp&f&l&k|&c&la&f&l&k|&c&lt&f&l&k|&c&lh&f&l&k|&f &l&k|&c&lIV&l&k|&r "),
        800000
    ),
    PSYCHOPATH_V(
        "Psychopath V",
        Color.translate("&f&l&k|&c&lP&f&l&k|&c&ls&f&l&k|&c&ly&f&l&k|&c&lc&f&l&k|&c&lh&f&l&k|&c&lo&f&l&k|&c&lp&f&l&k|&c&la&f&l&k|&c&lt&f&l&k|&c&lh&f&l&k|&f &l&k|&c&lV&l&k|&r "),
        900000
    ),
    INDEFINABLE(
        "Indefinable",
        Color.translate("&6&l&k|&e&l&k|&6&l&k|&r &e&lIndefinable &6&l&k|&e&l&k|&6&l&k|&r "),
        1000000
    ),
    PARANORMAL("Paranormal", Color.translate("&7&l&k|&8&l&k|&7&l&k|&r &0&lParanormal &7&l&k|&8&l&k|&7&l&k|&r "), 5000000),
    LUNATIC("Lunatic", Color.translate("&0&l&k|&r &0&l&o&nLunatic&r &0&l&k|&r "), 10000000);


    fun getID(): String
    {
        return name.replace("\\s".toRegex(), "")
    }

    companion object {
        fun getById(id: String) : Divisions? {
            return entries.find { it.id == id }
        }
    }
}
