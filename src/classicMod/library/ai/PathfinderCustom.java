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
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
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
    public static final Seq<Prov<Flowfield>> fieldTypes = Seq.with(new Prov[]{EnemyCoreField::new, SteamVentField::new, OresField::new});
    public static final int
            costGround = 0,
            costLegs = 1,
            costNaval = 2,
            costNeoplasm = 3,
            costNone = 4,
            costHover = 5,

    maxCosts = 8;

    public static final Seq<Pathfinder.PathCost> costTypes = Seq.with(
            //ground
            (team, tile) ->
                    (PathTile.allDeep(tile) || ((PathTile.team(tile) == team && !PathTile.teamPassable(tile)) || PathTile.team(tile) == 0) && PathTile.solid(tile)) ? impassable : 1 +
                            PathTile.health(tile) * 5 +
                            (PathTile.nearSolid(tile) ? 2 : 0) +
                            (PathTile.nearLiquid(tile) ? 6 : 0) +
                            (PathTile.deep(tile) ? 6000 : 0) +
                            (PathTile.damages(tile) ? 30 : 0),

            //legs
            (team, tile) ->
                    PathTile.legSolid(tile) ? impassable : 1 +
                            (PathTile.deep(tile) ? 6000 : 0) + //leg units can now drown
                            (PathTile.solid(tile) ? 5 : 0),

            //water
            (team, tile) ->
                    (!PathTile.liquid(tile) || PathTile.solid(tile) ? 6000 : 1) +
                            PathTile.health(tile) * 5 +
                            (PathTile.nearGround(tile) || PathTile.nearSolid(tile) ? 14 : 0) +
                            (PathTile.deep(tile) ? 0 : 1) +
                            (PathTile.damages(tile) ? 35 : 0),

            //neoplasm veins
            (team, tile) ->
                    (PathTile.deep(tile) || (PathTile.team(tile) == 0 && PathTile.solid(tile))) ? impassable : 1 +
                            (PathTile.health(tile) * 3) +
                            (PathTile.nearSolid(tile) ? 2 : 0) +
                            (PathTile.nearLiquid(tile) ? 2 : 0),

            //none (flat cost)
            (team, tile) -> 1,

            //hover
            (team, tile) ->
                    (((PathTile.team(tile) == team && !PathTile.teamPassable(tile)) || PathTile.team(tile) == 0) && PathTile.solid(tile)) ? impassable : 1 +
                            PathTile.health(tile) * 5 +
                            (PathTile.nearSolid(tile) ? 2 : 0)
    );
    int[] tiles = new int[0];
    Flowfield[][][] cache;
    Seq<Flowfield> threadList = new Seq();
    Seq<Flowfield> mainList = new Seq();
    TaskQueue queue = new TaskQueue();
    @Nullable
    Thread thread;
    IntSeq tmpArray = new IntSeq();

    public PathfinderCustom() {
        this.clearCache();
        Events.on(EventType.WorldLoadEvent.class, (event) -> {
            this.stop();
            this.tiles = new int[Vars.world.width() * Vars.world.height()];
            wwidth = Vars.world.width();
            wheight = Vars.world.height();
            this.threadList = new Seq();
            this.mainList = new Seq();
            this.clearCache();

            for(int i = 0; i < this.tiles.length; ++i) {
                Tile tile = Vars.world.tiles.geti(i);
                this.tiles[i] = this.packTile(tile);
            }

            for(int i = 0; i < tiles.length; i++){
                Tile tile = world.tiles.geti(i);
                tiles[i] = packTile(tile);
            }

            //don't bother setting up paths unless necessary
            if(state.rules.waveTeam.needsFlowField() && !net.client()){
                preloadPath(getField(state.rules.waveTeam, costGround, fieldCore));
                Log.debug("Preloading ground enemy flowfield.");

                //preload water on naval maps
                if(spawner.getSpawns().contains(t -> t.floor().isLiquid)){
                    preloadPath(getField(state.rules.waveTeam, costNaval, fieldCore));
                    Log.debug("Preloading naval enemy flowfield.");
                }

            }

            start();
        });

        Events.on(EventType.ResetEvent.class, (event) -> {
            this.stop();
        });
        Events.on(EventType.TileChangeEvent.class, (event) -> {
            this.updateTile(event.tile);
        });
        Events.on(EventType.TilePreChangeEvent.class, (event) -> {
            Tile tile = event.tile;
            if (tile.solid()) {
                for(int i = 0; i < 4; ++i) {
                    Tile other = tile.nearby(i);
                    if (other != null && !other.solid()) {
                        boolean otherNearSolid = false;

                        int arr;
                        for(arr = 0; arr < 4; ++arr) {
                            Tile othernear = other.nearby(i);
                            if (othernear != null && othernear.solid()) {
                                otherNearSolid = true;
                                break;
                            }
                        }

                        arr = other.array();
                        if (!otherNearSolid && this.tiles.length > arr) {
                            int[] var10000 = this.tiles;
                            var10000[arr] &= -2097153;
                        }
                    }
                }
            }

        });
    }

    private void clearCache() {
        this.cache = new Flowfield[256][5][5];
    }

    /** Packs a tile into its internal representation. */
    public int packTile(Tile tile){
        boolean nearLiquid = false, nearSolid = false, nearLegSolid = false, nearGround = false, solid = tile.solid(), allDeep = tile.floor().isDeep(), nearDeep = allDeep;

        for(int i = 0; i < 4; i++){
            Tile other = tile.nearby(i);
            if(other != null){
                Floor floor = other.floor();
                boolean osolid = other.solid();
                if(floor.isLiquid && floor.isDeep()) nearLiquid = true;
                //TODO potentially strange behavior when teamPassable is false for other teams?
                if(osolid && !other.block().teamPassable) nearSolid = true;
                if(!floor.isLiquid) nearGround = true;
                if(!floor.isDeep()){
                    allDeep = false;
                }else{
                    nearDeep = true;
                }
                if(other.legSolid()) nearLegSolid = true;

                //other tile is now near solid
                if(solid && !tile.block().teamPassable && other.array() < tiles.length){
                    tiles[other.array()] |= PathTile.bitMaskNearSolid;
                }
            }
        }

        //check diagonals for allDeep
        if(allDeep){
            for(int i = 0; i < 4; i++){
                Tile other = tile.nearby(Geometry.d8edge[i]);
                if(other != null && !other.floor().isDeep()){
                    allDeep = false;
                    break;
                }
            }
        }

        int tid = tile.getTeamID();

        return PathTile.get(
                tile.build == null || !solid || tile.block() instanceof CoreBlock ? 0 : Math.min((int)(tile.build.health / 40), 80),
                tid == 0 && tile.build != null && state.rules.coreCapture ? 255 : tid, //use teamid = 255 when core capture is enabled to mark out derelict structures
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
        return this.tiles[x + y * wwidth];
    }

    private void start() {
        this.stop();
        if (!Vars.net.client()) {
            this.thread = new Thread(this, "CustomRPathfinder");
            this.thread.setPriority(1);
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    private void stop() {
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }

        this.queue.clear();
    }

    public void updateTile(Tile tile) {
        if (!Vars.net.client()) {
            tile.getLinkedTiles((t) -> {
                int pos = t.array();
                if (pos < this.tiles.length) {
                    this.tiles[pos] = this.packTile(t);
                }

            });
            Iterator var2 = this.mainList.iterator();

            while(var2.hasNext()) {
                Flowfield path = (Flowfield)var2.next();
                if (path != null) {
                    synchronized(path.targets) {
                        path.updateTargetPositions();
                    }
                }
            }

            this.queue.post(() -> {
                Flowfield data;
                for(Iterator var1 = this.threadList.iterator(); var1.hasNext(); data.dirty = true) {
                    data = (Flowfield)var1.next();
                }

            });
        }
    }

    public void run() {
        while(!Vars.net.client()) {
            try {
                if (Vars.state.isPlaying()) {
                    this.queue.run();

                    Flowfield data;
                    for(Iterator var1 = this.threadList.iterator(); var1.hasNext(); this.updateFrontier(data, maxUpdate)) {
                        data = (Flowfield)var1.next();
                        if (data.dirty && data.frontier.size == 0) {
                            this.updateTargets(data);
                            data.dirty = false;
                        }
                    }
                }

                try {
                    Thread.sleep(16L);
                } catch (InterruptedException var3) {
                    return;
                }
            } catch (Throwable var4) {
                var4.printStackTrace();
            }
        }

    }

    public Flowfield getField(Team team, int costType, int fieldType) {
        if (this.cache[team.id][costType][fieldType] == null) {
            Flowfield field = (Flowfield)((Prov)fieldTypes.get(fieldType)).get();
            field.team = team;
            field.cost = (PathCost)costTypes.get(costType);
            field.targets.clear();
            field.getPositions(field.targets);
            this.cache[team.id][costType][fieldType] = field;
            this.queue.post(() -> {
                this.registerPath(field);
            });
        }

        return this.cache[team.id][costType][fieldType];
    }

    @Nullable
    public Tile getTargetTileD4(Tile tile, Flowfield path) {
        return getTargetTile(tile, path, false);
    }

    public @Nullable Tile getTargetTile(Tile tile, Flowfield path){
        return getTargetTile(tile, path, true);
    }

    /** Gets next tile to travel to. Main thread only. */
    public @Nullable Tile getTargetTile(Tile tile, Flowfield path, boolean diagonals){
        return getTargetTile(tile, path, diagonals, 0);
    }


    public @Nullable Tile getTargetTile(Tile tile, Flowfield path, boolean diagonals, int avoidanceId){
        if(tile == null) return null;

        //uninitialized flowfields are not applicable
        //also ignore paths with no targets, there is no destination
        if(!path.initialized || path.targets.size == 0){
            return tile;
        }

        //if refresh rate is positive, queue a refresh
        if(path.refreshRate > 0 && path.refreshRate != neverRefresh && Time.timeSinceMillis(path.lastUpdateTime) > path.refreshRate && path.frontier.size == 0){
            path.lastUpdateTime = Time.millis();

            tmpArray.clear();
            path.getPositions(tmpArray);

            synchronized(path.targets){
                path.updateTargetPositions();

                //queue an update
                queue.post(() -> updateTargets(path));
            }
        }

        //use complete weights if possible; these contain a complete flow field that is not being updated
        int[] values = path.hasComplete ? path.completeWeights : path.weights;
        int res = path.resolution;
        int ww = path.width;
        int apos = tile.x/res + tile.y/res * ww;
        int value = values[apos];

        var points = diagonals ? Geometry.d8 : Geometry.d4;
        int[] avoid = avoidanceId <= 0 ? null : avoidance.getAvoidance();

        Tile current = null;
        int tl = 0;
        for(Point2 point : points){
            int dx = tile.x + point.x * res, dy = tile.y + point.y * res;

            Tile other = world.tile(dx, dy);
            if(other == null) continue;

            int packed = dx/res + dy/res * ww;
            int avoidance = avoid == null ? 0 : avoid[packed] > Integer.MAX_VALUE - avoidanceId ? 1 : 0;
            int cost = values[packed] + avoidance;

            if(cost < value && avoidance == 0 && (current == null || cost < tl) && path.passable(packed) &&
                    !(point.x != 0 && point.y != 0 && (!path.passable(((tile.x + point.x)/res + tile.y/res*ww)) || !path.passable((tile.x/res + (tile.y + point.y)/res*ww))))){ //diagonal corner trap
                current = other;
                tl = cost;
            }
        }

        if(current == null || tl == impassable || (path.cost == costTypes.items[costGround] && current.dangerous() && !tile.dangerous())) return tile;

        return current;
    }

    @Nullable
    public Tile getTargetTileDodge(Tile tile, Flowfield path, Seq<Tile> dangerTile) {
        if (tile == null) {
            return null;
        } else if (!path.initialized) {
            return tile;
        } else {
            if (path.refreshRate > 0 && Time.timeSinceMillis(path.lastUpdateTime) > (long)path.refreshRate) {
                path.lastUpdateTime = Time.millis();
                this.tmpArray.clear();
                path.getPositions(this.tmpArray);
                synchronized(path.targets) {
                    if (path.targets.size != 1 || this.tmpArray.size != 1 || path.targets.first() != this.tmpArray.first()) {
                        path.updateTargetPositions();
                        this.queue.post(() -> {
                            this.updateTargets(path);
                        });
                    }
                }
            }

            int[] values = path.hasComplete ? path.completeWeights : path.weights;
            int apos = tile.array();
            int value = values[apos];
            Tile current = null, danger = null;
            int tl = 0;
            Point2[] var8 = DirectionalGenerator.generateDirections(6, false);
            int var9 = var8.length;

            if (dangerTile.size > 0){
                danger = dangerTile.copy().sort(tile1 -> tile1.dst(tile)).get(0);
            }

            for(int var10 = 0; var10 < var9; ++var10) {
                Point2 point = var8[var10];
                int dx = tile.x + point.x;
                int dy = tile.y + point.y;
                Tile other = Vars.world.tile(dx, dy);
                if (other != null) {
                    if (danger == null) {
                        int packed = Vars.world.packArray(dx, dy);
                        if (values[packed] < value && (current == null || values[packed] < tl) && path.passable(packed) && (point.x == 0 || point.y == 0 || path.passable(Vars.world.packArray(tile.x + point.x, tile.y)) && path.passable(Vars.world.packArray(tile.x, tile.y + point.y)))) {
                            current = other;
                            tl = values[packed];
                        }
                    } else {
                        if (danger.dst(other) > 40){
                            int packed = Vars.world.packArray(dx, dy);
                            if (values[packed] < value && (current == null || values[packed] < tl) && path.passable(packed) && (point.x == 0 || point.y == 0 || path.passable(Vars.world.packArray(tile.x + point.x, tile.y)) && path.passable(Vars.world.packArray(tile.x, tile.y + point.y)))) {
                                current = other;
                                tl = values[packed];
                            }
                        }
                    }
                }
            }

            if (current != null && tl != -1 && (path.cost != ((PathCost[])costTypes.items)[0] || !current.dangerous() || tile.dangerous())) {
                /*Tile danger;
                Tile finalCurrent = current;
                if (dangerTile.size > 0) {
                    dangerTile.sort(tile1 -> tile1.dst(finalCurrent));
                    danger = dangerTile.get(0);
                } else {
                    danger = null;
                }
                if (danger != null){
                    int size = 4;
                    int div = size / 2;
                    Seq<Tile> closestTile = new Seq<>(size*size);

                    for (int y = -div; y < size; y++){
                        for (int x = -div; x < size; x++){
                            closestTile.add(Vars.world.tile(x + current.x, y + current.y));
                        }
                    }
                    closestTile.removeAll(tile1 -> tile1.dst(danger) < 40f);
                    closestTile.sort(tile1 -> tile1.dst(finalCurrent));
                    if (closestTile.size > 0) {
                        return closestTile.get(0);
                    } else {
                        return current;
                    }
                } else {
                    return current;
                }*/
                return current;
            } else {
                return tile;
            }
        }
    }

    /** Increments the search and sets up flow sources. Does not change the frontier. */
    private void updateTargets(Flowfield path){

        //increment search, but do not clear the frontier
        path.search++;

        //search overflow; reset everything.
        if(path.search >= Short.MAX_VALUE){
            Arrays.fill(path.searches, (short)0);
            path.search = 1;
        }

        synchronized(path.targets){
            //add targets
            for(int i = 0; i < path.targets.size; i++){
                int pos = path.targets.get(i);

                if(pos >= path.weights.length) continue;

                path.weights[pos] = 0;
                path.searches[pos] = (short)path.search;
                path.frontier.addFirst(pos);
            }
        }
    }

    private void preloadPath(Flowfield path) {
        path.updateTargetPositions();
        this.registerPath(path);
        this.updateFrontier(path, -1L);
    }

    private void registerPath(Flowfield path){
        path.lastUpdateTime = Time.millis();
        path.setup();

        threadList.add(path);

        //add to main thread's list of paths
        Core.app.post(() -> mainList.add(path));

        //fill with impassables by default
        Arrays.fill(path.weights, impassable);

        //add targets
        for(int i = 0; i < path.targets.size; i++){
            int pos = path.targets.get(i);
            path.weights[pos] = 0;
            path.frontier.addFirst(pos);
        }
    }

    private void updateFrontier(Flowfield path, long nsToRun) {
        boolean hadAny = path.frontier.size > 0;
        long start = Time.nanos();
        int counter = 0;

        while(path.frontier.size > 0) {
            int tile = path.frontier.removeLast();
            if (path.weights == null) {
                return;
            }

            int cost = path.weights[tile];
            if (path.frontier.size >= Vars.world.width() * Vars.world.height()) {
                path.frontier.clear();
                return;
            }

            if (cost != -1) {
                Point2[] var10 = Geometry.d4;
                int var11 = var10.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    Point2 point = var10[var12];
                    int dx = tile % wwidth + point.x;
                    int dy = tile / wwidth + point.y;
                    if (dx >= 0 && dy >= 0 && dx < wwidth && dy < wheight) {
                        int newPos = tile + point.x + point.y * wwidth;
                        int otherCost = path.cost.getCost(path.team.id, this.tiles[newPos]);
                        if ((path.weights[newPos] > cost + otherCost || path.searches[newPos] < path.search) && otherCost != -1) {
                            path.frontier.addFirst(newPos);
                            path.weights[newPos] = cost + otherCost;
                            path.searches[newPos] = (short)path.search;
                        }
                    }
                }
            }

            if (nsToRun >= 0L && counter++ >= 200) {
                counter = 0;
                if (Time.timeSinceNanos(start) >= nsToRun) {
                    return;
                }
            }
        }

        if (hadAny && path.frontier.size == 0) {
            System.arraycopy(path.weights, 0, path.completeWeights, 0, path.weights.length);
            path.hasComplete = true;
        }

    }

    /**
     * Data for a flow field to some set of destinations.
     * Concrete subclasses must specify a way to fetch costs and destinations.
     */
    public static abstract class Flowfield{
        /** Refresh rate in milliseconds. <= 0 to disable. */
        protected int refreshRate;
        /** Team this path is for. Set before using. */
        protected Team team = Team.derelict;
        /** Function for calculating path cost. Set before using. */
        protected PathCost cost = costTypes.get(costGround);
        /** Whether there are valid weights in the complete array. */
        protected volatile boolean hasComplete;
        /** If true, this flow field needs updating. This flag is only set to false once the flow field finishes and the weights are copied over. */
        protected boolean dirty = false;

        /** costs of getting to a specific tile */
        public int[] weights;
        /** search IDs of each position - the highest, most recent search is prioritized and overwritten */
        public short[] searches;
        /** the last "complete" weights of this tilemap. */
        public int[] completeWeights;

        /** Scaling factor. For example, resolution = 2 means tiles are twice as large. */
        public final int resolution;
        public final int width, height;

        /** search frontier, these are Pos objects */
        final IntQueue frontier = new IntQueue();
        /** all target positions; these positions have a cost of 0, and must be synchronized on! */
        final IntSeq targets = new IntSeq();
        /** current search ID */
        int search = 1;
        /** last updated time */
        long lastUpdateTime;
        /** whether this flow field is ready to be used */
        boolean initialized;

        public Flowfield(){
            this(1);
        }

        public Flowfield(int resolution){
            this.resolution = resolution;
            this.width = Mathf.ceil((float)wwidth / resolution);
            this.height = Mathf.ceil((float)wheight / resolution);
        }

        void setup(){
            int length = width * height;

            this.weights = new int[length];
            this.searches = new short[length];
            this.completeWeights = new int[length];
            this.frontier.ensureCapacity((length) / 4);
            this.initialized = true;
        }

        public int getCost(int[] tiles, int pos){
            return cost.getCost(team.id, tiles[pos]);
        }

        public boolean hasTargets(){
            return targets.size > 0;
        }

        /** @return the next tile to travel to for this flowfield. Main thread only. */
        public @Nullable Tile getNextTile(Tile from, boolean diagonals){
            return pathfinderCustom.getTargetTile(from, this, diagonals);
        }

        /** @return the next tile to travel to for this flowfield. Main thread only. */
        public @Nullable Tile getNextTile(Tile from){
            return pathfinderCustom.getTargetTile(from, this);
        }

        /** @return the next tile to travel to for this flowfield. Main thread only. */
        public @Nullable Tile getNextTile(Tile from, int unitAvoidanceId){
            return pathfinderCustom.getTargetTile(from, this, true, unitAvoidanceId);
        }

        public boolean hasCompleteWeights(){
            return hasComplete && completeWeights != null;
        }

        public void updateTargetPositions(){
            targets.clear();
            getPositions(targets);
        }

        /** @return whether this flow field should be refreshed after the current block update */
        public boolean needsRefresh(){
            return refreshRate == 0;
        }

        protected boolean passable(int pos){
            int amount = cost.getCost(team.id, pathfinderCustom.tiles[pos]);
            //edge case: naval reports costs of 6000+ for non-liquids, even though they are not technically passable
            return amount != impassable && !(cost == costTypes.get(costNaval) && amount >= 6000);
        }

        /** Gets targets to pathfind towards. This must run on the main thread. */
        protected abstract void getPositions(IntSeq out);
    }

    public static class PositionTarget extends Flowfield {
        public final Position position;

        public PositionTarget(Position position) {
            this.position = position;
            this.refreshRate = 900;
        }

        public void getPositions(IntSeq out) {
            out.add(Vars.world.packArray(World.toTile(this.position.getX()), World.toTile(this.position.getY())));
        }
    }

    public static class SteamVentField extends Flowfield {

        public SteamVentField() {
            refreshRate = 900; //for Optimization purpose
        }

        protected void getPositions(IntSeq out) {
            for (Tile tile : SteamVents) {
                if (tile.floor().attributes.get(Attribute.steam) <= 0f) continue;
                float steam = 0f;
                for (int dy = -1; dy < 2; dy++) {
                    for (int dx = -1; dx < 2; dx++) {
                        Tile vents = Vars.world.tile(tile.x + dx, tile.y + dy);
                        if (vents == null || vents.build instanceof CausticHeart.HeartBuilding || vents.floor().attributes.get(Attribute.steam) <= 0f)
                            continue;
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
        //public Item OreTarget;

        public OresField() {}

        protected void getPositions(IntSeq out) {
            for (Tile tile : Ores) {
                if (RVars.CordCanDrill.contains(tile.wallDrop())) out.add(tile.array());
            }
        }
    }

    public static class EnemyCoreField extends Flowfield {
        public EnemyCoreField() {
        }

        protected void getPositions(IntSeq out) {
            Iterator var2 = Vars.indexer.getEnemy(this.team, BlockFlag.core).iterator();

            while(var2.hasNext()) {
                Building other = (Building)var2.next();
                out.add(other.tile.array());
            }

            if (Vars.state.rules.waves && this.team == Vars.state.rules.defaultTeam) {
                var2 = Vars.spawner.getSpawns().iterator();

                while(var2.hasNext()) {
                    Tile other = (Tile)var2.next();
                    out.add(other.array());
                }
            }

        }
    }
}
