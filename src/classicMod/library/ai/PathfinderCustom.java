//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package classicMod.library.ai;

import arc.*;
import arc.func.Prov;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import classicMod.content.RVars;
import classicMod.library.DirectionalGenerator;
import classicMod.library.GeometryPlus;
import classicMod.library.blocks.neoplasiaBlocks.CausticCord;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import classicMod.library.neoplasm.PheromoneMap;
import classicMod.library.neoplasm.ThreatMap;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.core.World;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.*;

import java.util.Arrays;
import java.util.Iterator;

import static classicMod.content.RVars.pathfinderCustom;
import static classicMod.library.ai.PathfinderExtended.*;
import static mindustry.Vars.*;
import static mindustry.Vars.spawner;

public class PathfinderCustom implements Runnable {
    private static final long maxUpdate = Time.millisToNanos(8);
    private static final int neverRefresh = Integer.MAX_VALUE;
    private static final int updateFPS = 60;
    private static final int updateInterval = 1000 / updateFPS;

    static int wwidth;
    static int wheight;
    static final int impassable = -1;

    public static final int fieldCore = 0, fieldVent = 1, fieldOres = 2;
    public static final Seq<Prov<Flowfield>> fieldTypes = Seq.with(
            (Prov<Flowfield>) EnemyCoreField::new,
            SteamVentField::new,
            OresField::new
    );

    public static final int
            costGround = 0,
            costLegs = 1,
            costNaval = 2,
            costNeoplasm = 3,
            costNone = 4,
            costHover = 5,
            maxCosts = 8;

    // costTypes now implicitly consider ThreatMap / PheromoneMap via helper
    public static final Seq<Pathfinder.PathCost> costTypes = Seq.with(
            // ground
            (team, tilePacked) -> {
                int base = baseGroundCost(team, tilePacked);
                return applyThreatAndPheromone(tilePacked, base, 1.0f, 0.4f);
            },

            // legs
            (team, tilePacked) -> {
                int base = baseLegCost(team, tilePacked);
                return applyThreatAndPheromone(tilePacked, base, 1.0f, 0.3f);
            },

            // water
            (team, tilePacked) -> {
                int base = baseNavalCost(team, tilePacked);
                return applyThreatAndPheromone(tilePacked, base, 1.2f, 0.2f);
            },

            // neoplasm veins (cords)
            (team, tilePacked) -> {
                int base = baseNeoplasmCost(team, tilePacked);
                return applyThreatAndPheromone(tilePacked, base, 1.5f, 0.8f);
            },

            // none (flat)
            (team, tilePacked) -> 1,

            // hover
            (team, tilePacked) -> {
                int base = baseHoverCost(team, tilePacked);
                return applyThreatAndPheromone(tilePacked, base, 1.0f, 0.3f);
            }
    );

    int[] tiles = new int[0];
    Flowfield[][][] cache;
    Seq<Flowfield> threadList = new Seq<>();
    Seq<Flowfield> mainList = new Seq<>();
    TaskQueue queue = new TaskQueue();
    @Nullable Thread thread;
    IntSeq tmpArray = new IntSeq();

    public PathfinderCustom() {
        clearCache();

        Events.on(EventType.WorldLoadEvent.class, event -> {
            stop();
            tiles = new int[Vars.world.width() * Vars.world.height()];
            wwidth = Vars.world.width();
            wheight = Vars.world.height();
            threadList = new Seq<>();
            mainList = new Seq<>();
            clearCache();

            for (int i = 0; i < tiles.length; ++i) {
                Tile tile = Vars.world.tiles.geti(i);
                tiles[i] = packTile(tile);
            }

            ThreatMap.init();
            PheromoneMap.init();

            if (state.rules.waveTeam.needsFlowField() && !net.client()) {
                preloadPath(getField(state.rules.waveTeam, costGround, fieldCore));
                Log.debug("Preloading ground enemy flowfield.");

                if (spawner.getSpawns().contains(t -> t.floor().isLiquid)) {
                    preloadPath(getField(state.rules.waveTeam, costNaval, fieldCore));
                    Log.debug("Preloading naval enemy flowfield.");
                }
            }

            start();
        });

        Events.on(EventType.ResetEvent.class, event -> stop());

        Events.on(EventType.TileChangeEvent.class, event -> updateTile(event.tile));

        Events.on(EventType.TilePreChangeEvent.class, event -> {
            Tile tile = event.tile;
            if (tile.solid()) {
                for (int i = 0; i < 4; ++i) {
                    Tile other = tile.nearby(i);
                    if (other != null && !other.solid()) {
                        boolean otherNearSolid = false;

                        for (int j = 0; j < 4; ++j) {
                            Tile othernear = other.nearby(j);
                            if (othernear != null && othernear.solid()) {
                                otherNearSolid = true;
                                break;
                            }
                        }

                        int arr = other.array();
                        if (!otherNearSolid && tiles.length > arr) {
                            tiles[arr] &= -2097153;
                        }
                    }
                }
            }
        });
    }

