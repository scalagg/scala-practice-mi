package land.esta.plugin.ssb.support.strikepractice;

import gg.tropic.practice.games.GameImpl;
import gg.tropic.practice.kit.feature.FeatureFlag;
import gg.tropic.practice.profile.PracticeProfileService;
import lombok.Getter;
import land.esta.plugin.ssb.botevents.events.BotAttackLandEvent;
import land.esta.plugin.ssb.botevents.events.BotDamageEvent;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.bots.mechanics.InventoryManager;
import land.esta.plugin.ssb.bots.setting.Setting;
import land.esta.plugin.ssb.bots.setting.SettingManager;
import land.esta.plugin.ssb.bots.setting.SettingObject;
import land.esta.plugin.ssb.data.DataPlayer;
import land.esta.plugin.ssb.SmartBotsBackend;
import land.esta.plugin.ssb.utils.Color;
import land.esta.plugin.ssb.utils.RandomUtil;
import land.esta.plugin.ssb.utils.gui.imple.BotGui;
import land.esta.plugin.ssb.utils.gui.imple.SettingGui;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Getter
public class SPManager implements Listener {

    private final EstaPracticeSupport support;

    public SPManager(EstaPracticeSupport support) {
        this.support = support;
        Bukkit.getServer().getPluginManager().registerEvents(this, SmartBotsBackend.getInstance());
    }

    public Bot spawn(
            Location loc,
            GameImpl game,
            boolean combo,
            Player player,
            boolean noDamage
    ) throws IOException {
        DataPlayer data = SmartBotsBackend.getInstance().getDataManager().getPlayer(player);
        try {
            SettingManager settings = getSettings(data, "easy", game.getKit().getId());

            if (settings == null) {
                game.complete(null, "No bot configuration was found!");
                return null;
            }

            if (settings.isGameRandomizer()) {
                for (Setting setting : settings.getSettings()) {
                    for (SettingObject object : setting.getSettings()) {
                        if (object.getMin() <= 0 || object.getMax() <= 0) continue;

                        if (object.getObject() instanceof Boolean) {
                            object.setObject(RandomUtil.randInt(1, 3) == 1 && RandomUtil.randInt(1, 2) != -1);
                        } else if (object.getObject() instanceof Double) {
                            if ((Double) object.getObject() % 1 == 0)
                                if (RandomUtil.randInt((int) object.getMin(), (int) object.getMax()) != -1)
                                    object.setObject(RandomUtil.randInt((int) object.getMin(), (int) object.getMax()));
                                else if (RandomUtil.randDouble(object.getMin(), object.getMax()) != -1)
                                    object.setObject(RandomUtil.randDouble(object.getMin(), object.getMax()));
                        } else {
                            if (RandomUtil.randInt((int) object.getMin(), (int) object.getMax()) != -1)
                                object.setObject(RandomUtil.randInt((int) object.getMin(), (int) object.getMax()));
                        }
                    }
                }
            }

            String name = settings.getBotName().replace("%player%", player.getName());
            if (name.length() > 16) {
                name = name.substring(0, 16);
            }

            Bot bot = new Bot(loc, Color.code(name), settings.getSkin().replace("%player%", player.getName()), player, settings, game.getKit()); // ADDED: Applies the bot skin and name
            if (game.flag(FeatureFlag.DeathOnLiquidInteraction)) {
                bot.getMovements().setEdgeDetector(true);
            }

            bot.getDamageManager().setAllowGlobalAttacks(false);
            // bot.lightning();
            bot.getMovements().getCollision().setWaterEnterEvent(bot::silentKill);
            bot.getDamageManager().setNoDamage(noDamage);
            bot.getMovements().setMove(false);

            if (combo) bot.getDamageManager().setCombo(combo);

            //Boxing support
            if (noDamage) {
                data.setBotHitCounter(0);
                data.setHitCounter(0);
                data.setCombo(0);
                data.setBotCombo(0);
                bot.setEventListener((event -> {
                    if (event instanceof BotDamageEvent) {
                        if (((BotDamageEvent) event).getSource() != null && ((BotDamageEvent) event).getSource().getEntity() != null && Bukkit.getServer().getPlayer(((BotDamageEvent) event).getSource().getEntity().getName()).equals(player)) {
                            data.hit();
                            // TODO: support this
//                            fight.getStatistics(player).setHits(data.getHitCounter());

                            if (data.getHitCounter() >= 100) {
                                bot.kill();
                            }
                        } else {
                            BotDamageEvent e = (BotDamageEvent) event;
                            e.setCanceled(true);
                        }
                    }
                    if (event instanceof BotAttackLandEvent) {
                        data.botHit();
                        if (data.getBotHitCounter() >= 100) {
                            player.setHealth(0);
                        }
                    }
                }));
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (bot.isAlive()) bot.getMovements().setMove(true);
                }
            }.runTaskLater(SmartBotsBackend.getInstance(), 6 * 20);

            InventoryManager inventoryManager = bot.getInventoryManager();
            inventoryManager.applyItemList(
                    Stream
                            .of(game.getKit().getContents())
                            .map(ItemStack::clone)
                            .toList()
            );

            if (game.getKit().getArmorContents()[3] != null) {
                // inventoryManager.updateHelmet(helmet.clone());
                inventoryManager.updateArmorContents(game.getKit().getArmorContents()[3].clone(), 4);
            }
            if (game.getKit().getArmorContents()[2] != null) {
                // inventoryManager.updateChestPlate(chestPlate.clone());
                inventoryManager.updateArmorContents(game.getKit().getArmorContents()[2].clone(), 3);
            }
            if (game.getKit().getArmorContents()[1] != null) {
                // inventoryManager.updateLeggings(leggings.clone());
                inventoryManager.updateArmorContents(game.getKit().getArmorContents()[1].clone(), 2);
            }
            if (game.getKit().getArmorContents()[0] != null) {
                // inventoryManager.updateBoots(boots.clone());
                inventoryManager.updateArmorContents(game.getKit().getArmorContents()[0].clone(), 1);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    bot.getEffectsManager().addEffects(
                            game.getKit()
                                    .getPotionEffects()
                                    .entrySet()
                                    .stream()
                                    .map(pair -> new PotionEffect(
                                            PotionEffectType.getByName(pair.getKey()), Integer.MAX_VALUE, pair.getValue()
                                    ))
                                    .toList()
                    );
                }
            }.runTaskLater(SmartBotsBackend.getInstance(), 1);
            //TODO combo mode support.
            return bot;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    public void kill(int id) {
        Bot bot = SmartBotsBackend.getInstance().getBotManager().getNpcFromId(id);
        if (bot != null && bot.isAlive()) bot.silentKill();
    }

