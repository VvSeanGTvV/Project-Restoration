package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.*;
import classicMod.content.RVars;
import classicMod.library.GeometryPlus;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.Turret;

public class NeoplasmAIController extends AIController {

    public Seq<Tile> DodgeTile = new Seq<>();
    public Seq<Unit> groups = new Seq<>();

    public boolean ignore;

    private Tile currentSafeTarget = null;
    private float rerouteCooldown = 0f; // Time left before recomputing a new safe tile

    // Use ObjectSet for fast lookup
    public ObjectSet<Unit> knownNeoplasms = new ObjectSet<>();
    public Seq<Unit> neoplasmGroup = new Seq<>();

    @Override
    public void updateUnit() {
        // Look for nearby untracked neoplasms
        Unit neo = Units.closest(unit.team, unit.x, unit.y, u ->
                u.controller() instanceof NeoplasmAIController &&
                        !knownNeoplasms.contains(u) &&
                        u != this.unit
        );

        if (neo != null) {
            knownNeoplasms.add(neo);
            neoplasmGroup.add(neo);
        }

        Seq<Unit> toRemove = new Seq<>();

        for (var neoplasm : neoplasmGroup) {
            if (neoplasm == null || !neoplasm.dead) continue;

            Tile deadTile = neoplasm.tileOn();
            if (deadTile != null && neoplasm.controller() instanceof NeoplasmAIController ai && !ai.ignore) {
                // Add central and surrounding tiles if not already in DodgeTile
                for (var point : GeometryPlus.d8plus) { // includes center + 8 surrounding
                    Tile t = Vars.world.tile(deadTile.x + point.x, deadTile.y + point.y);
                    if (t != null) DodgeTile.addUnique(t);
                }
            }

            // Remove from group tracking
            toRemove.add(neoplasm);
            knownNeoplasms.remove(neoplasm);
        }

        neoplasmGroup.removeAll(toRemove);


        if (rerouteCooldown > 0) rerouteCooldown -= Time.delta;
        super.updateUnit();
    }

    @Nullable
    public Tile getClosestVent(boolean dontPlaceNearDangerous) {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy().removeAll(tile -> tile.build instanceof CausticHeart.HeartBuilding || tile.block() != Blocks.air);
        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);

        if (vent == null) return null;
        Building nearbyEnemyTile = Units.findEnemyTile(this.unit.team, vent.getX(), vent.getY(), 240f, building -> !building.dead);
        if (dontPlaceNearDangerous && nearbyEnemyTile != null) avaliableVents = avaliableVents.copy().removeAll(tile -> tile.dst(nearbyEnemyTile) <= 80f);
        vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        return (vent != null && !(vent.build instanceof CausticHeart.HeartBuilding)) ? vent : null;
    }

    @Nullable
    public Tile getClosestVent() {
        return getClosestVent(false);
    }

    public Tile closestDanger(){
        if (DodgeTile.size <= 0 || DodgeTile.isEmpty()) return null;
        return DodgeTile.copy().sort(tile1 -> tile1.dst(this.unit)).get(0);
    }

    public Tile getClosestTarget(int range, Tile closestDanger, Tile targetTile, Unit unit){
        int mid = Mathf.floor((float) range / 2);
        Seq<Tile> avaliableLand = new Seq<>();

        for (int y = -mid; y < range; y++){
            for (int x = -mid; x < range; x++){
                Tile tile = Vars.world.tile(unit.tileX() + x, unit.tileY() + y);
                if (
                        tile != null
                                && tile.block() == Blocks.air
                                && tile.floor() != null
                                && !tile.floor().isLiquid
                ) avaliableLand.add(tile);
            }
        }

        if (avaliableLand.size <= 0) return null;
        avaliableLand.removeAll(tile -> closestDanger.dst(tile) < 80);
        avaliableLand.removeAll(tile -> this.unit.dst(tile) < 10 && this.unit.dst(tile) > 100);
        avaliableLand.sort(tile -> tile.dst(targetTile));

        if (avaliableLand.size <= 0) return null;
        return avaliableLand.first();
    }

    @Nullable
    public Tile getEdgeEscapeTile(){
        Seq<Tile> candidates = new Seq<>();

        for(Tile danger : DodgeTile){
            for(Point2 offset : Geometry.d8){
                Tile neighbor = Vars.world.tile(danger.x + offset.x, danger.y + offset.y);
                if(
                        neighbor != null &&
                                !DodgeTile.contains(neighbor) &&
                                neighbor.block() == Blocks.air &&
                                neighbor.floor() != null &&
                                !neighbor.floor().isLiquid
                ){
                    candidates.add(neighbor);
                }
            }
        }

        if(candidates.isEmpty()) return null;
        return candidates.sort(tile -> tile.dst(unit)).first(); // Closest edge tile
    }

    public void routeAir(){
        Tile tile = this.unit.tileOn();
        Tile targetTile = target != null ? target.tileOn() : null;
        Tile nearDanger = closestDanger();

        // Reroute if too close to danger
        if (nearDanger != null && targetTile != null) {
            float distance = nearDanger.dst(tile);
            if (distance < 80f){
                Tile safeTarget = getClosestTarget(15, nearDanger, targetTile, unit);
                if (safeTarget != null){
                    targetTile = safeTarget;
                } else {
                    // Use edge escape if can't find a good safe target
                    targetTile = getEdgeEscapeTile();
                }
            }
        }

        // Move toward safe target
        if (targetTile != null && tile != targetTile){
            unit.movePref(vec.set(targetTile).sub(unit).limit(unit.speed()));
        }
    }

    @Override
    public void pathfind(int pathTarget) {
        int costType = this.unit.type.flowfieldPathType;
        Tile tile = this.unit.tileOn();
        if (tile == null) return;

        // Attempt to get a pathfinding target tile avoiding DodgeTile
        Tile targetTile = RVars.pathfinderCustom.getTargetTileDodge(
                tile,
                RVars.pathfinderCustom.getField(this.unit.team, costType, pathTarget),
                DodgeTile
        );

        Tile nearDanger = closestDanger();

        // Try rerouting if near danger
        if (nearDanger != null) {
            float distance = nearDanger.dst(tile);
            if (distance < 80f) {
                Tile safeTarget = getClosestTarget(15, nearDanger, targetTile, unit);
                if (safeTarget != null) {
                    targetTile = safeTarget;
                } else {
                    // Fallback: move to tile on edge of danger zone
                    targetTile = getEdgeEscapeTile();
                }

                // Optional: remove the danger if no alternative found
                if (targetTile == null) {
                    DodgeTile.remove(nearDanger);
                }
            }
        }

        if (targetTile != null && tile != targetTile) {
            // Only move if it's a valid move (don't path to self)
            if (costType != 2 || targetTile.floor().isLiquid) {
                this.unit.movePref(vec.trns(
                        this.unit.angleTo(targetTile.worldx(), targetTile.worldy()),
                        this.unit.speed()
                ));
            }
        }
    }
}
