package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.ExtendedStat;
import classicMod.library.MathE;
import classicMod.library.blocks.legacyBlocks.LegacyUnitFactory;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.BaseShield;
import mindustry.world.blocks.production.Pump;
import mindustry.world.meta.*;

import java.util.Objects;

import static mindustry.Vars.*;

public class EMP extends Block {
    /**
     * Specific block that can suppressed blocks.
     **/
    public Block[] toDestroy = {};

    /**
     * Special Effect on: {@link #breakEffect}, {@link #effect}
     **/
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion;

    public float range = 480f;

    /**
     * Creates a cool looking lightning with included variable {@link #lightningLength} & {@link #lightningLengthRand} if enabled.
     **/
    public boolean lightningEffectEnabled = false;

    /**
     * used for lightning effects. (can only work if {@link #lightningEffectEnabled} is set to true)
     **/
    public float lightningLength = 5f, lightningLengthRand = 0;

    public float cooldownTime = 480, reEnable = 240;

    public TextureRegion top;

    public EMP(String name) {
        super(name);

        solid = update = true;
        rebuildable = false;
        configurable = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, player.team().color);
    }

    public Building getNearestEMP(Tile tile, Team team, float range){
        return Units.closestBuilding(team, tile.worldx(), tile.worldy(), range, b ->
                b.isValid()
                && b instanceof EMPBuild
                && b != tile.build
        );
    }

    public Building getNearestEMP(Building building, float range){
        return getNearestEMP(building.tile, building.team, range);
    }

    public boolean nearToEMP(Tile tile, Team team){
        return (getNearestEMP(tile, team, range * 2f) != null);
    }
    public boolean nearToEMP(Building building){
        return nearToEMP(building.tile, building.team);
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        boolean tooNear = nearToEMP(tile, team);
        return !tooNear;
    }

    @Override
    public void load() {
        super.load();
        teamRegion = Core.atlas.find(name + "-team");
        top = Core.atlas.find(name + "-top");
    }

    @Override
    public void setBars() {
        addBar("progress", (EMP.EMPBuild e) -> new Bar("bar.loadprogress", Pal.ammo, e::fraction));
        super.setBars();
    }

    @Override
    public boolean canBreak(Tile tile) {
        return true; //Vars.state.isEditor()
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team-" + "sharded")};
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.range, range / Vars.tilesize, StatUnit.blocks);
        stats.add(ExtendedStat.empDuration, (cooldownTime - reEnable) / 60f, StatUnit.seconds);
        stats.add(ExtendedStat.cooldown, cooldownTime / 60f, StatUnit.seconds);

        stats.add(ExtendedStat.canEMP, table -> {
            table.row();
            table.table(Styles.grayPanel, t -> {
                for (var blocko : toDestroy) {
                    if (Vars.state.rules.isBanned(blocko)) {
                        t.image(Icon.cancel).color(Pal.remove).size(40);
                        t.table(info -> {
                            info.add(blocko.localizedName).color(Pal.remove).left();
                            if(blocko.isModded()) {
                                info.row();
                                info.add("Modded").color(Pal.ammo).left();
                            }
                        }).left().pad(10f);
                        return;
                    }

                    t.image(blocko.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    t.table(info -> {
                        info.add(blocko.localizedName).left();
                        if(blocko.isModded()) {
                            info.row();
                            info.add("Modded").color(Pal.ammo).left();
                        }
                    }).left().pad(10f);

                    t.row();
                }
            }).growX();
        });
    }

    public class EMPBuild extends Building {

        Seq<Building> disabled = new Seq<>();
        Seq<Vec2> IDXY = new Seq<>();
        public float cooldownTimer = 0;
        public boolean cooling;

        public float fraction(){ return 1 - (cooldownTimer / cooldownTime); }

        public boolean isValidTarget(@Nullable Building building) {
            if (building == null) return false;
            for (var target : toDestroy) {
                if ((building.block == target)) return true;
            }
            return false;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return super.acceptItem(source, item) && cooldownTimer <= 0;
        }

        @Override
        public void drawConfigure() {
            Drawf.dashCircle(x, y, range, team.color);
            Seq<Building> buildings = new Seq<>();
            Building b = Units.findEnemyTile(team, x, y, range, building -> (building instanceof BaseShield.BaseShieldBuild && isValidTarget(building) && !buildings.contains(building)));
            while (b != null){
                buildings.add(b);
                b = Units.findEnemyTile(team, x, y, range, building -> (building instanceof BaseShield.BaseShieldBuild && isValidTarget(building) && !buildings.contains(building)));
            }
            for (var build : buildings) {
                if (build != null) Drawf.circles(build.x, build.y, build.block.size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Color.red);
            }

            /*if (target != null) {
                Drawf.square(target.x, target.y, target.hitSize * 0.8f, Color.green);
            }*/
        }

        @Override
        public void updateTile() {
            if (!IDXY.isEmpty()) {
                disabled.clear();
                for (var xy : IDXY) {
                    Building b = Vars.world.buildWorld(xy.x, xy.y);
                    Log.info(b);
                    if (b != null && isValidTarget(b)) disabled.add(b);
                }
                IDXY.clear();
            }

            if (cooldownTimer <= reEnable && cooling && disabled != null) {
                for (var b : disabled){
                    if (b != null) b.enabled = true;
                    disabled.remove(b);
                }
            }

            if (efficiency >= 1f) {
                effect.at(this);
                Sounds.spark.at(this);
                if (nearToEMP(this)) {
                    var Building = getNearestEMP(this, range * 2f);
                    if (Building != null) Building.kill();
                    kill();
                }
                Building b = Units.findEnemyTile(team, x, y, range, building -> (building instanceof BaseShield.BaseShieldBuild && isValidTarget(building) && !disabled.contains(building)));
                if (b != null) {
                    Sounds.spark.at(b);
                    breakEffect.at(b);
                    b.enabled = false;

                    Seq<Vec2> data = new Seq<>(
                            new Vec2[]{
                                    new Vec2(x, y),
                                    MathE.interpolate(new Vec2(x, y), new Vec2(b.x, b.y), 1.25f, lightningLength + Mathf.random(lightningLengthRand)),
                                    MathE.interpolate(new Vec2(x, y), new Vec2(b.x, b.y), 2.25f, lightningLength + Mathf.random(lightningLengthRand)),
                                    new Vec2(b.x, b.y)
                            }
                    );
                    if (lightningEffectEnabled) Fx.lightning.at(x, y, b.rotation, Color.valueOf("99d9ea"), data);

                    disabled.add(b);
                } else {
                    cooldownTimer = cooldownTime;
                    consume();

                    //damage(30f);
                    //selfKillEffect.at(this);
                    //kill();
                }
            }
            if (cooldownTimer > 0) {
                cooling = true;
                cooldownTimer -= (1 * this.delta());
            } else {
                cooling = false;
            }
        }

        void enableSuspendedBlock(){
            for (var b : disabled){
                if (b != null) b.enabled = true;
                disabled.remove(b);
            }
        }

        @Override
        public void onRemoved() {
            enableSuspendedBlock();
            super.onRemoved();
        }

        @Override
        public void onDestroyed() {
            enableSuspendedBlock();
            super.onDestroyed();
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());
            Draw.z(Layer.blockOver);

            float itemCap = itemCapacity;

            Draw.color(team.color);
            Draw.rect(teamRegion, tile.drawx(), tile.drawy());
            Draw.color(Color.valueOf("92dd7e"), Color.valueOf("99d9ea"),  (items.total() / itemCap));//items.total() / itemCap);
            Draw.rect(top, tile.drawx(), tile.drawy());

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(cooldownTimer);
            write.i(disabled.size);
            write.bool(cooling);
            for (int i = 0; i < disabled.size; i++){
                var b = disabled.get(i);
                write.f(b.x);
                write.f(b.y);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            IDXY.clear();

            cooldownTimer = read.f();
            float dSize = read.i();
            cooling = read.bool();
            for (int i = 0; i < dSize; i++){
                IDXY.add(new Vec2(read.f(), read.f()));
                /*if (b instanceof BaseShield.BaseShieldBuild shieldBuild){
                    disabled.add(shieldBuild);
                }*/
            }
        }
    }
}
