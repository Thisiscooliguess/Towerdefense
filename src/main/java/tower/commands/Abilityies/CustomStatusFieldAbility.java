package tower.commands.Abilityies;

import arc.math.*;
import arc.scene.ui.layout.Table;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.Ability;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.meta.*;

import static mindustry.Vars.tilesize;

public class CustomStatusFieldAbility extends Ability{
    public StatusEffect effect;
    public float duration = 60, reload = 100, range = 20; //
    public boolean onShoot = false;
    public Effect applyEffect = Fx.none;
    public Effect activeEffect = Fx.overdriveWave;
    public float effectX, effectY;
    public boolean parentizeEffects, effectSizeParam = true;

    protected float timer;

    CustomStatusFieldAbility(){}

    public CustomStatusFieldAbility(StatusEffect effect, float duration, float reload, float range){
        this.duration = duration;
        this.reload = reload;
        this.range = range;
        this.effect = effect;
    }

    @Override
    public void addStats(Table t){
        t.add("[lightgray]" + Stat.reload.localized() + ": [white]" + Strings.autoFixed(60f / reload, 2) + " " + StatUnit.perSecond.localized());
        t.row();
        t.add("[lightgray]" + Stat.shootRange.localized() + ": [white]" +  Strings.autoFixed(range / tilesize, 2) + " " + StatUnit.blocks.localized());
        t.row();
        t.add(effect.emoji() + " " + effect.localizedName);
    }

    @Override
    public void update(Unit unit){
        timer += Time.delta;
    
        if(timer >= reload && (!onShoot || unit.isShooting)){
            Units.nearby(unit.team, unit.x, unit.y, range, other -> {

                if(other.team != unit.team){
                    other.apply(effect, duration);
                    applyEffect.at(other, parentizeEffects);
                }
            });
    
            float x = unit.x + Angles.trnsx(unit.rotation, effectY, effectX), y = unit.y + Angles.trnsy(unit.rotation, effectY, effectX);
            activeEffect.at(x, y, effectSizeParam ? range : unit.rotation, parentizeEffects ? unit : null);
            display=true;
            timer = 0f;
        }
    }
}