    private static int baseGroundCost(int team, int packed) {
        return (PathTile.allDeep(packed) ||
                (((PathTile.team(packed) == team && !PathTile.teamPassable(packed)) || PathTile.team(packed) == 0) && PathTile.solid(packed)))
                ? impassable
                : 1 +
                PathTile.health(packed) * 5 +
                (PathTile.nearSolid(packed) ? 2 : 0) +
                (PathTile.nearLiquid(packed) ? 6 : 0) +
                (PathTile.deep(packed) ? 6000 : 0) +
                (PathTile.damages(packed) ? 30 : 0);
    }

    private static int baseLegCost(int team, int packed) {
        return PathTile.legSolid(packed) ? impassable :
                1 +
                        (PathTile.deep(packed) ? 6000 : 0) +
                        (PathTile.solid(packed) ? 5 : 0);
    }

    private static int baseNavalCost(int team, int packed) {
        return (!PathTile.liquid(packed) || PathTile.solid(packed) ? 6000 : 1) +
                PathTile.health(packed) * 5 +
                (PathTile.nearGround(packed) || PathTile.nearSolid(packed) ? 14 : 0) +
                (PathTile.deep(packed) ? 0 : 1) +
                (PathTile.damages(packed) ? 35 : 0);
    }

    private static int baseNeoplasmCost(int team, int packed) {
        return (PathTile.deep(packed) || (PathTile.team(packed) == 0 && PathTile.solid(packed))) ? impassable :
                1 +
                        (PathTile.health(packed) * 3) +
                        (PathTile.nearSolid(packed) ? 2 : 0) +
                        (PathTile.nearLiquid(packed) ? 2 : 0);
    }

    private static int baseHoverCost(int team, int packed) {
        return ((((PathTile.team(packed) == team && !PathTile.teamPassable(packed)) || PathTile.team(packed) == 0) && PathTile.solid(packed)) ? impassable :
                1 +
                        PathTile.health(packed) * 5 +
                        (PathTile.nearSolid(packed) ? 2 : 0));
    }

    private static int applyThreatAndPheromone(int packed, int base, float threatWeight, float pheromoneWeight) {
        if (base == impassable) return impassable;

        Tile t = Vars.world.tiles.geti(packed);
        short threat = ThreatMap.threat != null ? ThreatMap.get(t) : 0;
        float pher = PheromoneMap.pheromone != null ? PheromoneMap.get(t) : 0f;

        float threatCost = threat * threatWeight;
        float pherCost = (pher > 0.01f ? (1f / pher) * 10f * pheromoneWeight : 10f * pheromoneWeight);

        int total = base + (int)threatCost + (int)pherCost;
        return Math.min(total, Integer.MAX_VALUE - 1);
    }

    private void clearCache() {
        cache = new Flowfield[256][5][5];
    }

