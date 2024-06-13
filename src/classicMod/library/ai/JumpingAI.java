package classicMod.library.ai;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
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
import mindustry.world.blocks.environment.StaticTree;
import mindustry.world.blocks.environment.StaticWall;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
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

    boolean once; boolean oS;

    protected static final Vec2 v1 = new Vec2();

    @Override
    public void init() {
        super.init();
        lastHealth = unit.health;
    }

    @Override
    public void updateVisuals(){
        if(unit.isFlying()){
            unit.lookAt(unit.prefRotation());
        }
    }

    public void OverrideVec2(){
        if(BlockOn() != null && SolidOn() == null) { vec.set(unit.lastX, unit.lastY).sub(unit); } else {
            unit.elevation = 0;
            unit.moveAt(vec);
        }
    }

    @Override
    public void updateMovement() {

        if(unit.type instanceof JumpingUnitType Ju) {
            Building core = unit.closestEnemyCore();


           /* if (core != null && unit.within(core, unit.range() / 1.3f + core.block.size * tilesize / 2f)) {
                target = core;
                for (var mount : unit.mounts) {
                    if (mount.weapon.controllable && mount.weapon.bullet.collidesGround) {
                        mount.target = core;
                    }
                }
            }*/

            if ((core == null || !unit.within(core, 0.5f))) {
                boolean move = (Ju.getTimingSine(this) >= 0.5f && !hit);
                stopMoving = false;

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

                if(!move && !once){
                    if(BuildOn() != null) {
                        if (BlockOn() != null) if (BuildOn().team != unit.team) {
                            DamageBuild(BuildOn());
                            oS = true;
                        }
                    }
                    if(TileOn() != null){
                        if(FloorOn() != null) { Stomp.at(unit.x, unit.y, FloorOn().isLiquid ? 1f : 0.5f, TileOn().floor().mapColor); }
                        else { Stomp.at(unit.x, unit.y, 1f, TileOn().floor().mapColor); }
                    }
                    once = true;
                }
                if(move && once && !stopMoving){ once = false; }

                if (move && !stopMoving){
                    pathfind(Pathfinder.fieldCore, Pathfinder.costGround);
                    /*if(SolidOn() == null) { pathfind(Pathfinder.fieldCore, Pathfinder.costLegs); unit.elevation = 1; } else
                    if(BlockOn() != null) { pathfind(Pathfinder.fieldCore, Pathfinder.costLegs); unit.elevation = 1; } else {
                        unit.elevation = 0;
                    }*/
                }
                faceMovement();
            }
        }else{
            unit.remove();
        }
    }

    Block Analyze(Tile v){
        Block f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree)) {
                f = v.block();
            }
        }
        return f;
    }

    Building AnalyzeBuild(Tile v){
        Building f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree)) {
                f = v.build;
            }
        }
        return f;
    }

    public void pathfind(int pathTarget, int costType){
        v1.set(unit);
        Tile tile = unit.tileOn();
        if(tile == null) return;
        Tile targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, costType, pathTarget));
        Block f = Analyze(TileOn(targetTile.worldx(), targetTile.worldy())); //Checks ahead of the tile.
        unit.elevation = (f != null || BlockOn() != null) ? 1 : 0;

        if(f != null) {
            targetTile = TileOn(targetTile.worldx(), targetTile.worldy())
            /*if(oS) {
                Wave();
                DamageBuild(AnalyzeBuild(TileOn(targetTile.worldx(), targetTile.worldy())));
                oS = false;
            }*/

            //targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, Pathfinder.costLegs, pathTarget));
            //f = Analyze(TileOn(targetTile.worldx(), targetTile.worldy()));
        }

        if(tile == targetTile || (costType == Pathfinder.costNaval && !targetTile.floor().isLiquid)) return;

        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
    }

    public void Wave(){
        Fx.shockwave.at(unit.x, unit.y, 0f, Color.valueOf("ffd27e"));
    }

    public void DamageBuild(Building b) {
        if(b != null){
            b.damage(120);
        }
    }

    public Tile TileOn(float x, float y){
        return Vars.world.tile(Mathf.round(x / Vars.tilesize), Mathf.round(y / Vars.tilesize));
    }
    public Tile TileOn(){
        return TileOn(unit.x, unit.y);
    }

    public Block SolidOn(){
        var v = TileOn();
        Block f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor)) {
                if(v.block() instanceof StaticWall || v.block() instanceof StaticTree) f = v.block();
            }
        }
        return f;
    }

    public Block BlockOn(){
        var v = TileOn();
        Block f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree)) {
                f = v.block();
            }
        }
        return f;
    }

    public Building BuildOn(){
        var v = TileOn();
        Building f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree)) {
                f = v.build;
            }
        }
        return f;
    }

    public Floor FloorOn(){
        var v = TileOn();
        Floor f = null;
        if(v != null) {
            if (v.block() instanceof Floor a) {
                f = a;
            }
        }
        return f;
    }
}
