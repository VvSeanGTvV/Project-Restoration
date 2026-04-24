package classicMod.library.ai;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Time;
import classicMod.library.GeometryPlus;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import classicMod.library.neoplasm.ThreatMap;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.content.Blocks;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.Turret;

import static classicMod.content.RVars.pathfinderCustom;

public class NeoplasmAIController extends AIController {

    // tiles to avoid (explosions, dead neoplasm, etc.)
    public Seq<Tile> DodgeTile = new Seq<>();
    public Seq<Unit> groups = new Seq<>();

    public boolean ignore;

    private float rerouteCooldown = 0f;

    public ObjectSet<Unit> knownNeoplasms = new ObjectSet<>();
    public Seq<Unit> neoplasmGroup = new Seq<>();

    @Override
    public void updateUnit() {
        // discover nearby neoplasm units to track
        Unit neo = Units.closest(unit.team, unit.x, unit.y, u ->
                u.controller() instanceof NeoplasmAIController &&
                        !knownNeoplasms.contains(u) &&
                        u != this.unit
        );

        if (neo != null) {
            knownNeoplasms.add(neo);
            neoplasmGroup.add(neo);
        }

        // handle dead neoplasms -> add their area to DodgeTile
        Seq<Unit> toRemove = new Seq<>();
        for (var neoplasm : neoplasmGroup) {
            if (neoplasm == null || !neoplasm.dead) continue;

            Tile deadTile = neoplasm.tileOn();
            if (deadTile != null && neoplasm.controller() instanceof NeoplasmAIController ai && !ai.ignore) {
                for (var point : GeometryPlus.d8plus) {
                    Tile t = Vars.world.tile(deadTile.x + point.x, deadTile.y + point.y);
                    if (t != null) DodgeTile.addUnique(t);
                }
            }

            toRemove.add(neoplasm);
            knownNeoplasms.remove(neoplasm);
        }
        neoplasmGroup.removeAll(toRemove);

        if (rerouteCooldown > 0f) rerouteCooldown -= Time.delta;

        super.updateUnit();
    }

    // ---------- VENT TARGETING ----------

    public Tile getClosestVent(boolean dontPlaceNearDangerous) {
        Seq<Tile> avaliableVents = PathfinderExtended.SteamVents.copy()
                .removeAll(tile -> tile.build instanceof CausticHeart.HeartBuilding || tile.block() != Blocks.air);

        Tile vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        if (vent == null) return null;

        Building nearbyEnemyTile = Units.findEnemyTile(this.unit.team, vent.getX(), vent.getY(), 240f, building -> !building.dead);
        if (dontPlaceNearDangerous && nearbyEnemyTile != null) {
            avaliableVents = avaliableVents.copy().removeAll(tile -> tile.dst(nearbyEnemyTile) <= 80f);
            vent = Geometry.findClosest(this.unit.x, this.unit.y, avaliableVents);
        }

        return (vent != null && !(vent.build instanceof CausticHeart.HeartBuilding)) ? vent : null;
    }

    public Tile getClosestVent() {
        return getClosestVent(false);
    }

    // ---------- DANGER HANDLING ----------

    public Tile closestDanger(){
        if (DodgeTile.isEmpty()) return null;
        return DodgeTile.copy().sort(tile1 -> tile1.dst(this.unit)).get(0);
    }

    /** Find a safe tile near targetTile, away from closestDanger. */
    public Tile getClosestTarget(int range, Tile closestDanger, Tile targetTile, Unit unit){
        int mid = Mathf.floor((float) range / 2);
        Seq<Tile> avaliableLand = new Seq<>();

        for (int y = -mid; y < range; y++){
            for (int x = -mid; x < range; x++){
                Tile tile = Vars.world.tile(unit.tileX() + x, unit.tileY() + y);
                if (tile != null &&
                        tile.block() == Blocks.air &&
                        tile.floor() != null &&
                        !tile.floor().isLiquid) {
                    avaliableLand.add(tile);
                }
            }
        }

        if (avaliableLand.isEmpty()) return null;

        if (closestDanger != null) {
            avaliableLand.removeAll(tile -> closestDanger.dst(tile) < 80f);
        }

        avaliableLand.removeAll(tile -> {
            float d = unit.dst(tile);
            return d < 10f || d > 100f;
        });

        avaliableLand.sort(tile -> tile.dst(targetTile));
        return avaliableLand.isEmpty() ? null : avaliableLand.first();
    }

    /** Tile on the edge of danger cluster. */
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
        return candidates.sort(tile -> tile.dst(unit)).first();
    }

    // ---------- AIR ROUTING (OPTIONAL USE) ----------

    public void routeAir(){
        Tile tile = this.unit.tileOn();
        if (tile == null) return;

        Tile targetTile = target != null ? target.tileOn() : null;
        Tile nearDanger = closestDanger();

        if (nearDanger != null && targetTile != null) {
            float distance = nearDanger.dst(tile);
            if (distance < 80f){
                Tile safeTarget = getClosestTarget(15, nearDanger, targetTile, unit);
                if (safeTarget != null){
                    targetTile = safeTarget;
                } else {
                    targetTile = getEdgeEscapeTile();
                }
            }
        }

        if (targetTile != null && tile != targetTile){
            unit.movePref(vec.set(targetTile).sub(unit).limit(unit.speed()));
        }
    }

    // ---------- MAIN PATHFIND USING NEW PATHFINDER ----------

    @Override
    public void pathfind(int pathTarget) {
        int costType = this.unit.type.flowfieldPathType;
        Tile tile = this.unit.tileOn();
        if (tile == null) return;

        // base flowfield target
        PathfinderCustom.Flowfield field = pathfinderCustom.getField(this.unit.team, costType, pathTarget);
        Tile targetTile = pathfinderCustom.getTargetTile(tile, field, true);

        Tile nearDanger = closestDanger();

        // if near a DodgeTile danger, try to reroute locally
        if (nearDanger != null) {
            float distance = nearDanger.dst(tile);
            if (distance < 80f && rerouteCooldown <= 0f) {
                Tile safeTarget = getClosestTarget(15, nearDanger, targetTile, unit);
                if (safeTarget != null) {
                    targetTile = safeTarget;
                } else {
                    Tile edge = getEdgeEscapeTile();
                    if (edge != null) targetTile = edge;
                }
                rerouteCooldown = 20f; // small cooldown to avoid constant recompute
            }
        }

        // extra safety: avoid very high ThreatMap tiles if possible
        if (targetTile != null && ThreatMap.get(targetTile) > 120) {
            Tile best = null;
            float bestScore = Float.POSITIVE_INFINITY;

            for (Point2 p : Geometry.d8) {
                Tile n = Vars.world.tile(targetTile.x + p.x, targetTile.y + p.y);
                if (n == null) continue;
                if (n.block() != Blocks.air || (n.floor() != null && n.floor().isLiquid)) continue;

                float threat = ThreatMap.get(n);
                float dist = n.dst(tile);
                float score = threat * 2f + dist;
                if (score < bestScore) {
                    bestScore = score;
                    best = n;
                }
            }

            if (best != null) targetTile = best;
        }

        if (targetTile != null && tile != targetTile) {
            // for naval, ensure target is liquid; otherwise normal
            if (costType != Pathfinder.costNaval || targetTile.floor().isLiquid) {
                float angle = unit.angleTo(targetTile.worldx(), targetTile.worldy());
                unit.movePref(vec.trns(angle, unit.speed()));
            }
        }
    }
}
