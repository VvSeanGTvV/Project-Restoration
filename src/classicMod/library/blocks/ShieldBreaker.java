package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.*;
import classicMod.content.ExtendedStat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.defense.BaseShield;

import java.util.Objects;

public class ShieldBreaker extends Block {
    /**
     * Specific block that can destroyed by this block
     **/
    public Block[] toDestroy = {};

    /**
     * Special Effect on: {@link #breakEffect}, {@link #selfKillEffect}
     **/
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion, selfKillEffect = Fx.massiveExplosion;
    public TextureRegion top;

    public ShieldBreaker(String name) {
        super(name);

        solid = update = true;
        rebuildable = false;
    }

    @Override
    public void load() {
        super.load();
        teamRegion = Core.atlas.find(name + "-team");
        top = Core.atlas.find(name + "-top");
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
        stats.add(ExtendedStat.canBreak, table -> {
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

    public class ShieldBreakerBuild extends Building {

        Seq<Building> disabled = new Seq<>();
        public float cooldownTime = 0;
        public boolean cooling;

        public boolean isValidBuild(Building building) {
            for (var target : toDestroy) {
                if ((building.block == target)) return true;
            }
            return false;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return super.acceptItem(source, item) && cooldownTime <= 0;
        }

        @Override
        public void updateTile() {
            Log.info(cooldownTime);
            Log.info(cooling);
            if (cooldownTime <= 240 && cooling && disabled != null) {
                for (var b : disabled){
                    Log.info(b);
                    if (b != null) b.enabled = true;
                    disabled.remove(b);
                }
            }

            if (efficiency >= 1f) {
                effect.at(this);
                Building b = Units.findEnemyTile(team, x, y, Float.MAX_VALUE / 2, building -> (building instanceof BaseShield.BaseShieldBuild && isValidBuild(building)));
                if (b != null && !disabled.contains(b)) {
                    breakEffect.at(b);
                    b.enabled = false;
                    disabled.add(b);
                } else {
                    cooldownTime = 480;
                    consume();

                    damage(30f);
                    //selfKillEffect.at(this);
                    //kill();
                }
            }
            if (cooldownTime > 0) {
                cooling = true;
                cooldownTime -= (1 * this.delta());
            } else {
                cooling = false;
            }
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
            Draw.color(Color.valueOf("92dd7e"), Color.red,  (items.total() / itemCap));//items.total() / itemCap);
            Draw.rect(top, tile.drawx(), tile.drawy());

            Draw.reset();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(cooldownTime);
            write.i(disabled.size);
            write.bool(cooling);
            for (var b : disabled){
                write.f(b.x);
                write.f(b.y);
            }
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            disabled.clear();
            cooldownTime = read.f();
            float dSize = read.i();
            cooling = read.bool();
            for (int i = 0; i < dSize; i++){
                Building b = Vars.world.buildWorld(read.f(), read.f());
                Log.info(b);
                if (b instanceof BaseShield.BaseShieldBuild shieldBuild){
                    disabled.add(shieldBuild);
                }
            }
        }
    }
}
