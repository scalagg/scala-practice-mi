package land.esta.plugin.ssb.features.imple;

import gg.tropic.practice.games.GameService;
import land.esta.plugin.ssb.EstaRobotsPlugin;
import land.esta.plugin.ssb.bot.setting.setting.RootAccessor;
import land.esta.plugin.ssb.bots.Bot;
import land.esta.plugin.ssb.features.Feature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lucko.helper.Schedulers;
import me.lucko.helper.random.RandomSelector;
import me.lucko.helper.random.Weighted;
import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BuildingFeature extends Feature {

    private long lastPlaced = System.currentTimeMillis();

    @Override
    public void tickEvent(Bot bot) {
        boolean enabled = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getEnabled());
        if (!enabled) {
            return;
        }

        if (bot.isCurrentlyBuilding()) {
            return;
        }

        double minDistanceFromPlayer = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getMinDistanceFromPlayer());
        int placeCooldown = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getPlaceCooldown());
        int buildingTicks = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildingTicks());
        boolean buildWhenCombo = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenCombo());
        int buildWhenComboHits = bot.getProfile().value(RootAccessor.INSTANCE.getBuildingSetting().getBuildWhenComboHits());

        var distanceToPlayer = bot.getMovements().getLocation().distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation());
        var currentComboHots = EstaRobotsPlugin.getInstance().getFeatureManager().getStrafeFeature().getCombo().getOrDefault(bot.getUniqueId(), 0);
        var currentTime = System.currentTimeMillis();

        bot.getPlayer().sendMessage(
                String.valueOf(
                        BuildingUtilitiesKt.isMovingTowards(
                                bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
                                bot.getMovements().getLocation()
                        )
                )
        );

        boolean shouldBuild = (/*(buildWhenCombo && currentComboHots >= buildWhenComboHits) || */distanceToPlayer <= minDistanceFromPlayer)
                && currentTime - placeCooldown > lastPlaced
                && BuildingUtilitiesKt.isMovingTowards(
                    bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
                    bot.getMovements().getLocation()
                )
                && BuildingUtilitiesKt.isInLineOfSight(
                    bot.getMovements().getLocation().getDirection(),
                    bot.getMovements().getLocation(),
                    bot.getMovements().getTargetLocate().getTargetEntity().getLocation(),
                    40.0
                )
                && Math.floor(bot.getMovements().getLocation().getY()) == Math.floor(bot.getMovements().getTargetLocate().getTargetEntity().getLocation().getY());

        if (!shouldBuild) {
            return;
        }

        lastPlaced = System.currentTimeMillis();

        List<ItemStack> blocks = bot.getInventoryManager().hasItemUsingName(Material.WOOD); // TODO: Change to wool
        if (blocks.isEmpty()) {
            return;
        }

        PlacementConfiguration placementConfiguration = new PlacementConfiguration().random().updateLocations(bot);
        AtomicInteger previousSlot = new AtomicInteger(bot.getInventoryManager().getMainHand());

        bot.getInventoryManager().setMainHand(
                bot.getInventoryManager().getItemSlot(blocks.get(0))
        );

        Schedulers
                .sync()
                .runRepeating((task) -> {
                    if (!placementConfiguration.hasNext()) {
                        bot.setCurrentlyBuilding(false);
                        bot.getInventoryManager().setMainHand(previousSlot.get());
                        task.closeAndReportException();
                        return;
                    }

                    Location location = placementConfiguration.next();
                    bot.setCurrentlyBuilding(true);

                    if (!location.getBlock().getType().equals(Material.AIR)) return;
                    if (BuildingUtilitiesKt.isLocationInsidePlayerHitbox(location, bot.getPlayer())) return;
                    if (location.distance(bot.getMovements().getLocation()) < 0.2) return;
                    if (location.distance(bot.getMovements().getTargetLocate().getTargetEntity().getLocation()) < 0.2)
                        return;

                    var blockType = bot.getInventoryManager().getItemInMainHand().getType();
                    location.getBlock().setType(blockType);
                    location.getBlock().setMetadata(
                            "placed",
                            new FixedMetadataValue(GameService.INSTANCE.getPlugin(), true)
                    );

                    var yawPitch = getYawPitch(bot.getMovements().getLocation(), location);
                    bot.getMovements().getRotations().setRotationLockUntil(System.currentTimeMillis() + 500L);
                    bot.getMovements().rotate(yawPitch[0], yawPitch[1]);

                    var block = Block.getById(location.getBlock().getTypeId());

                    bot.getGame().getPlacedBlocks().add(location.getBlock().getLocation().toVector());
                    bot.getMovements().getTargetLocate().getTargetEntity()
                            .playSound(
                                    location, block.stepSound.getPlaceSound(), 1, 1
                            );


                }, 0, buildingTicks)
                .bindWith(bot.getTerminable());
    }

    /**
     * Derived from https://github.com/CitizensDev/Citizens/blob/master/src/core/net/citizensnpcs/resources/npclib/NPCManager.java#L42
     */
    public static float[] getYawPitch(Location from, Location to) {
        double xDiff = to.getX() - from.getX();
        double yDiff = to.getY() - from.getY();
        double zDiff = to.getZ() - from.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
        double yaw = (Math.acos(xDiff / DistanceXZ) * 180 / Math.PI);
        double pitch = (Math.acos(yDiff / DistanceY) * 180 / Math.PI) - 90;
        if (zDiff < 0.0) {
            yaw = yaw + (Math.abs(180 - yaw) * 2);
        }
        return new float[]{(float) yaw - 90, (float) pitch};
    }

    @RequiredArgsConstructor
    public static class PlacementConfiguration {
        private ExitComboBuildAction buildAction;
        private List<Location> locations = new ArrayList<>();
        private int current = 0;

        public PlacementConfiguration random() {
            this.buildAction = ExitComboBuildAction.random();
            return this;
        }

        public Location next() {
            current++;
            return locations.get(current);
        }

        public boolean hasNext() {
            return current + 1 <= locations.size() - 1;
        }

        public PlacementConfiguration updateLocations(Bot bot) {
            List<Location> result = new ArrayList<>();
            var selfLocation = bot.getMovements().getLocation().clone();
            var selfDirection = selfLocation.getDirection();
            var targetLocation = bot.getMovements().getTargetLocate().getTargetEntity().getLocation().clone();

            var startLocation = buildAction.blockPlayer ? selfLocation.add(selfDirection.clone().multiply(1.5))
                    : targetLocation.add(targetLocation.getDirection().clone());

            for (var i = 10; i > 0; i--) {
                var beneath = startLocation.clone().subtract(0, 1, 0);
                if (beneath.getBlock().getType().isSolid()) {
                    startLocation = beneath;
                    break;
                }
            }

            result.add(startLocation);
            Location botLocation = bot.getMovements().getLocation();

            for (String placementPosition : buildAction.getPlacementDirections().keySet()) {
                int amountToPlace = buildAction.getPlacementDirections().get(placementPosition);
                for (int level = 1; level <= amountToPlace; level++) {
                    result.add(
                            getRelativeLocation(
                                    startLocation.clone(),
                                    placementPosition, level,
                                    getDirectionFromYaw(botLocation.getYaw())
                            )
                    );
                }
            }

            locations = result;
            return this;
        }

        private Location getRelativeLocation(Location base, String direction, int distance, Direction facing) {
            return switch (direction.toUpperCase()) {
                case "UP" -> base.add(0, distance, 0);
                case "DOWN" -> base.add(0, -distance, 0);
                case "LEFT" -> getLeft(base, distance, facing);
                case "RIGHT" -> getRight(base, distance, facing);
                case "FORWARDS" -> getForwards(base, distance, facing);
                case "BACKWARDS" -> getBackwards(base, distance, facing);
                default -> throw new IllegalArgumentException("Invalid direction: " + direction);
            };
        }

        private Location getLeft(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(-distance, 0, 0);
                case SOUTH -> base.add(distance, 0, 0);
                case EAST -> base.add(0, 0, -distance);
                default -> base.add(0, 0, distance);
            };
        }

        private Location getRight(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(distance, 0, 0);
                case SOUTH -> base.add(-distance, 0, 0);
                case EAST -> base.add(0, 0, distance);
                default -> base.add(0, 0, -distance);
            };
        }

        private Location getForwards(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(0, 0, -distance);
                case SOUTH -> base.add(0, 0, distance);
                case EAST -> base.add(distance, 0, 0);
                default -> base.add(-distance, 0, 0);
            };
        }

        private Location getBackwards(Location base, int distance, Direction facing) {
            return switch (facing) {
                case NORTH -> base.add(0, 0, distance);
                case SOUTH -> base.add(0, 0, -distance);
                case EAST -> base.add(-distance, 0, 0);
                default -> base.add(distance, 0, 0);
            };
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
        public enum ExitComboBuildAction implements Weighted {
            PLAYER_BLOCK(new HashMap<>() {{
                put("UP", 1);
            }}, 50, true),
            /*LEFT(new HashMap<>() {{
                put("LEFT", 1);
            }}, 15, true),
            RIGHT(new HashMap<>() {{
                put("RIGHT", 1);
            }}, 15, true),
            STAIR(new HashMap<>() {{
                put("UP", 1);
                put("BACKWARDS", 2);
            }}, 20, false)*/;

            private static final List<ExitComboBuildAction> ENTRIES = Arrays.asList(values());
            private static final RandomSelector<ExitComboBuildAction> WEIGHTED_RANDOM = RandomSelector.weighted(ENTRIES);

            @NotNull
            public static ExitComboBuildAction random() {
                return WEIGHTED_RANDOM.pick();
            }

            private final int chance;
            private final Map<String, Integer> placementDirections;
            private final boolean blockPlayer;

            ExitComboBuildAction(Map<String, Integer> placementDirections, int chance, boolean blockPlayer) {
                this.placementDirections = placementDirections;
                this.chance = chance;
                this.blockPlayer = blockPlayer;
            }

            @Override
            public double getWeight() {
                return chance;
            }
        }
    }
}
