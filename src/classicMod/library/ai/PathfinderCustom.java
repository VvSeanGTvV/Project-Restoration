//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package classicMod.library.ai;

import arc.*;
import arc.func.Prov;
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

import java.util.Iterator;

import static classicMod.library.ai.PathfinderExtended.*;
import static mindustry.Vars.state;

public class PathfinderCustom implements Runnable {
    private static final long maxUpdate = Time.millisToNanos(8L);
    private static final int updateFPS = 60;
    private static final int updateInterval = 16;
    static int wwidth;
    static int wheight;
    static final int impassable = -1;
    public static final int fieldCore = 0, fieldVent = 1, fieldOres = 2;
    public static final Seq<Prov<Flowfield>> fieldTypes = Seq.with(new Prov[]{EnemyCoreField::new, SteamVentField::new, OresField::new});
    public static final int costGround = 0;
    public static final int costLegs = 1;
    public static final int costNaval = 2;
    public static final Seq<PathCost> costTypes = Seq.with(new PathCost[]{(team, tile) -> {
        return !PathTile.allDeep(tile) && ((PathTile.team(tile) != team || PathTile.teamPassable(tile)) && PathTile.team(tile) != 0 || !PathTile.solid(tile)) ? 1 + PathTile.health(tile) * 5 + (PathTile.nearSolid(tile) ? 2 : 0) + (PathTile.nearLiquid(tile) ? 6 : 0) + (PathTile.deep(tile) ? 6000 : 0) + (PathTile.damages(tile) ? 30 : 0) : -1;
    }, (team, tile) -> {
        return PathTile.legSolid(tile) ? -1 : 1 + (PathTile.deep(tile) ? 6000 : 0) + (PathTile.solid(tile) ? 5 : 0);
    }, (team, tile) -> {
        return (!PathTile.liquid(tile) ? 6000 : 1) + PathTile.health(tile) * 5 + (!PathTile.nearGround(tile) && !PathTile.nearSolid(tile) ? 0 : 14) + (PathTile.deep(tile) ? 0 : 1) + (PathTile.damages(tile) ? 35 : 0);
    }});
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

            if (Vars.state.rules.waveTeam.needsFlowField() && !Vars.net.client()) {
                this.preloadPath(this.getField(Vars.state.rules.waveTeam, 0, 0));
                Log.debug("Preloading ground enemy flowfield.");
                if (Vars.spawner.getSpawns().contains((t) -> {
                    return t.floor().isLiquid;
                })) {
                    this.preloadPath(this.getField(Vars.state.rules.waveTeam, 2, 0));
                    Log.debug("Preloading naval enemy flowfield.");
                }
            }

            this.start();
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
            this.thread = new Thread(this, "Pathfinder");
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
            Tile current = null;
            int tl = 0;
            Point2[] var8 = Geometry.d4;
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Point2 point = var8[var10];
                int dx = tile.x + point.x;
                int dy = tile.y + point.y;
                Tile other = Vars.world.tile(dx, dy);
                if (other != null) {
                    int packed = Vars.world.packArray(dx, dy);
                    if (values[packed] < value && (current == null || values[packed] < tl) && path.passable(packed) && (point.x == 0 || point.y == 0 || path.passable(Vars.world.packArray(tile.x + point.x, tile.y)) && path.passable(Vars.world.packArray(tile.x, tile.y + point.y)))) {
                        current = other;
                        tl = values[packed];
                    }
                }
            }

            if (current != null && tl != -1 && (path.cost != ((PathCost[])costTypes.items)[0] || !current.dangerous() || tile.dangerous())) {
                return current;
            } else {
                return tile;
            }
        }
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

    private void updateTargets(Flowfield path) {
        ++path.search;
        synchronized(path.targets) {
            for(int i = 0; i < path.targets.size; ++i) {
                int pos = path.targets.get(i);
                path.weights[pos] = 0;
                path.searches[pos] = path.search;
                path.frontier.addFirst(pos);
            }

        }
    }

    private void preloadPath(Flowfield path) {
        path.updateTargetPositions();
        this.registerPath(path);
        this.updateFrontier(path, -1L);
    }

    private void registerPath(Flowfield path) {
        path.lastUpdateTime = Time.millis();
        path.setup(this.tiles.length);
        this.threadList.add(path);
        Core.app.post(() -> {
            this.mainList.add(path);
        });

        int i;
        for(i = 0; i < this.tiles.length; ++i) {
            path.weights[i] = -1;
        }

        for(i = 0; i < path.targets.size; ++i) {
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

    public abstract static class Flowfield {
        protected int refreshRate;
        protected Team team;
        protected PathCost cost;
        protected volatile boolean hasComplete;
        protected boolean dirty;
        public int[] weights;
        public int[] searches;
        public int[] completeWeights;
        IntQueue frontier;
        final IntSeq targets;
        int search;
        long lastUpdateTime;
        boolean initialized;

        public Flowfield() {
            this.team = Team.derelict;
            this.cost = (PathCost)PathfinderCustom.costTypes.get(0);
            this.dirty = false;
            this.frontier = new IntQueue();
            this.targets = new IntSeq();
            this.search = 1;
        }

        void setup(int length) {
            this.weights = new int[length];
            this.searches = new int[length];
            this.completeWeights = new int[length];
            this.frontier.ensureCapacity(length / 4);
            this.initialized = true;
        }

        public boolean hasCompleteWeights() {
            return this.hasComplete && this.completeWeights != null;
        }

        public void updateTargetPositions() {
            this.targets.clear();
            this.getPositions(this.targets);
        }

        protected boolean passable(int pos) {
            int amount = this.cost.getCost(this.team.id, RVars.pathfinderCustom.tiles[pos]);
            return amount != -1 && (this.cost != PathfinderCustom.costTypes.get(2) || amount < 6000);
        }

        protected abstract void getPositions(IntSeq var1);
    }

    public interface PathCost {
        int getCost(int var1, int var2);
    }

    class PathTileStruct {
        int health;
        int team;
        boolean solid;
        boolean liquid;
        boolean legSolid;
        boolean nearLiquid;
        boolean nearGround;
        boolean nearSolid;
        boolean nearLegSolid;
        boolean deep;
        boolean damages;
        boolean allDeep;
        boolean teamPassable;

        PathTileStruct() {
        }
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
                out.add(tile.array());
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
