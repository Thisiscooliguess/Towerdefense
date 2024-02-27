package tower.commands;

import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.gen.Call;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.ui.Menus;
import mindustry.world.Tile;
import tower.Bundle;
import tower.Players;
import tower.Domain.PlayerData;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SuperPowers {
    private static final float tilesize =   1.0f; // Adjust the value as needed

    public static void execute(Player player) {
        Unit playerUnit = player.unit(); // Get the player's unit
        if (playerUnit != null) {
            float playerX = playerUnit.x; // Get the player's X position
            float playerY = playerUnit.y; // Get the player's Y position
            World world = Vars.world; // Get the World object from the unit
            openUnitSelectionMenu(player, world, playerX, playerY);
        } else {
            player.sendMessage(Bundle.get("player.unit.not-available", player.locale()));
        }
    }
    private static void spawnCorvusUnit(Player player, World world, float playerX, float playerY) {
        spawnUnitWithType(player, world, playerX, playerY, UnitTypes.corvus);
    }
    
    private static void spawnCollarisUnit(Player player, World world, float playerX, float playerY) {
        spawnUnitWithType(player, world, playerX, playerY, UnitTypes.collaris);
    }
    private static void openUnitSelectionMenu(Player player, World world, float playerX, float playerY) {
        String[][] buttons = {
            {"Corvus"},
            {"Collaris"},
            {"Squad"},
            {"Disrupt"},
            {"Omni Tower"} 
        };
        Call.menu(player.con, Menus.registerMenu((player1, option) -> {
            if (option ==  0) {
                spawnCorvusUnit(player, world, playerX, playerY);
            } else if (option ==  1) {
                spawnCollarisUnit(player, world, playerX, playerY);
            } else if (option ==  2) {
                spawnArcOfUnits(player, world, playerX, playerY, UnitTypes.disrupt);
            } else if (option ==  3) { // Add this block
                spawnDisruptUnit(player, world, playerX, playerY);
            }
             else if (option ==  4) { // Add this block
            specialSpawn(player, world, playerX, playerY);
            }
            
        }), "[lime]Choose a ability to use:", "", buttons);
    }
    
    private static void spawnUnitWithType(Player player, World world, float playerX, float playerY, UnitType unitType) {
        PlayerData playerData = Players.getPlayer(player);
        if (playerData.getPoints() >=  40) {
            float angleStep =  360f /  6;
            float radius =  100f; // Calculate the angle step for evenly spaced spawns
            for (int i =  0; i <  6; i++) {
                float angle = i * angleStep; // Calculate the angle for each unit
                double radians = Math.toRadians(angle);
                float x = playerX + radius * (float) Math.cos(radians);
                float y = playerY + radius * (float) Math.sin(radians);
            
                int intX = (int) x;
                int intY = (int) y;
                float worldX = intX * tilesize;
                float worldY = intY * tilesize;
    
                Tile tile = world.tileWorld(worldX, worldY);
                if (tile != null) {
                    Unit unit = unitType.spawn(worldX, worldY);
                    if (unit != null && unit.isValid()) {
                        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                        executor.schedule(() -> {
                            if (unit != null && unit.isValid()) {
                                unit.kill();
                            }
                        },  50, TimeUnit.SECONDS);
    
                        if (unitType == UnitTypes.corvus) {
                            unitType.groundLayer = Layer.flyingUnit;
                            unitType.weapons.get(0).reload =  10f;
                            unitType.weapons.get(0).cooldownTime =  10f;
                        } else if (unitType == UnitTypes.collaris) {
                            unitType.groundLayer = Layer.flyingUnit;
                            unitType.weapons.get(0).reload =  10f;
                            unitType.weapons.get(0).cooldownTime =  10f;
                        }
                    } else {
                        playerData.addPoints(40, player);
                        player.sendMessage(Bundle.get("spawn.unit.failed", player.locale()));
                    }
                } else {
                    player.sendMessage(Bundle.get("spawn.unit.invalid-location", player.locale()));
                }
            }
        } else {
            player.sendMessage(Bundle.get("spawn.arc-of-units.not-enough-points", player.locale()));
        }
    }
    private static void spawnArcOfUnits(Player player, World world, float playerX, float playerY, UnitType unitType) {
        PlayerData playerData = Players.getPlayer(player);
        int unitCost =   40; // Cost per unit
        int totalCost = unitCost; // Total cost remains   40 for all units

        if (playerData.getPoints() >= totalCost) {
            float radius =   140f;
            float arcAngle =   180f;
            float angleStep = arcAngle /   20; // Divide the arc by the number of units
            boolean allUnitsSpawned = true;

            for (int i =   0; i <   20; i++) {
                float angle = i * angleStep - arcAngle /   2; // Calculate the angle for each unit
                double radians = Math.toRadians(angle);
                float x = playerX + radius * (float) Math.cos(radians);
                float y = playerY + radius * (float) Math.sin(radians);

                int intX = (int) x;
                int intY = (int) y;
                float worldX = intX * tilesize;
                float worldY = intY * tilesize;

                Tile tile = world.tileWorld(worldX, worldY);
                if (tile != null) {
                    Unit unit = unitType.spawn(worldX, worldY);
                    if (unit == null || !unit.isValid()) {
                        allUnitsSpawned = false;
                        break;
                    }
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.schedule(() -> {
                        if (unit != null && unit.isValid()) {
                            unit.kill();
                        }
                    },   10, TimeUnit.SECONDS); // Adjusted to  5 seconds
                } else {
                    allUnitsSpawned = false;
                    break;
                }
            }

            if (allUnitsSpawned) {
                playerData.subtractPoints(totalCost, player); // Subtract the total cost
                player.sendMessage(Bundle.get("spawn.arc-of-units.success", player.locale()));
            } else {
                // If any unit was not successfully spawned, give back the points
                playerData.addPoints(totalCost, player);
                player.sendMessage(Bundle.get("spawn.arc-of-units.failed", player.locale()));
            }
        } else {
            player.sendMessage(Bundle.get("spawn.arc-of-units.not-enough-points", player.locale()));
        }
    }
    private static void spawnDisruptUnit(Player player, World world, float playerX, float playerY) {
        PlayerData playerData = Players.getPlayer(player);
        int price =  100; // Set the price to  100
        if (playerData.getPoints() >= price) {
            playerData.subtractPoints((float) price, player);
    
            // Spawn either disrupt or eclipse unit for the player
            UnitType spawnType = Math.random() <  0.5 ? UnitTypes.disrupt : UnitTypes.eclipse;
            Unit spawned = spawnType.spawn(player.x, player.y);
    
            // Check if the spawned unit is alive
            if (spawned != null && !spawned.dead()) {
                Call.unitControl(player, spawned);
                Unit oldUnit = player.unit();
                oldUnit.kill();
    
                ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                executor.schedule(() -> {
                    if (spawned.dead()) {
                        // Return the item to the player
                        playerData.addPoints((float) price, player);
                        player.sendMessage(Bundle.get("unit.spawn.failed", player.locale));
                        player.sendMessage(Bundle.get("unit.died", player.locale));
                    }
                },  3, TimeUnit.SECONDS);
    
                player.sendMessage(Bundle.get("unit.brought", player.locale));
    
                // Spawn zenith or obviate units within a radius of  80 units from the player
                float radius =  80f;
                float angleStep =  360f /  6; // Divide the circle into  6 equal parts for even spacing
                int zenithSpawned =  0;
                int obviateSpawned =  0;
                for (int i =  0; i <  6; i++) {
                    float angle = i * angleStep;
                    double radians = Math.toRadians(angle);
                    float x = playerX + radius * (float) Math.cos(radians);
                    float y = playerY + radius * (float) Math.sin(radians);
    
                    int intX = (int) x;
                    int intY = (int) y;
                    float worldX = intX * tilesize;
                    float worldY = intY * tilesize;
    
                    Tile tile = world.tileWorld(worldX, worldY);
                    if (tile != null) {
                        UnitType unitType = spawnType == UnitTypes.disrupt ? UnitTypes.zenith : UnitTypes.obviate;
                        if ((unitType == UnitTypes.zenith && zenithSpawned <  6) || (unitType == UnitTypes.obviate && obviateSpawned <  6)) {
                            Unit unit = unitType.spawn(worldX, worldY);
                            if (unit != null && unit.isValid()) {
                                if (unitType == UnitTypes.zenith) {
                                    zenithSpawned++;
                                } else {
                                    obviateSpawned++;
                                }
                                ScheduledExecutorService unitExecutor = Executors.newSingleThreadScheduledExecutor();
                                unitExecutor.schedule(() -> {
                                    if (unit != null && unit.isValid()) {
                                        unit.kill();
                                    }
                                },  6, TimeUnit.SECONDS); // Kill the zenith or obviate units after  6 seconds
                            }
                        }
                    }
                }
    
                // Spawn flare or avert units in a larger radius
                radius =  140f; // Increase the radius for the larger spawn
                angleStep =  360f /  6; // Divide the circle into  6 equal parts for even spacing
                int flareSpawned =  0;
                int avertSpawned =  0;
                ScheduledExecutorService flareExecutor = Executors.newSingleThreadScheduledExecutor();
                ScheduledExecutorService avertExecutor = Executors.newSingleThreadScheduledExecutor();
                for (int i =  0; i <  6; i++) {
                    float angle = i * angleStep;
                    double radians = Math.toRadians(angle);
                    float x = playerX + radius * (float) Math.cos(radians);
                    float y = playerY + radius * (float) Math.sin(radians);
    
    
                    int intX = (int) x;
                    int intY = (int) y;
                    float worldX = intX * tilesize;
                    float worldY = intY * tilesize;
    
                    Tile tile = world.tileWorld(worldX, worldY);
                    if (tile != null) {
                        UnitType unitType = i %  2 ==  0 ? UnitTypes.flare : UnitTypes.avert; // Alternate between flare and avert
                        Unit unit = unitType.spawn(worldX, worldY);
                        if (unit != null && unit.isValid()) {
                            if (unitType == UnitTypes.flare) {
                                flareSpawned++;
                            } else {
                                avertSpawned++;
                            }
                            ScheduledExecutorService unitExecutor = Executors.newSingleThreadScheduledExecutor();
                            unitExecutor.schedule(() -> {
                                if (unit != null && unit.isValid()) {
                                    unit.kill();
                                }
                            },  9, TimeUnit.SECONDS); // Kill the flare or avert units after  9 seconds
                            if (flareSpawned >  8 || avertSpawned >  8) {
                                flareExecutor.shutdown();
                                avertExecutor.shutdown();
                                break;
                            }
                        }
                    }
                }
    
                // Check if the player has left the spawned unit
                ScheduledExecutorService checkPlayerExecutor = Executors.newSingleThreadScheduledExecutor();
                checkPlayerExecutor.schedule(() -> {
                    if (!player.unit().equals(spawned)) {
                        flareExecutor.shutdown();
                        avertExecutor.shutdown();
                        player.sendMessage(Bundle.get("unit.spawn.player.left", player.locale));
                    }
                },  10, TimeUnit.SECONDS); // Check every  10 seconds
            } else {
                // Handle the case where the unit could not be spawned
                playerData.addPoints((float) price, player); // Return the points to the player
                player.sendMessage(Bundle.get("unit.spawn.failed", player.locale));
            }
        } else {
            player.sendMessage(Bundle.get("menu.units.not-enough.l", player.locale()));
        }
    }
    private static void specialSpawn(Player player, World world, float playerX, float playerY) {
    PlayerData playerData = Players.getPlayer(player);
    int price =  100; // Set the price to  200
    if (playerData.getPoints() >= price) {
        playerData.subtractPoints(price, player);

        // Define the units to spawn
        UnitType[] unitsToSpawn = {
            UnitTypes.corvus,
            UnitTypes.quell,
            UnitTypes.quell,
            UnitTypes.quell,
            UnitTypes.collaris,
            UnitTypes.conquer,
            UnitTypes.eclipse,
            UnitTypes.oct,
            UnitTypes.toxopid,
            UnitTypes.reign
        };

        // Spawn the units and apply the unmoving status effect
        for (UnitType unitType : unitsToSpawn) {
            Unit unit = unitType.spawn(playerX, playerY);
            if (unit != null && unit.isValid()) {
                unit.apply(StatusEffects.unmoving, Float.POSITIVE_INFINITY);
                unit.type.physics = false;
            }
        }

        player.sendMessage(Bundle.get("special.spawn.success", player.locale));
    } else {
        player.sendMessage(Bundle.get("menu.units.not-enough.l", player.locale()));
    }
}
}