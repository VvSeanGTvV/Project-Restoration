package classicMod.library.blocks;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
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
    public float droneRange = 50f*1.5f;

    public DroneCenterNew(String name){
        super(name);

        update = solid = true;
        configurable = true;
    }

    @Override
    public void init(){
        super.init();

        //droneType.controller = u -> new EffectDroneAI();
    }

    /*public static void unitTetherBlockSpawned(Tile tile, int id){
        if(tile == null || !(tile.build instanceof UnitTetherBlock build)) return;
        build.spawned(id);
    }*/

    public class DroneCenterNewBuild extends Building implements UnitTetherBlock {
        protected IntSeq readUnits = new IntSeq();
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

            droneWarmup = Mathf.lerpDelta(droneWarmup, efficiency, 0.1f);
            totalDroneProgress += droneWarmup * Time.delta;

            if(unit == null && Units.canCreate(team, droneType)) {
                droneProgress += edelta() / droneConstructTime;

                //TODO better effects?
                if (droneProgress >= 1f) {
                    var unit = droneType.create(team);
                    if (unit instanceof BuildingTetherc bt) {
                        bt.building(this);
                    }
                    unit.set(x, y);
                    unit.rotation = 90f;
                    unit.add();

                    //Fx.spawn.at(unit);
                    //units.add(unit);
                    Call.unitTetherBlockSpawned(tile, unit.id);
                    spawned(unit.id);
                    droneProgress = 0f;
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
                Drawf.square(target.x, target.y, target.hitSize * 0.8f);
            }
        }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            //TODO draw more stuff

            if(droneWarmup > 0){
                Draw.draw(Layer.blockOver + 0.2f, () -> {
                    Drawf.construct(this, droneType.fullIcon, Pal.accent, 0f, droneProgress, droneWarmup, totalDroneProgress, 14f);
                });
            }
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

    /*public class EffectDroneAI extends AIController {
        protected DroneCenterNewBuild build;
        @Override
        public void updateMovement(){
            //if(!(unit instanceof BuildingTetherc tether)) return;
            //if(!(tether.building() instanceof DroneCenterNewBuild build)) return;
            //if(build.target == null) unit.remove(); //TODO fix the ai because it is ded :I
            if(build.target != null) {
                target = build.target;
                if(unit.within(target, droneRange + build.target.hitSize)){
                    build.target.apply(status, statusDuration);
                }else{
                    moveTo(build.target.hitSize / 1.8f + droneRange - 10f);
                }
            }

            //TODO what angle?

            unit.lookAt(target);
            //unit.angleTo(target);
            //unit.moveAt(TarVector, build.target.hitSize / 1.8f + droneRange - 10f);

            //TODO low power? status effects may not be the best way to do this...
            /*if(unit.within(target, droneRange + build.target.hitSize)){
                build.target.apply(status, statusDuration);
            }
        }

        protected void moveTo(float circleLength){
            if(target == null) return;

            vec.set(target).sub(unit);

            float length = circleLength <= 0.001f ? 1f : Mathf.clamp((unit.dst(target) - circleLength) / 100f, -1f, 1f);

            vec.setLength(unit.type().speed * Time.delta * length);
            if(length < -0.5f){
                vec.rotate(180f);
            }else if(length < 0){
                vec.setZero();
            }

            unit.moveAt(vec);
        }

        protected void targetClosest(){
            Teamc newTarget = Units.closestTarget(unit.team(), unit.x(), unit.y(), Math.max(unit.range(), unit.type().range), u -> (unit.type().targetAir && u.isFlying()) || (unit.type().targetGround && !u.isFlying()));
            if(newTarget != null){
                target = newTarget;
            }
        }
    }*/
}
