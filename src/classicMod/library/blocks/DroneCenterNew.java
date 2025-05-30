package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.ExtendedStat;
import classicMod.library.ai.EffectDroneAI;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.UnitTetherBlock;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class DroneCenterNew extends Block {
    /**
     * Unit Type
     **/
    public UnitType droneType;
    /**
     * Status effect for Effect Drone to give the exact effect to the units
     **/
    public StatusEffect status = StatusEffects.overclock;
    /**
     * Contrustion time per Units
     **/
    public float droneConstructTime = 60f * 3f;
    /**
     * Duration of the currently selected status effect
     **/
    public float statusDuration = 60f * 2f;
    /**
     * Effect Drone's maximum range
     **/
    public float droneRange = 52f * 2f;

    public TextureRegion topRegion;
    public TextureRegion topRegion1;

    public DroneCenterNew(String name) {
        super(name);

        update = solid = true;
        configurable = true;

        teamRegion = Core.atlas.find(name + "-team");
        topRegion = Core.atlas.find(name + "-top");
        topRegion1 = Core.atlas.find(name + "-top1");
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        if (!Units.canCreate(Vars.player.team(), droneType)) {
            drawPlaceText(Core.bundle.get("bar.cargounitcap"), x, y, valid);
        }

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, droneRange, player.team().color);
    }

    @Override
    public void setStats() {
        super.setStats();

        stats.add(Stat.range, droneRange / tilesize, StatUnit.blocks);
        //stats.add(Stat.maxUnits, unitsMax);
        //stats.add(ExtendedStat.StatusDuration, statusDuration / 60f, StatUnit.seconds);

        stats.add(ExtendedStat.StatusOutput, table -> {
            table.table(Styles.none, t -> {
                t.image(status.uiIcon).size(40).pad(2.5f).left().scaling(Scaling.fit);

                t.table(info -> {
                    info.add(status.localizedName);
                    info.row();
                    if (status.permanent) {
                        info.add("Permanent");
                        return;
                    }
                    info.add(Strings.autoFixed(statusDuration / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
                }).left().pad(10f);
            });
        });
    }

    @Override
    public void init() {
        super.init();

        teamRegion = Core.atlas.find(name + "-team");
        topRegion = Core.atlas.find(name + "-top");
        topRegion1 = Core.atlas.find(name + "-top1");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-top")};
    }

    public class DroneCenterNewBuild extends Building implements UnitTetherBlock {
        public int readUnitId = -1;

        public boolean hadUnit = false;
        //public Seq<Unit> units = new Seq<>();
        public @Nullable Unit target;
        public @Nullable Unit unit;
        public float droneProgress, droneWarmup, totalDroneProgress;
        private boolean placeUnit = false;

        @Override
        public void updateTile() {
            if (unit != null && (unit.dead || !unit.isAdded())) {
                unit = null;
            }

            if (readUnitId != -1) {
                unit = Groups.unit.getByID(readUnitId);
                if (unit != null || !Vars.net.client()) {
                    readUnitId = -1;
                }
            }

            //units.removeAll(u -> !u.isAdded() || u.dead);

            droneWarmup = Mathf.lerpDelta(droneWarmup, efficiency, 0.1f);
            totalDroneProgress += droneWarmup * Time.delta;

            if (unit == null && Units.canCreate(team, droneType) && target != null) {
                if (!hadUnit) droneProgress += edelta() / droneConstructTime;
                else droneProgress = 1f;

                //TODO better effects?
                if (droneProgress >= 1f || hadUnit) {
                    hadUnit = true;
                    placeUnit = false;

                    unit = droneType.create(team);
                    if (unit instanceof BuildingTetherc bt) {
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();

                    Call.unitTetherBlockSpawned(tile, unit.id);

                }

                if (target != null && !target.isValid()) {
                    target = null;
                }

                //TODO no autotarget, bad
                   /* if(target == null){
                        target = targetClosest(); //Units.closest(team, x, y, u -> !u.spawnedByCore && u.type != droneType);
                    }*/
            }
            if (target == null) {
                if (unit != null) if (within(unit, 7f)) {
                    placeUnit = true;
                    if (unit.controller() instanceof EffectDroneAI ai) ai.Nullify(true);
                }
            }

            targetClosest();
        }

        @Override
        public boolean shouldConsume() {
            return unit == null;
        }

        public void spawned(int id) {
            Fx.spawn.at(x, y);
            droneProgress = 0f;
            if (Vars.net.client()) {
                readUnitId = id;
            }
        }


        protected void targetClosest() {
            target = Units.closest(team, x, y, droneRange, u -> !u.spawnedByCore && u.type != droneType);
        }

        @Override
        public void drawConfigure() {
            Drawf.dashCircle(x, y, droneRange, team.color);
            Drawf.square(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));

            if (target != null) {
                Drawf.square(target.x, target.y, target.hitSize * 0.8f, Color.green);
            }
        }

        @Override
        public void draw() {
            teamRegion = Core.atlas.find(name + "-team");
            topRegion = Core.atlas.find(name + "-top");
            topRegion1 = Core.atlas.find(name + "-top1");

            Draw.z(Layer.block);
            Draw.rect(block.region, x, y);

            //TODO draw more stuff

            if (droneWarmup > 0 && !hadUnit && efficiency >= 1f && this.target != null) {
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }

            if (hadUnit && placeUnit && unit == null) {
                Draw.rect(droneType.fullIcon, x, y);
            }

            Draw.z(Layer.blockOver + 0.3f);
            //Draw.rect(topRegion1, x, y);
            Draw.rect(topRegion, x, y);

        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(unit == null ? -1 : unit.id);
            write.bool(hadUnit);
            write.bool(placeUnit);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            //readTarget = read.i();

            readUnitId = read.i();
            hadUnit = read.bool();
            placeUnit = read.bool();
        }
    }
}
