package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import classicMod.content.ExtendedStat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class DroneCenterNew extends Block {

    /** Maximum Unit that can spawn **/
    public int unitsSpawned = 4;
    public UnitType droneType;
    /** Status effect for Effect Drone to give the exact effect to the units **/
    public StatusEffect status = StatusEffects.overdrive;
    /** Contrustion time per Units **/
    public float droneConstructTime = 60f * 3f;
    /** Duration of the currently selected status effect **/
    public float statusDuration = 60f * 2f;
    /** Effect Drone's maximum range **/
    public float droneRange = 50f*2f;

    public TextureRegion topRegion = Core.atlas.find(name+"-top");

    public DroneCenterNew(String name){
        super(name);

        update = solid = true;
        configurable = true;

        teamRegion = Core.atlas.find(name + "-team");
        topRegion = Core.atlas.find(name + "-top");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        if(!Units.canCreate(Vars.player.team(), droneType)){
            drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
        }
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.range, droneRange / tilesize, StatUnit.blocks);
        stats.add(ExtendedStat.StatusOutput, status.localizedName);
        stats.add(ExtendedStat.StatusDuration, statusDuration / 60f, StatUnit.seconds);
    }

    @Override
    public void init(){
        super.init();

        teamRegion = Core.atlas.find(name + "-team");
        topRegion = Core.atlas.find(name + "-top");
    }

    public class DroneCenterNewBuild extends Building implements UnitTetherBlock {
        public int readUnitId = -1;
        protected int readTarget = -1;

        public Seq<Unit> units = new Seq<>();
        public @Nullable Unit target;
        public @Nullable Unit unit;
        public float droneProgress, droneWarmup, totalDroneProgress;

        @Override
        public void updateTile(){
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !Vars.net.client()){
                    readUnitId = -1;
                }
            }

            units.removeAll(u -> !u.isAdded() || u.dead);

            droneWarmup = Mathf.lerpDelta(droneWarmup, efficiency, 0.1f);
            totalDroneProgress += droneWarmup * Time.delta;

            if(unit == null && Units.canCreate(team, droneType)) {
                droneProgress += edelta() / droneConstructTime;

                //TODO better effects?
                if (droneProgress >= 1f) {
                    if(!Vars.net.client()) {
                        unit = droneType.create(team);
                        if (unit instanceof BuildingTetherc bt) {
                            bt.building(this);
                        }
                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();

                        Call.unitTetherBlockSpawned(tile, unit.id);
                    }
                }

                if (target != null && !target.isValid()) {
                    target = null;
                }

                //TODO no autotarget, bad
               /* if(target == null){
                    target = targetClosest(); //Units.closest(team, x, y, u -> !u.spawnedByCore && u.type != droneType);
                }*/
            }
            targetClosest();
        }

        @Override
        public boolean shouldConsume(){
            return unit == null;
        }

        public void spawned(int id){
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if(Vars.net.client()){
                readUnitId = id;
            }
        }


        protected void targetClosest() {
            target = Units.closest(team, x, y, droneRange, u -> !u.spawnedByCore && u.type != droneType);
        }

        @Override
        public void drawConfigure(){
            Drawf.circles(x, y, droneRange);
            Drawf.square(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));

            if(target != null){
                Drawf.square(target.x, target.y, target.hitSize * 0.8f, Color.green);
            }
        }

        @Override
        public void draw(){
            teamRegion = Core.atlas.find(name + "-team");
            topRegion = Core.atlas.find(name + "-top");

            Draw.rect(block.region, x, y);

            //TODO draw more stuff

            if(droneWarmup > 0){
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }

            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);

            Draw.color(team.color);
            Draw.rect(teamRegion, x, y);

        }

        @Override
        public void write(Writes write){
            super.write(write);

            //write.i(target == null ? -1 : target.id);

            write.i(unit == null ? -1 : unit.id);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            //readTarget = read.i();

            readUnitId = read.i();
        }
    }
}