    public int packTile(Tile tile) {
        boolean nearLiquid = false, nearSolid = false, nearLegSolid = false, nearGround = false,
                solid = tile.solid(), allDeep = tile.floor().isDeep(), nearDeep = allDeep;

        for (int i = 0; i < 4; i++) {
            Tile other = tile.nearby(i);
            if (other != null) {
                Floor floor = other.floor();
                boolean osolid = other.solid();
                if (floor.isLiquid && floor.isDeep()) nearLiquid = true;
                if (osolid && !other.block().teamPassable) nearSolid = true;
                if (!floor.isLiquid) nearGround = true;
                if (!floor.isDeep()) {
                    allDeep = false;
                } else {
                    nearDeep = true;
                }
                if (other.legSolid()) nearLegSolid = true;

                if (solid && !tile.block().teamPassable && other.array() < tiles.length) {
                    tiles[other.array()] |= PathTile.bitMaskNearSolid;
                }
            }
        }

        if (allDeep) {
            for (int i = 0; i < 4; i++) {
                Tile other = tile.nearby(Geometry.d8edge[i]);
                if (other != null && !other.floor().isDeep()) {
                    allDeep = false;
                    break;
                }
            }
        }

        int tid = tile.getTeamID();

        return PathTile.get(
                tile.build == null || !solid || tile.block() instanceof CoreBlock ? 0 : Math.min((int) (tile.build.health / 40), 80),
                tid == 0 && tile.build != null && state.rules.coreCapture ? 255 : tid,
                solid,
                tile.floor().isLiquid,
                tile.legSolid(),
                nearLiquid,
                nearGround,
                nearSolid,
                nearLegSolid,
                tile.floor().isDeep(),
                tile.floor().damages(),
                allDeep,
                nearDeep,
                tile.block().teamPassable
        );
    }

    public int get(int x, int y) {
        return tiles[x + y * wwidth];
    }

