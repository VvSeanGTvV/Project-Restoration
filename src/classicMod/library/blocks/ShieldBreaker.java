package classicMod.library.blocks;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.BaseShield;
import mindustry.world.blocks.defense.ShieldWall;

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

    public class ShieldBreakerBuild extends Building{
        @Override
        public void updateTile(){ //TODO fix this
            if(efficiency >= 1f){
                effect.at(this);
                Building b = Units.closestBuilding(team, x, y, Float.MAX_VALUE/2, building -> building instanceof BaseShield.BaseShieldBuild);
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
