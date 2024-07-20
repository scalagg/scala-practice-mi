package land.esta.plugin.ssb.support.estapractice;

import land.esta.plugin.ssb.bots.setting.SettingManager;
import land.esta.plugin.ssb.utils.FileManager;
import land.esta.plugin.ssb.utils.MathUtils;
import land.esta.plugin.ssb.utils.config.CustomConfig;

import java.util.*;

public class EloBotCreator {

    private static CustomConfig eloSettings = null;

    public static SettingManager createBot(int elo) {
        if (eloSettings == null) {
            eloSettings = new CustomConfig("eloSettings");
        }

        List<String> players = new ArrayList<>();
        players.add("Crafticat");

        // Read players from config
        players = readConfig("players",players);

        SettingManager bot = new SettingManager();
        int index = (int) MathUtils.getRandom(0,players.size() - 1);
        String name = players.get(index);

        bot.setName(name);
        bot.setSkin(players.get(index));

        //Every (30 elo)
        int eloMultiplier = (elo - 1000) / 30;

        // CPS
        int minCps = readConfig("settings.minCps",5);
        int maxCps = readConfig("settings.maxCps",8);
        int minCpsIncrement = readConfig("settings.minCpsEloIncrement",1) * eloMultiplier;
        int maxCpsIncrement = readConfig("settings.minCpsEloIncrement",1) * eloMultiplier;

        minCps += minCpsIncrement;
        maxCps += maxCpsIncrement;

        // Reach
        double reach = readConfig("settings.reach",2.7);
        double reachIncrement = readConfig("settings.reachIncrement",0.0314) * eloMultiplier;
        double reachMax = readConfig("settings.reachMax",3.0);

        reach = Math.max(Math.min(reach + reachIncrement,reachMax),0);

        // Reduce
        boolean reduce = false;
        int reduceElo = readConfig("settings.reduceElo",1200);
        if (elo >= reduceElo) reduce = true;

        // Strafe
        boolean strafe = false;
        int strafeElo = readConfig("settings.strafeElo",1250);
        if (elo >= strafeElo) strafe = true;

        // Rotation settings
        int rotationSpeed = readConfig("settings.rotationSpeed",18);
        int rotationSpeedIncrement = readConfig("settings.rotationSpeedIncrement",1) * eloMultiplier;

        rotationSpeed+= rotationSpeedIncrement;

        // Agro potion
        boolean agroPotion = false;
        int agroPotionElo = readConfig("settings.agroPotionElo",1100);
        if (elo >= agroPotionElo) agroPotion = true;

        // W Tap
        boolean wTap = false;
        int wTapElo = readConfig("settings.wTap",1075);
        if (elo >= wTapElo) wTap = true;

        bot.getClickSetting().getCps().setObject((int) ((minCps + maxCps) / 2));
        bot.getClickSetting().getCps().setRandomizer((int) ((maxCps - minCps) / 2));

        bot.getClickSetting().getReach().setObject(reach);

        bot.getClickSetting().getReach().setObject(reduce);

        bot.getStrafeSettings().getEnabled().setObject(strafe);

        bot.getRotationSetting().getSpeed().setObject(rotationSpeed);

        bot.getPotionSetting().getAgroPotion().setObject(agroPotion);

        bot.getWTapSetting().getEnabled().setObject(wTap);

        return bot;
    }

    public static  <V> V readConfig(String path, V defaultValue) {
        return FileManager.getOrDefault(eloSettings,path,defaultValue);
    }
}
