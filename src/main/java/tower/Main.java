package tower;

import arc.Events;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.struct.*;
import arc.util.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.game.EventType.*;
import mindustry.gen.Call;
import mindustry.mod.Plugin;
import mindustry.net.Administration.ActionType;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.ShockMine;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitBlock;
import mindustry.world.meta.BlockFlag;
import useful.Bundle;

import static mindustry.Vars.*;

@SuppressWarnings("unused")
public class Main extends Plugin {

    public static ObjectMap<UnitType, Seq<ItemStack>> drops;
    public static float multiplier = 1f;
    public static float ARMOR = 1f;

    public static boolean isPath(Tile tile) {
        return tile.floor() == Blocks.darkPanel4 || tile.floor() == Blocks.darkPanel5;
    }

    public static boolean canBePlaced(Tile tile, Block block) {
        return !tile.getLinkedTilesAs(block, new Seq<>()).contains(Main::isPath);
    }

    @Override
    public void init() {
        Bundle.load(getClass());
        TowerPathfinder.load();

        drops = ObjectMap.of(
                UnitTypes.crawler, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.silicon, 3),
                UnitTypes.atrax, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.graphite, 10, Items.titanium, 5),
                UnitTypes.spiroct, ItemStack.list(Items.lead, 100, Items.graphite, 40, Items.silicon, 40, Items.thorium, 10),
                UnitTypes.arkyid, ItemStack.list(Items.copper, 300, Items.graphite, 80, Items.metaglass, 80, Items.titanium, 80, Items.thorium, 20, Items.phaseFabric, 10),
                UnitTypes.toxopid, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 120, Items.silicon, 120, Items.thorium, 40, Items.plastanium, 40, Items.surgeAlloy, 15, Items.phaseFabric, 5),

                UnitTypes.dagger, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.silicon, 3),
                UnitTypes.mace, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.graphite, 10, Items.titanium, 5),
                UnitTypes.fortress, ItemStack.list(Items.lead, 100, Items.graphite, 40, Items.silicon, 40, Items.thorium, 10),
                UnitTypes.scepter, ItemStack.list(Items.copper, 300, Items.silicon, 80, Items.metaglass, 80, Items.titanium, 80, Items.thorium, 20, Items.phaseFabric, 10),
                UnitTypes.reign, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 120, Items.silicon, 120, Items.thorium, 40, Items.plastanium, 40, Items.surgeAlloy, 15, Items.phaseFabric, 5),

                UnitTypes.nova, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.metaglass, 3),
                UnitTypes.pulsar, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.metaglass, 10),
                UnitTypes.quasar, ItemStack.list(Items.lead, 100, Items.metaglass, 40, Items.silicon, 40, Items.titanium, 80, Items.thorium, 10),
                UnitTypes.vela, ItemStack.list(Items.copper, 300, Items.metaglass, 80, Items.graphite, 80, Items.titanium, 60, Items.plastanium, 20, Items.surgeAlloy, 5),
                UnitTypes.corvus, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 100, Items.silicon, 100, Items.metaglass, 120, Items.titanium, 120, Items.thorium, 60, Items.surgeAlloy, 10, Items.phaseFabric, 10),

                UnitTypes.flare, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.graphite, 3),
                UnitTypes.horizon, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.graphite, 10),
                UnitTypes.zenith, ItemStack.list(Items.lead, 100, Items.graphite, 40, Items.silicon, 40, Items.titanium, 30, Items.plastanium, 10),
                UnitTypes.antumbra, ItemStack.list(Items.copper, 300, Items.graphite, 80, Items.metaglass, 80, Items.titanium, 60, Items.surgeAlloy, 15),
                UnitTypes.eclipse, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 120, Items.silicon, 120, Items.titanium, 120, Items.thorium, 40, Items.plastanium, 40, Items.surgeAlloy, 5, Items.phaseFabric, 10),

                UnitTypes.mono, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.silicon, 3),
                UnitTypes.poly, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.silicon, 10, Items.titanium, 5),
                UnitTypes.mega, ItemStack.list(Items.lead, 100, Items.silicon, 40, Items.graphite, 40, Items.thorium, 10),
                UnitTypes.quad, ItemStack.list(Items.copper, 300, Items.silicon, 80, Items.metaglass, 80, Items.titanium, 80, Items.thorium, 20, Items.phaseFabric, 10),
                UnitTypes.oct, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 120, Items.silicon, 120, Items.thorium, 40, Items.plastanium, 40, Items.surgeAlloy, 15, Items.phaseFabric, 5),

                // These units are ships, and need water to be placed and work
                // UnitTypes.risso, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.metaglass, 3),
                // UnitTypes.minke, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.metaglass, 10),
                // UnitTypes.bryde, ItemStack.list(Items.lead, 100, Items.metaglass, 40, Items.silicon, 40, Items.titanium, 80, Items.thorium, 10),
                // UnitTypes.sei, ItemStack.list(Items.copper, 300, Items.metaglass, 80, Items.graphite, 80, Items.titanium, 60, Items.plastanium, 20, Items.surgeAlloy, 5),
                // UnitTypes.omura, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 100, Items.silicon, 100, Items.metaglass, 120, Items.titanium, 120, Items.thorium, 60, Items.surgeAlloy, 10, Items.phaseFabric, 10),
                // UnitTypes.retusa, ItemStack.list(Items.copper, 20, Items.lead, 10, Items.metaglass, 3),
                // UnitTypes.oxynoe, ItemStack.list(Items.copper, 30, Items.lead, 40, Items.metaglass, 10),
                // UnitTypes.cyerce, ItemStack.list(Items.lead, 100, Items.metaglass, 40, Items.silicon, 40, Items.titanium, 80, Items.thorium, 10),
                // UnitTypes.aegires, ItemStack.list(Items.copper, 300, Items.metaglass, 80, Items.graphite, 80, Items.titanium, 60, Items.plastanium, 20, Items.surgeAlloy, 5),
                // UnitTypes.navanax, ItemStack.list(Items.copper, 400, Items.lead, 400, Items.graphite, 100, Items.silicon, 100, Items.metaglass, 120, Items.titanium, 120, Items.thorium, 60, Items.surgeAlloy, 10, Items.phaseFabric, 10),
                
                // Player Units
                // UnitTypes.alpha, ItemStack.list(Items.copper, 30, Items.lead, 30, Items.graphite, 20, Items.silicon, 20, Items.metaglass, 20),
                // UnitTypes.beta, ItemStack.list(Items.titanium, 40, Items.thorium, 20),
                // UnitTypes.gamma, ItemStack.list(Items.plastanium, 20, Items.surgeAlloy, 10, Items.phaseFabric, 10),

                UnitTypes.stell, ItemStack.list(Items.beryllium, 20, Items.silicon, 25),
                UnitTypes.locus, ItemStack.list(Items.beryllium, 20, Items.graphite, 20, Items.silicon, 20, Items.tungsten, 15),
                UnitTypes.precept, ItemStack.list(Items.beryllium, 45, Items.graphite, 25, Items.silicon, 50, Items.tungsten, 50, Items.surgeAlloy, 75, Items.thorium, 40),
                UnitTypes.vanquish, ItemStack.list(Items.beryllium, 80, Items.graphite, 50, Items.silicon, 100, Items.tungsten, 120, Items.oxide, 60, Items.surgeAlloy, 125, Items.thorium, 100, Items.phaseFabric, 60),
                UnitTypes.conquer, ItemStack.list(Items.beryllium, 250, Items.graphite, 225, Items.silicon, 125, Items.tungsten, 140, Items.oxide, 120, Items.carbide, 240, Items.surgeAlloy, 250, Items.thorium, 240, Items.phaseFabric, 120),

                UnitTypes.elude, ItemStack.list(Items.beryllium, 6, Items.graphite, 25, Items.silicon, 35),
                UnitTypes.avert, ItemStack.list(Items.beryllium, 24, Items.graphite, 50, Items.silicon, 30, Items.tungsten, 20, Items.oxide, 20),
                UnitTypes.obviate, ItemStack.list(Items.beryllium, 48, Items.graphite, 75, Items.silicon, 50, Items.tungsten, 45, Items.carbide, 50, Items.thorium, 40, Items.phaseFabric, 75),
                UnitTypes.quell, ItemStack.list(Items.beryllium, 96, Items.graphite, 100, Items.silicon, 140, Items.tungsten, 70, Items.oxide, 60, Items.carbide, 75, Items.surgeAlloy, 60, Items.thorium, 100, Items.phaseFabric, 125),
                UnitTypes.disrupt, ItemStack.list(Items.beryllium, 122, Items.graphite, 125, Items.silicon, 155, Items.tungsten, 100, Items.oxide, 120, Items.carbide, 240, Items.surgeAlloy, 120, Items.thorium, 240, Items.phaseFabric, 250),

                UnitTypes.merui, ItemStack.list(Items.beryllium, 25, Items.silicon, 35, Items.tungsten, 10),
                UnitTypes.cleroi, ItemStack.list(Items.beryllium, 35, Items.graphite, 20, Items.silicon, 25, Items.tungsten, 20, Items.oxide, 20),
                UnitTypes.anthicus, ItemStack.list(Items.beryllium, 50, Items.graphite, 25, Items.silicon, 50, Items.tungsten, 65, Items.oxide, 75, Items.thorium, 40),
                UnitTypes.tecta, ItemStack.list(Items.beryllium, 100, Items.graphite, 50, Items.silicon, 140, Items.tungsten, 120, Items.oxide, 125, Items.surgeAlloy, 60, Items.thorium, 100, Items.phaseFabric, 125),
                UnitTypes.collaris, ItemStack.list(Items.beryllium, 135, Items.graphite, 90, Items.silicon, 175, Items.tungsten, 155, Items.oxide, 250, Items.carbide, 240, Items.surgeAlloy, 120, Items.thorium, 240, Items.phaseFabric, 120)
                // These units cannot harm
                // UnitTypes.evoke, ItemStack.list(Items.beryllium, 50, Items.graphite, 50, Items.silicon, 50),
                // UnitTypes.incite, ItemStack.list(Items.tungsten, 25, Items.oxide, 25, Items.carbide, 50),
                // UnitTypes.emanate, ItemStack.list(Items.surgeAlloy, 25, Items.thorium, 25, Items.phaseFabric, 50)
        );

        content.units().each(type -> {
            type.mineWalls = type.mineFloor = type.targetAir = type.targetGround = false;
            type.payloadCapacity = type.legSplashDamage = type.range = type.maxRange = type.mineRange = 0f;

            type.aiController = type.flying ? FlyingAI::new : GroundAI::new;
            type.targetFlags = new BlockFlag[]{BlockFlag.core};
        });
        netServer.admins.addActionFilter(action -> {
            if (action.tile == null) return true;
        
            if (action.type == ActionType.placeBlock || action.type == ActionType.breakBlock) {
                if (!(canBePlaced(action.tile, action.block) || action.block instanceof CoreBlock)) {
                    Bundle.label(action.player, 4f, action.tile.drawx(), action.tile.drawy(), "ui.forbidden");
                    return false; // Explicitly return false here
                }
            }
            return true; // Return true if no conditions are met that would return false
        });
        

        Timer.schedule(() -> state.rules.waveTeam.data().units.each(unit -> {
            var core = unit.closestEnemyCore();
            if (core == null || unit.dst(core) > 80f) return;

            core.damage(unit.health / Mathf.sqrt(multiplier), true);
            core.damage(unit.armor / Mathf.sqrt(ARMOR), true);
            unit.kill();
        }), 0f, 1f);

        Timer.schedule(() -> Bundle.popup(1f, 20, 50, 20, 450, 0, "ui.multiplier", Color.HSVtoRGB(multiplier * 120f, 100f, 100f), Strings.autoFixed(multiplier, 2)), 0f, 1f);
        Timer.schedule(() -> Bundle.popup(1f, 20, 50, 20, 500, 0, "ui.ARMOR", Color.HSVtoRGB(240f, 100f, 100f), Strings.autoFixed(ARMOR, 2)), 0f, 1f);

        Events.on(WorldLoadEvent.class, event -> multiplier = 1f);
        Events.on(WorldLoadEvent.class, event -> ARMOR = 1f);
        Events.on(WaveEvent.class, event -> multiplier = Mathf.clamp(((state.wave * state.wave / 3200f) + 0.5f), multiplier, 100f));
        Events.on(WaveEvent.class, event -> ARMOR = Mathf.clamp(((state.wave * state.wave / 3200f) + 0.5f), ARMOR, 100f));
        Events.on(UnitDestroyEvent.class, event -> {
            if (event.unit.team != state.rules.waveTeam) return;

            var core = event.unit.closestEnemyCore();
            var drop = drops.get(event.unit.type);

            if (core == null || drop == null) return;

            var builder = new StringBuilder();

            drop.each(stack -> {
                int amount = Mathf.random(stack.amount - stack.amount / 2, stack.amount + stack.amount / 2);

                builder.append("[accent]+").append(amount).append(" [red]").append(stack.item.emoji()).append("  ");
                Call.transferItemTo(event.unit, stack.item, core.acceptStack(stack.item, amount, core), event.unit.x, event.unit.y, core);
            });

            Call.label(builder.toString(), 1f, event.unit.x + Mathf.range(4f), event.unit.y + Mathf.range(4f));
        });

        Events.on(UnitSpawnEvent.class, event -> {
            if (event.unit.team != state.rules.waveTeam) return;

            event.unit.health(event.unit.maxHealth * multiplier);
            event.unit.maxHealth(event.unit.maxHealth * multiplier);
            event.unit.armor(event.unit.armor * ARMOR);

            event.unit.damageMultiplier(0f);
            event.unit.apply(StatusEffects.disarmed, Float.POSITIVE_INFINITY);
        });
    }
}