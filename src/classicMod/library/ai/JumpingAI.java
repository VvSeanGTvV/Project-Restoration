package classicMod.library.ai;

import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import classicMod.content.ExtendedFx;
import classicMod.library.animdustry.JumpingUnitType;
import mindustry.Vars;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.Prop;
import mindustry.world.blocks.environment.StaticTree;
import mindustry.world.blocks.environment.StaticWall;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static classicMod.library.ui.UIExtended.fdelta;
import static mindustry.Vars.*;

public class JumpingAI extends AIController {

    public float timing;

    public float timingY;

    public boolean stopMoving;

    public boolean hit;

    private float lastHealth;

    public float hitDelay;

    private float lH;

    private boolean inraged;
    private Teamc targetInraged;

    public Effect Stomp;

    boolean once;

    int size = 3;

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

    public Tile[][] TileUniformUnitSurround;

    @Override
    public void updateMovement() {

        if(unit.type instanceof JumpingUnitType Ju) {
            Stomp = Ju.StompEffect;
            Building core = unit.closestEnemyCore();

            if ((core == null || !unit.within(core, 0.5f))) {
                boolean move = (Ju.getTimingSine(this) >= 0.5f && !hit);
                stopMoving = false;

                if(lastHealth > unit.health){
                    hit = true;
                    stopMoving = true;
                    move = false;

                    if(hitDelay >= 2f){
                        hit = false;
                        lastHealth = unit.health;
                        hitDelay = 0;
                    }
                }
                if(lastHealth < unit.health){
                    lastHealth = unit.health;
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
                    SurroundingBlock(size);
                    if(isSurroundedBlockEnemy(size, unit.team)){
                        Wave(Ju);

                        for (int x = 0; x < 3; x++) {
                            for (int y = 0; y < 3; y++) {
                                DamageBuild(TileUniformUnitSurround[y][x].build);
                            }
                        }
                    }

                    if(Ju.healPercent / 60f > 0f && Ju.healRange > 0f){
                        var baller = Units.closest(unit.team, unit.x, unit.y, Ju.healRange, u -> u.isValid() && u.health < u.maxHealth && u != this.unit);
                        if(baller != null){
                            Fx.heal.at(baller);
                            baller.heal(Ju.healPercent / 60f);
                        }
                    }

                    if(TileOn() != null){
                        if(FloorOn() != null) { Fx.unitLand.at(unit.x, unit.y, FloorOn().isLiquid ? 1f : 0.5f, TileOn().floor().mapColor); }
                        else { Fx.unitLand.at(unit.x, unit.y, 1f, TileOn().floor().mapColor); }
                    }
                    once = true;
                }
                if(move && once && !stopMoving){
                    once = false;
                }

                if (move && !stopMoving){
                    pathfind(Pathfinder.fieldCore, Pathfinder.costGround);
                }
                faceMovement();
            }
        }else{
            unit.remove();
        }
    }

    void SurroundingBlock(int size){
        if(size < 2) return;
        TileUniformUnitSurround = new Tile[size][size];
        boolean odd = ((size % 2) != 0);
        for (int x = 0; x < size; x++){
            for (int y = 0; y < size; y++){
                var xv = tilesize * (x - Mathf.floor((float) size / 2));
                var yv = tilesize * (y - Mathf.floor((float) size / 2));
                TileUniformUnitSurround[y][x] = TileOn(unit.x + xv, unit.y + yv);
            }
        }
    }

    boolean isSurroundedBlock(int size){
        int yeet = 0;
        boolean ret;
        if(TileUniformUnitSurround != null) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    yeet += Mathf.sign(TileUniformUnitSurround[y][x] != null);
                }
            }
            ret = (yeet >= TileUniformUnitSurround.length);
        } else {
            ret = false;
        }
        return ret;
    }

    boolean isSurroundedBlockEnemy(int size, Team team){
        int yeet = 0;
        boolean ret;
        if(TileUniformUnitSurround != null) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    var v = TileUniformUnitSurround[y][x];
                    if (v != null) {
                        if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree || v.block() instanceof Prop))
                            yeet += Mathf.sign(TileUniformUnitSurround[y][x] != null && TileUniformUnitSurround[y][x].build.team != team);
                    }
                }
            }
            ret = (yeet >= TileUniformUnitSurround.length);
        } else {
            ret = false;
        }
        return ret;
    }

    Block Analyze(Tile v){
        Block f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree || v.block() instanceof Prop)) {
                f = v.block();
            }
        }
        return f;
    }

    Block AnalyzeTeam(Tile v){
        Team team = unit.team;
        Block f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree || v.block() instanceof Prop)) {
                if(team != v.team()) f = v.block();
            }
        }
        return f;
    }

    public void pathfind(int pathTarget, int costType){
        v1.set(unit);
        Tile tile = unit.tileOn();

        if(tile == null) return;
        Tile targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, costType, pathTarget));
        Block f = AnalyzeTeam(TileOn(targetTile.worldx(), targetTile.worldy())); //Checks ahead of the tile.

        unit.elevation = (f != null || BlockOn() != null || isSurroundedBlock(size)) ? 1 : 0;

        if(f != null) {
            targetTile = TileOn(targetTile.worldx(), targetTile.worldy());
            //targetTile = pathfinder.getTargetTile(tile, pathfinder.getField(unit.team, Pathfinder.costLegs, pathTarget));

            //f = Analyze(TileOn(targetTile.worldx(), targetTile.worldy()));
        }

        if(tile == targetTile || (costType == Pathfinder.costNaval && !targetTile.floor().isLiquid)) return;

        unit.movePref(vec.trns(unit.angleTo(targetTile.worldx(), targetTile.worldy()), unit.speed()));
    }

    public void Wave(JumpingUnitType Ju){
        if(Ju != null) {
            if (Ju.StompExplosion) Ju.StompExplosionEffect.at(unit.x, unit.y, 0f, Ju.StompColor);
            Ju.StompEffect.at(unit.x, unit.y, 10f, Ju.StompColor);
            //ExtendedFx.dynamicWave.at(unit.x, unit.y, 10f, Color.valueOf("ffd27e"));
        }
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
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree || v.block() instanceof Prop)) {
                f = v.block();
            }
        }
        return f;
    }

    public Building BuildOn(){
        var v = TileOn();
        Building f = null;
        if(v != null) {
            if (!(v.block() instanceof Floor || v.block() instanceof StaticWall || v.block() instanceof StaticTree || v.block() instanceof Prop)) {
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