    public SettingManager getSettings(DataPlayer data, String level, String kitName, int... tier) {
        //Unranked / ranked queue
        if (level.equals("UNRANKED"))
            return SmartBotsBackend.getInstance().getPresetManager().getUnranked().getPreset().getManager();
        if (level.equals("RANKED")) {
            int elo = getElo(data.getPlayer(), kitName);

            SettingGui closedPreset = null;
            int best = 1000;

            for (SettingGui preset : SmartBotsBackend.getInstance().getPresetManager().getRanked().getPresets()) {
                int diff = Math.abs(preset.getManager().getElo() - elo);
                if (diff < best) {
                    closedPreset = preset;
                    best = diff;
                }
            }

            if (closedPreset != null)
                return closedPreset.getManager();
        }
        if (level.equals("TOURNAMENT")) {
            for (SettingGui preset : SmartBotsBackend.getInstance().getPresetManager().getTournament().getPresets()) {
                if (preset.getManager().getTier() == tier[0]) return preset.getManager();
            }
        }

        //Custom
        BotGui gui = data.getGui();
        if (level.equals("HACKER")) {
            if (!gui.isLoaded()) {
                data.getPlayer().sendMessage(Color.code("&aFetching settings, please a moment..."));
                return null;
            }
            return data.getGui().getManager();
        }
        //Easy,Normal,Hard,Expert,Nightmare
        return SmartBotsBackend.getInstance().getDifficultiesSettings().getData(level);
    }

    public int getElo(Player player, String kitName) {
        var rankedStats = PracticeProfileService.INSTANCE
                .find(player)
                .getRankedStatistics()
                .get(kitName);

        if (rankedStats == null) {
            return 1000;
        }

        int playerKitElo = rankedStats.getElo();

        int elo1 = playerKitElo - 100;
        int elo2 = playerKitElo + 100;

        return ThreadLocalRandom.current().nextInt(elo2 - elo1) + elo1;
    }
}
