package land.esta.plugin.ssb.features.imple;

import gg.tropic.practice.games.GameImpl;
import gg.tropic.practice.games.GameService;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import lombok.Getter;
import me.lucko.helper.Schedulers;
import net.evilblock.cubed.util.bukkit.Direction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.*;

public class BuildingFeature extends Feature {
    private boolean building;
    private long lastPlaced = System.currentTimeMillis();

    @Override
    public void tickEvent(Bot bot) {
        boolean enabled = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getEnabled());
        double minDistanceFromPlayer = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getMinDistanceFromPlayer());
        int placeCooldown = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getPlaceCooldown());
        int buildingTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingTicks());
        boolean buildWhenCombo = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenCombo());
        int buildWhenComboHits = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenComboHits());

        if (!enabled) return;

        boolean build = (buildWhenCombo && EstaRobotsPlugin.getInstance().getFeatureManager().getStrafeFeature().getCombo().getOrDefault(bot.getUniqueId(),0) >= buildWhenComboHits
                || bot.getMovements().getLocation().distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation()) <= minDistanceFromPlayer)
                && System.currentTimeMillis() - placeCooldown > lastPlaced;

        if (!build) return;

        lastPlaced = System.currentTimeMillis();

        List<ItemStack> wool = bot.getInventoryManager().hasItemUsingName(Material.WOOD); // TODO: Change to wool

        if (wool.isEmpty()) return;

        Type type = new Type()
                .random()
                .updateLocations(bot);

        final Map<UUID, Integer> prevSlot = new HashMap<>();

        prevSlot.put(bot.getUniqueId(), bot.getInventoryManager().getMainHand());

        bot.getInventoryManager().setMainHand(bot.getInventoryManager().getItemSlot(wool.get(0)));

        Schedulers
                .sync()
                .runRepeating((task) -> {
                    if (!type.hasNext()) {
                        building = false;
                        bot.getInventoryManager().setMainHand(prevSlot.get(bot.getUniqueId()));
                        task.stop();
                        return;
                    }

                    Location location = type.next();
                    building = true;

                    if (!location.getBlock().getType().equals(Material.AIR)) return;
                    if (location.distance(bot.getMovements().getLocation()) < 0.2) return;
                    if (location.distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation()) < 0.2) return;

                    location.getBlock().setType(bot.getInventoryManager().getItemInMainHand().getType());
                    location.getBlock().setMetadata("placed", new FixedMetadataValue(GameService.INSTANCE.getPlugin(), true));
                    bot.getGame().getPlacedBlocks().add(location.getBlock().getLocation().toVector());
                    bot.getMovements().getTargetLocate().getTargetEntity().playSound(location, Sound.STEP_WOOL, 1, 1);
                }, 0, buildingTicks)
                .bindWith(bot.getTerminable());
    }

    public class Type {
        private ENUM eType;
        private List<Location> locations = new ArrayList<>();
        private int current = 0;

        public Type() {

        }

        Type(ENUM eType) {
            this.eType = eType;
        }

        public Type random() {
            this.eType = ENUM.random();

            return this;
        }

        public Location next() {
            current++;
            return locations.get(current);
        }

        public boolean hasNext() {
            try {
                Location loc = locations.get(current + 1);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public Type updateLocations(Bot bot) {
            List<Location> result = new ArrayList<>();

            Location startLocation = eType.blockPlayer ? bot.getMovements().getLocation().clone().add(bot.getMovements().getLocation().getDirection().clone().multiply(2))
                    : bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone().add(bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getDirection());

            for (int i = 10; i > 0; i--) {
                if (!startLocation.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                    startLocation = startLocation.subtract(0, 1, 0);
                    break;
                }
            }

            result.add(startLocation);
            Location botLocation = bot.getMovements().getLocation();

            for (String key : eType.getLocations().keySet()) {
                int distance = eType.getLocations().get(key);
                for (int i = 1; i <= distance; i++) {
                    result.add(getRelativeLocation(startLocation.clone(), key, distance, getDirectionFromYaw(botLocation.getYaw())));
                }
            }

            locations = result;

            return this;
        }

        private Location getRelativeLocation(Location base, String direction, int distance, Direction facing) {
            switch (direction.toUpperCase()) {
                case "UP":
                    return base.add(0, distance, 0);
                case "DOWN":
                    return base.add(0, -distance, 0);
                case "LEFT":
                    return getLeft(base, distance, facing);
                case "RIGHT":
                    return getRight(base, distance, facing);
                case "FORWARDS":
                    return getForwards(base, distance, facing);
                case "BACKWARDS":
                    return getBackwards(base, distance, facing);
                default:
                    throw new IllegalArgumentException("Invalid direction: " + direction);
            }
        }

        private Location getLeft(Location base, int distance, Direction facing) {
            switch (facing) {
                case NORTH:
                    return base.add(-distance, 0, 0);
                case SOUTH:
                    return base.add(distance, 0, 0);
                case EAST:
                    return base.add(0, 0, -distance);
                case WEST:
                default:
                    return base.add(0, 0, distance);
            }
        }

        private Location getRight(Location base, int distance, Direction facing) {
            switch (facing) {
                case NORTH:
                    return base.add(distance, 0, 0);
                case SOUTH:
                    return base.add(-distance, 0, 0);
                case EAST:
                    return base.add(0, 0, distance);
                case WEST:
                default:
                    return base.add(0, 0, -distance);
            }
        }

        private Location getForwards(Location base, int distance, Direction facing) {
            switch (facing) {
                case NORTH:
                    return base.add(0, 0, -distance);
                case SOUTH:
                    return base.add(0, 0, distance);
                case EAST:
                    return base.add(distance, 0, 0);
                case WEST:
                default:
                    return base.add(-distance, 0, 0);
            }
        }

        private Location getBackwards(Location base, int distance, Direction facing) {
            switch (facing) {
                case NORTH:
                    return base.add(0, 0, distance);
                case SOUTH:
                    return base.add(0, 0, -distance);
                case EAST:
                    return base.add(-distance, 0, 0);
                case WEST:
                default:
                    return base.add(distance, 0, 0);
            }
        }

        private Direction getDirectionFromYaw(float yaw) {
            if (yaw >= -135 && yaw < -45) {
                return Direction.EAST;
            }

            if (yaw >= -45 && yaw < 45) {
                return Direction.SOUTH;
            }

            if (yaw >= 45 && yaw < 135) {
                return Direction.WEST;
            }

            return Direction.NORTH;
        }

        public enum Direction {
            NORTH, SOUTH, EAST, WEST
        }

        @Getter
        public enum ENUM {
            PLAYER_BLOCK(new HashMap<>() {{
                    put("UP", 1);
                }}, 50, true),
            LEFT(new HashMap<>() {{
                    put("LEFT", 1);
                }}, 15, true),
            RIGHT(new HashMap<>() {{
                    put("RIGHT", 1);
                }}, 15, true),
            STAIR(new HashMap<>() {{
                    put("UP", 1);
                    put("BACKWARDS", 1);
                }}, 20, false);

            private static Random random = new Random();

            private int chance;
            private Map<String, Integer> locations;
            private boolean blockPlayer;

            ENUM(Map<String, Integer> locations, int chance, boolean blockPlayer) {
                this.locations = locations;
                this.chance = chance;
                this.blockPlayer = blockPlayer;
            }

            public static ENUM random() {
                int totalChances = 0;
                for (ENUM e : ENUM.values()) {
                    totalChances += e.getChance();
                }

                int randomValue = random.nextInt(totalChances);

                for (ENUM e : ENUM.values()) {
                    randomValue -= e.getChance();
                    if (randomValue < 0) {
                        return e;
                    }
                }

                return ENUM.values()[0];
            }
        }
    }
}
