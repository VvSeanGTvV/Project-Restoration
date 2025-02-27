package classicMod.library.ai;

import arc.Events;
import arc.math.geom.QuadTree;
import arc.struct.*;
import arc.util.Log;
import classicMod.library.blocks.legacyBlocks.LegacyCommandCenter;
import classicMod.library.blocks.neoplasiaBlocks.CausticHeart;
import mindustry.Vars;
import mindustry.ai.Pathfinder;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.world.Tile;
import mindustry.world.meta.Attribute;

import java.util.*;

public class PathfinderExtended extends Pathfinder {
    public static final int fieldVent = 1, fieldCommandCenter = 2, fieldOres = 3;

    public static Seq<Tile> SteamVents = new Seq<>();
    public static Seq<Tile> Ores = new Seq<>();

    public PathfinderExtended(){}

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

    public static class CommandCenterField extends Flowfield {

        public CommandCenterField() {}

        protected void getPositions(IntSeq out) {
            Seq<Building> builds = new Seq<>();
            Seq<Building> valid = new Seq<>();
            QuadTree<Building> buildings = team.data().buildingTree;
            buildings.getObjects(builds);

            for (var build : builds) {
                if (build.team == team && build instanceof LegacyCommandCenter.LegacyCommandCenterBuild){
                    valid.add(build);
                }
            }

            //builds.removeAll(b -> !(b instanceof classicMod.library.blocks.legacyBlocks.LegacyCommandCenter.LegacyCommandCenterBuild) && b.team != team);
            for (var center : valid){
                out.add(center.tile.array());
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

    public static void preloadAddons(){
        Events.on(EventType.WorldLoadEvent.class, (event) -> {
            Iterator tileIterator = Vars.world.tiles.iterator();

            while(tileIterator.hasNext()) {
                Tile tile = (Tile) tileIterator.next();
                var item = tile.drop();
                if (item == null){
                    item = tile.wallDrop(); // TODO wall ore func
                }
                if (item != null){
                    Ores.add(tile);
                    Log.info(tile);
                }
            }
            for (Tile tile : Vars.world.tiles) {

                if (tile.floor() == null) continue;
                if (tile.floor().attributes.get(Attribute.steam) >= 1f) {
                    float steam = 0f;
                    for (int dy = -1; dy < 2; dy++) {
                        for (int dx = -1; dx < 2; dx++) {
                            Tile vents = Vars.world.tile(tile.x + dx, tile.y + dy);
                            if (vents == null || vents.floor().attributes.get(Attribute.steam) <= 0f)
                                continue;
                            steam += vents.floor().attributes.get(Attribute.steam);
                        }
                    }
                    if (steam >= 9f) {
                        SteamVents.add(tile);
                    }
                }
            }
        });
    }

    public static void addonFieldTypes(){
        fieldTypes.add(SteamVentField::new);
        fieldTypes.add(CommandCenterField::new);
        fieldTypes.add(OresField::new);
    }


}