    private void start() {
        stop();
        if (!Vars.net.client()) {
            thread = new Thread(this, "CustomRPathfinder");
            thread.setPriority(1);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        queue.clear();
    }

    public void updateTile(Tile tile) {
        if (!Vars.net.client()) {
            tile.getLinkedTiles(t -> {
                int pos = t.array();
                if (pos < tiles.length) {
                    tiles[pos] = packTile(t);
                }
            });

            for (Flowfield path : mainList) {
                if (path != null) {
                    synchronized (path.targets) {
                        path.updateTargetPositions();
                    }
                }
            }

            queue.post(() -> {
                for (Flowfield data : threadList) {
                    data.dirty = true;
                }
            });
        }
    }

    @Override
    public void run() {
        while (!Vars.net.client()) {
            try {
                if (Vars.state.isPlaying()) {
                    ThreatMap.update();
                    PheromoneMap.decay(0.001f);

                    queue.run();

                    for (Flowfield data : threadList) {
                        if (data.dirty && data.frontier.size == 0) {
                            updateTargets(data);
                            data.dirty = false;
                        }
                        updateFrontier(data, maxUpdate);
                    }
                }

                try {
                    Thread.sleep(updateInterval);
                } catch (InterruptedException e) {
                    return;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public Flowfield getField(Team team, int costType, int fieldType) {
        if (cache[team.id][costType][fieldType] == null) {
            Flowfield field = fieldTypes.get(fieldType).get();
            field.team = team;
            field.cost = costTypes.get(costType);
            field.targets.clear();
            field.getPositions(field.targets);
            cache[team.id][costType][fieldType] = field;
            queue.post(() -> registerPath(field));
        }
        return cache[team.id][costType][fieldType];
    }

    @Nullable
    public Tile getTargetTileD4(Tile tile, Flowfield path) {
        return getTargetTile(tile, path, false);
    }

    public @Nullable Tile getTargetTile(Tile tile, Flowfield path) {
        return getTargetTile(tile, path, true);
    }

    public @Nullable Tile getTargetTile(Tile tile, Flowfield path, boolean diagonals) {
        return getTargetTile(tile, path, diagonals, 0);
    }

    public @Nullable Tile getTargetTile(Tile tile, Flowfield path, boolean diagonals, int avoidanceId) {
        if (tile == null) return null;

        if (!path.initialized || path.targets.size == 0) {
            return tile;
        }

        if (path.refreshRate > 0 && path.refreshRate != neverRefresh &&
                Time.timeSinceMillis(path.lastUpdateTime) > path.refreshRate &&
                path.frontier.size == 0) {

            path.lastUpdateTime = Time.millis();
            tmpArray.clear();
            path.getPositions(tmpArray);

            synchronized (path.targets) {
                path.updateTargetPositions();
                queue.post(() -> updateTargets(path));
            }
        }

        int[] values = path.hasComplete ? path.completeWeights : path.weights;
        int res = path.resolution;
        int ww = path.width;
        int apos = tile.x / res + tile.y / res * ww;
        int value = values[apos];

        var points = diagonals ? Geometry.d8 : Geometry.d4;
        int[] avoid = avoidanceId <= 0 ? null : avoidance.getAvoidance();

        Tile current = null;
        int tl = 0;

        for (Point2 point : points) {
            int dx = tile.x + point.x * res;
            int dy = tile.y + point.y * res;

            Tile other = world.tile(dx, dy);
            if (other == null) continue;

            int packed = dx / res + dy / res * ww;
            int avoidVal = avoid == null ? 0 : avoid[packed] > Integer.MAX_VALUE - avoidanceId ? 1 : 0;
            int cost = values[packed] + avoidVal;

            if (cost < value && avoidVal == 0 &&
                    (current == null || cost < tl) &&
                    path.passable(packed) &&
                    !(point.x != 0 && point.y != 0 &&
                            (!path.passable(((tile.x + point.x) / res + tile.y / res * ww)) ||
                                    !path.passable((tile.x / res + (tile.y + point.y) / res * ww))))) {

                current = other;
                tl = cost;
            }
        }

        if (current == null || tl == impassable ||
                (path.cost == costTypes.items[costGround] && current.dangerous() && !tile.dangerous())) {
            return tile;
        }

        return current;
    }

    private void updateTargets(Flowfield path) {
        path.search++;

        if (path.search >= Short.MAX_VALUE) {
            Arrays.fill(path.searches, (short) 0);
            path.search = 1;
        }

        synchronized (path.targets) {
            for (int i = 0; i < path.targets.size; i++) {
                int pos = path.targets.get(i);
                if (pos >= path.weights.length) continue;

                path.weights[pos] = 0;
                path.searches[pos] = (short) path.search;
                path.frontier.addFirst(pos);
            }
        }
    }

    private void preloadPath(Flowfield path) {
        path.updateTargetPositions();
        registerPath(path);
        updateFrontier(path, -1L);
    }

    private void registerPath(Flowfield path) {
        path.lastUpdateTime = Time.millis();
        path.setup();

        threadList.add(path);
        Core.app.post(() -> mainList.add(path));

        Arrays.fill(path.weights, impassable);

        for (int i = 0; i < path.targets.size; i++) {
            int pos = path.targets.get(i);
            path.weights[pos] = 0;
            path.frontier.addFirst(pos);
        }
    }

    private void updateFrontier(Flowfield path, long nsToRun) {
        boolean hadAny = path.frontier.size > 0;
        long start = Time.nanos();
        int counter = 0;

        while (path.frontier.size > 0) {
            int tile = path.frontier.removeLast();
            if (path.weights == null) return;

            int cost = path.weights[tile];
            if (path.frontier.size >= wwidth * wheight) {
                path.frontier.clear();
                return;
            }

            if (cost != impassable) {
                for (Point2 point : Geometry.d4) {
                    int dx = tile % wwidth + point.x;
                    int dy = tile / wwidth + point.y;
                    if (dx < 0 || dy < 0 || dx >= wwidth || dy >= wheight) continue;

                    int newPos = tile + point.x + point.y * wwidth;
                    int otherCost = path.cost.getCost(path.team.id, tiles[newPos]);
                    if ((path.weights[newPos] > cost + otherCost || path.searches[newPos] < path.search) &&
                            otherCost != impassable) {

                        path.frontier.addFirst(newPos);
                        path.weights[newPos] = cost + otherCost;
                        path.searches[newPos] = (short) path.search;
                    }
                }
            }

            if (nsToRun >= 0L && counter++ >= 200) {
                counter = 0;
                if (Time.timeSinceNanos(start) >= nsToRun) return;
            }
        }

        if (hadAny && path.frontier.size == 0) {
            System.arraycopy(path.weights, 0, path.completeWeights, 0, path.weights.length);
            path.hasComplete = true;
        }
    }

    // ===================== Flowfield classes =====================

    public static abstract class Flowfield {
        protected int refreshRate;
        protected Team team = Team.derelict;
        protected Pathfinder.PathCost cost = costTypes.get(costGround);
        protected volatile boolean hasComplete;
        protected boolean dirty = false;

        public int[] weights;
        public short[] searches;
        public int[] completeWeights;

        public final int resolution;
        public final int width, height;

        final IntQueue frontier = new IntQueue();
        final IntSeq targets = new IntSeq();
        int search = 1;
        long lastUpdateTime;
        boolean initialized;

        public Flowfield() {
            this(1);
        }

        public Flowfield(int resolution) {
            this.resolution = resolution;
            this.width = Mathf.ceil((float) wwidth / resolution);
            this.height = Mathf.ceil((float) wheight / resolution);
        }

        void setup() {
            int length = width * height;
            weights = new int[length];
            searches = new short[length];
            completeWeights = new int[length];
            frontier.ensureCapacity(length / 4);
            initialized = true;
        }

        public int getCost(int[] tiles, int pos) {
            return cost.getCost(team.id, tiles[pos]);
        }

        public boolean hasTargets() {
            return targets.size > 0;
        }

        public @Nullable Tile getNextTile(Tile from, boolean diagonals) {
            return pathfinderCustom.getTargetTile(from, this, diagonals);
        }

        public @Nullable Tile getNextTile(Tile from) {
            return pathfinderCustom.getTargetTile(from, this);
        }

        public @Nullable Tile getNextTile(Tile from, int unitAvoidanceId) {
            return pathfinderCustom.getTargetTile(from, this, true, unitAvoidanceId);
        }

        public boolean hasCompleteWeights() {
            return hasComplete && completeWeights != null;
        }

        public void updateTargetPositions() {
            targets.clear();
            getPositions(targets);
        }

        public boolean needsRefresh() {
            return refreshRate == 0;
        }

        protected boolean passable(int pos) {
            int amount = cost.getCost(team.id, pathfinderCustom.tiles[pos]);
            return amount != impassable && !(cost == costTypes.get(costNaval) && amount >= 6000);
        }

        protected abstract void getPositions(IntSeq out);
    }

    public static class PositionTarget extends Flowfield {
        public final Position position;

        public PositionTarget(Position position) {
            this.position = position;
            this.refreshRate = 900;
        }

        public void getPositions(IntSeq out) {
            out.add(Vars.world.packArray(World.toTile(position.getX()), World.toTile(position.getY())));
        }
    }

    public static class SteamVentField extends Flowfield {
        public SteamVentField() {
            refreshRate = 900;
        }

        protected void getPositions(IntSeq out) {
            for (Tile tile : SteamVents) {
                if (tile.floor().attributes.get(Attribute.steam) <= 0f) continue;
                float steam = 0f;
                for (int dy = -1; dy < 2; dy++) {
                    for (int dx = -1; dx < 2; dx++) {
                        Tile vents = Vars.world.tile(tile.x + dx, tile.y + dy);
                        if (vents == null || vents.build instanceof CausticHeart.HeartBuilding ||
                                vents.floor().attributes.get(Attribute.steam) <= 0f) continue;
                        steam += vents.floor().attributes.get(Attribute.steam);
                    }
                }
                if (steam >= 9f) {
                    out.add(tile.array());
                }
            }
        }
    }

    public static class OresField extends Flowfield {
        public OresField() {
            refreshRate = 900;
        }

        protected void getPositions(IntSeq out) {
            for (Tile tile : Ores) {
                if (RVars.CordCanDrill.contains(tile.wallDrop())) {
                    for (int i = 0; i < Geometry.d4.length; i++) {
                        Tile near = Vars.world.tile(tile.x + Geometry.d4[i].x, tile.y + + Geometry.d4[i].y);
                        if (near == null || near.build instanceof CausticCord.CordBuild) continue;
                        out.add(tile.array());
                    }
                }
                else
                {
                    if (RVars.CordCanDrill.contains(tile.drop()) && !(tile.build instanceof CausticCord.CordBuild)) out.add(tile.array());
                }
            }
        }
    }

    public static class EnemyCoreField extends Flowfield {
        public EnemyCoreField() {
            refreshRate = 900;
        }

        protected void getPositions(IntSeq out) {
            for (Building other : Vars.indexer.getEnemy(team, BlockFlag.core)) {
                out.add(other.tile.array());
            }

            if (Vars.state.rules.waves && team == Vars.state.rules.defaultTeam) {
                for (Tile other : Vars.spawner.getSpawns()) {
                    out.add(other.array());
                }
            }
        }
    }
}