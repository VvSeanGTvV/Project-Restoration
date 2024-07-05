package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.util.Scaling;
import classicMod.content.ExtendedStat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
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

    public ShieldBreaker(String name) {
        super(name);

        solid = update = true;
        rebuildable = false;
    }

    @Override
    public void load() {
        super.load();
        teamRegion = Core.atlas.find(name + "-team");
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
                        t.add(blocko.localizedName).color(Pal.remove).left().pad(10f);
                        return;
                    }

                    t.image(blocko.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
                    t.add(blocko.localizedName).left().pad(10f);
                    t.row();
                }
            });
        });
    }

    public class ShieldBreakerBuild extends Building {

        public boolean isValidBuild(Building building) {
            for (var target : toDestroy) {
                if ((Objects.equals(building.block.name, target.name))) return true;
            }
            return false;
        }

        @Override
        public void updateTile() { //TODO fix this
            if (efficiency >= 1f) {
                effect.at(this);
                Building b = Units.findEnemyTile(team, x, y, Float.MAX_VALUE / 2, building -> (building instanceof BaseShield.BaseShieldBuild && isValidBuild(building)));
                if (b != null) {
                    breakEffect.at(b);
                    b.kill();
                } else {
                    selfKillEffect.at(this);
                    kill();
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());
            Draw.z(Layer.blockOver);
            float op = itemCapacity;
            Draw.color(team.color, Color.red, items.total() / op);
            Draw.rect(teamRegion, tile.drawx(), tile.drawy());
            Draw.reset();
        }
    }
}
