package classicMod.library.ai.factoryai;

import arc.math.Mathf;
import arc.math.geom.*;
import classicMod.library.ai.RallyAI;
import classicMod.library.blocks.legacyBlocks.*;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.liquid.Conduit;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockGroup;

import static classicMod.content.ClassicVars.*;

public class FactorySuicideAI extends RallyAI {
    static boolean blockedByBlock;

    @Override
    public void updateUnit(){
        if(unit.team == Vars.state.rules.defaultTeam) {
            if (!(unit instanceof BuildingTetherc tether) || tether.building() == null) return;
            if (!(tether.building().block instanceof LegacyUnitFactory block)) return;
            if (!(tether.building() instanceof LegacyUnitFactory.LegacyUnitFactoryBuild build)) return;
        }

        if(state == UnitState.attack) {
            if (Units.invalidateTarget(target, unit.team, unit.x, unit.y, Float.MAX_VALUE)) {
                target = null;
            }

            if (retarget()) {
                target = target(unit.x, unit.y, unit.range(), unit.type.targetAir, unit.type.targetGround);
            }

            Building core = unit.closestEnemyCore();

            boolean rotate = false, shoot = false, moveToTarget = false;

            if (target == null) {
                target = core;
            }

            if (!Units.invalidateTarget(target, unit, unit.range()) && unit.hasWeapons()) {
                rotate = true;
                shoot = unit.within(target, unit.type.weapons.first().bullet.range +
                        (target instanceof Building b ? b.block.size * Vars.tilesize / 2f : ((Hitboxc) target).hitSize() / 2f));

                //do not move toward walls or transport blocks
                if (!(target instanceof Building build && !(build.block instanceof CoreBlock) && (
                        build.block.group == BlockGroup.walls ||
                                build.block.group == BlockGroup.liquids ||
                                build.block.group == BlockGroup.transportation
                ))) {
                    blockedByBlock = false;

                    //raycast for target
                    boolean blocked = World.raycast(unit.tileX(), unit.tileY(), target.tileX(), target.tileY(), (x, y) -> {
                        for (Point2 p : Geometry.d4c) {
                            Tile tile = Vars.world.tile(x + p.x, y + p.y);
                            if (tile != null && tile.build == target) return false;
                            if (tile != null && tile.build != null && tile.build.team != unit.team()) {
                                blockedByBlock = true;
                                return true;
                            } else {
                                return tile == null || tile.solid();
                            }
                        }
                        return false;
                    });

                    //shoot when there's an enemy block in the way
                    if (blockedByBlock) {
                        shoot = true;
                    }

                    if (!blocked) {
                        moveToTarget = true;
                        //move towards target directly
                        unit.movePref(vec.set(target).sub(unit).limit(unit.speed()));
                    }
                }
            }

            if (!moveToTarget) {
                boolean move = true;

                //stop moving toward the drop zone if applicable
                if (core == null && Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
                    Tile spawner = getClosestSpawner();
                    if (spawner != null && unit.within(spawner, Vars.state.rules.dropZoneRadius + 120f)) {
                        move = false;
                    }
                }

                if (move) {
                    pathfind(Pathfinder.fieldCore);
                }
            }

            unit.controlWeapons(rotate, shoot);

            faceTarget();
        }
        if(state == UnitState.rally){
            if(retarget()){
                NearbyCenter();

                if(target != null && !Units.invalidateTarget(target, unit.team, unit.x, unit.y)){
                    state = UnitState.attack;
                }

                //if(target == null) target = unit.closestEnemyCore();
            }
            if(target == null) {
                NearbyCenter();
                building = Units.closestBuilding(unit.team, unit.x, unit.y, MaximumRangeCommand, b -> (b instanceof LegacyCommandCenter.LegacyCommandCenterBuild) && b.isValid() && !(b.isNull()));
                if (building != null) {
                    /*if(!unit.within(building, unit.type.range * 0.5f)){
                        pathfind(Pathfinder.fieldCore);
                        target = building;
                    }*/
                    moveTo(building, 65f + Mathf.randomSeed(unit.id) * 100);
                }
            } else {
                state = UnitState.attack;
            }
        }
        if(target == null && state != defaultState) state = defaultState;
    }

    @Override
    public Teamc target(float x, float y, float range, boolean air, boolean ground){
        return Units.closestTarget(unit.team, x, y, range, u -> u.checkTarget(air, ground), t -> ground &&
                !(t.block instanceof Conveyor || t.block instanceof Conduit)); //do not target conveyors/conduits
    }
}
