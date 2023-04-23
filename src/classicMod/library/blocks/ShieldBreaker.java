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
            if(Mathf.equal(efficiency, 1f)){
                effect.at(this);
                /*for(var other : Vars.state.teams.active){
                    if(team != other.team){
                        for(var block : toDestroy){
                            other.getBuildings(block).copy().each(b -> {
                                breakEffect.at(b);
                                b.kill();
                            });
                        }
                    }
                    selfKillEffect.at(this);
                }*/
                int i = 1;
                for(var other : Vars.state.teams.active) {
                    if (team != other.team) {
                        for (Building b : other.getBuildings(toDestroy[i])) {
                            breakEffect.at(b);
                            b.kill();
                        }
                        if(i < toDestroy.length) i++;
                    }
                }
                //selfKillEffect.at(this);
                //kill();
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
