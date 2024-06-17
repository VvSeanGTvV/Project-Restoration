package classicMod.library.blocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Scaling;
import classicMod.content.ExtendedStat;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.Styles;
import mindustry.world.*;
import mindustry.world.blocks.defense.BaseShield;
import mindustry.world.blocks.defense.ShieldWall;
import mindustry.world.meta.Stat;

import java.util.Objects;

public class ShieldBreaker extends Block{
    /** Specific block that can destroyed by this block **/
    public Block[] toDestroy = {};

    /** Special Effect on: {@link #breakEffect}, {@link #selfKillEffect}**/
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion, selfKillEffect = Fx.massiveExplosion;

    public ShieldBreaker(String name){
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
    public boolean canBreak(Tile tile){
        return true; //Vars.state.isEditor()
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-team")};
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(ExtendedStat.canBreak, table -> {
            table.row();
            table.table(Styles.grayPanel, t -> {
                for (var blocko : toDestroy) {
                    if (Vars.state.rules.isBanned(blocko)) {
                        t.image(Icon.cancel).color(Pal.remove).size(32);
                        return;
                    }

                    t.image(blocko.uiIcon).size(32).pad(2.5f).left().scaling(Scaling.fit);
                    t.add(blocko.localizedName).left().pad(10f);
                }
            });
        });
    }

    public class ShieldBreakerBuild extends Building{

        public boolean isValidBuild(Building building){
            boolean isValid = false;
            for (int i = 0; i < toDestroy.length; i++ ){
                isValid = (Objects.equals(toDestroy[i].name, building.block.name));
                if(isValid)break;
            }
            return isValid;
        }
        @Override
        public void updateTile(){ //TODO fix this
            if(efficiency >= 1f){
                effect.at(this);
                Building b = Units.findEnemyTile(team, x, y, Float.MAX_VALUE/2, building -> (building instanceof BaseShield.BaseShieldBuild && isValidBuild(building)));
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
            Draw.color(team.color, Color.red, items.total()/op);
            Draw.rect(teamRegion, tile.drawx(), tile.drawy());
            Draw.reset();
        }
    }
}
