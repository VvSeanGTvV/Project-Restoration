package classicMod.library.ai;

import arc.math.Mathf;
import classicMod.library.animdustry.JumpingUnitType;
import mindustry.Vars;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.*;

public class JumpingAI extends AIController {

    public float timing;

    public float timingY;

    public boolean stopMoving;

    public boolean hit;

    private float lastHealth;

    private int hitTimer;

    private float lH;

    private boolean inraged;
    private Teamc targetInraged;

    public Effect Stomp = Fx.unitLand;

    private boolean once;

    @Override
    public void init() {
        super.init();
        lastHealth = unit.health;
    }

    @Override
    public void updateMovement() {
        if(unit.type instanceof JumpingUnitType Ju) {
            Building core = unit.closestEnemyCore();

            if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)) {
                target = core;
                for (var mount : unit.mounts) {
                    if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                        mount.target = core;
                    }
                }
            }

            if ((core == null || !unit.within(core, 0.5f))) {
                boolean move = (Ju.getTimingSine(this) >= 0.5f && !hit);
                stopMoving = false;

                if(!move && !once){
                    DamageBuild();
                    if(TileOn() != null){
                        if(FloorOn() != null) Stomp.at(unit.x, unit.y, FloorOn().isLiquid ? 1f : 0.5f, TileOn().floor().mapColor);
                        else Stomp.at(unit.x, unit.y, 1f, TileOn().floor().mapColor);
                    }
                    once = true;
                }
                if(move && once){ once = false; }

                if(lH != unit.health){ hitTimer = 0; lH = unit.health; }
                if(lastHealth != unit.health){
                    hit = true;
                    hitTimer++;
                    stopMoving = true;
                    move = false;

                    if(hitTimer>200){
                        hit = false;
                        lastHealth = unit.health;
                        hitTimer = 0;
                    }
                }

                if (state.rules.waves && unit.team == state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, state.rules.dropZoneRadius + 120f)){ move = false; stopMoving = true; }
                    if (spawner == null && core == null){ move = false; stopMoving = true; }
                }

                //no reason to move if there's nothing there
                if (core == null && (!state.rules.waves || getClosestSpawner() == null)) {
                    move = false;
                    stopMoving = true;
                }

                if (move && !stopMoving){ if(BlockOn() == null) pathfind(Pathfinder.fieldCore); else {
                    if(core != null) moveTo(core, 0f);
                    if(getClosestSpawner() != null) moveTo(getClosestSpawner(), 0f);
                    }
                }
                faceMovement();
            }
        }else{
            unit.remove();
        }
    }

    public void DamageBuild() {
        Tile b = Vars.world.tile(Mathf.round(unit.x / Vars.tilesize), Mathf.round(unit.y / Vars.tilesize));
        if(!(b.block() instanceof Floor)){
            if(b.build != null){
                b.build.damage(120);
            }
        }
    }

    public Tile TileOn(){
        return Vars.world.tile(Mathf.round(unit.x / Vars.tilesize), Mathf.round(unit.y / Vars.tilesize));
    }

    public Block SolidOn(){
        var v = Vars.world.tile(Mathf.round(unit.x / Vars.tilesize), Mathf.round(unit.y / Vars.tilesize));
        Block f = null;
        if(!(v.block() instanceof Floor)){
            f = v.block();
        }
        return f;
    }

    public Block BlockOn(){
        var v = Vars.world.tile(Mathf.round(unit.x / Vars.tilesize), Mathf.round(unit.y / Vars.tilesize));
        Block f = null;
        if(!(v.block() instanceof Floor)){
            if(v.block().health != -1 || v.block().health != Integer.MAX_VALUE) f = v.block();
        }
        return f;
    }

    public Floor FloorOn(){
        var v = Vars.world.tile(Mathf.round(unit.x / Vars.tilesize), Mathf.round(unit.y / Vars.tilesize));
        Floor f = null;
        if(v.block() instanceof Floor a){
            f = a;
        }
        return f;
    }
}
