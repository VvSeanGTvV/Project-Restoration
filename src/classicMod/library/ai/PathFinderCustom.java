package classicMod.library.ai;

import arc.func.Prov;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.ai.Pathfinder;
import mindustry.gen.PathTile;

public class PathFinderCustom extends Pathfinder {

    private static final long maxUpdate = Time.millisToNanos(8);
    private static final int updateFPS = 60;
    private static final int updateInterval = 1000 / updateFPS;

    /** cached world size */
    static int wwidth, wheight;

    static final int impassable = -1;

    public static final int
            fieldCore = 0;

    public static final Seq<Prov<Flowfield>> fieldTypes = Seq.with(
            EnemyCoreField::new
    );

    public static final int
            costGround = 0,
            costLegs = 1,
            costNaval = 2,
            costStomp = 3;

    public static final Seq<PathCost> costTypes = Seq.with(
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
                    (!PathTile.liquid(tile) ? 6000 : 1) +
                            PathTile.health(tile) * 5 +
                            (PathTile.nearGround(tile) || PathTile.nearSolid(tile) ? 14 : 0) +
                            (PathTile.deep(tile) ? 0 : 1) +
                            (PathTile.damages(tile) ? 35 : 0),

            //stompers
            (team, tile) ->
                    ((PathTile.allDeep(tile) || ((PathTile.team(tile) == team && !PathTile.teamPassable(tile)))) ? impassable : 1 +
                            PathTile.health(tile) * 5 +
                            (PathTile.deep(tile) ? 6000 : 0) +
                            (PathTile.nearLiquid(tile) ? 6 : 0))
                    /*(PathTile.allDeep(tile) || ((PathTile.team(tile) == team && !PathTile.teamPassable(tile)) || PathTile.team(tile) == 0) && PathTile.solid(tile)) ? impassable : 1 +
                            PathTile.health(tile) * 5 +
                            (PathTile.nearSolid(tile) ? 2 : 0) +
                            (PathTile.nearLiquid(tile) ? 6 : 0) +
                            (PathTile.deep(tile) ? 6000 : 0) +
                            (PathTile.damages(tile) ? 30 : 0)*/


    );

}
