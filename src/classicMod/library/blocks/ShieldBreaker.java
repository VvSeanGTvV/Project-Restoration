package classicMod.library.blocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class ShieldBreaker extends Block{
    public Block[] toDestroy = {};
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion, selfKillEffect = Fx.massiveExplosion;

    public ShieldBreaker(String name){
        super(name);
        teamRegion = Core.atlas.find(name + "-team");

        solid = update = true;
        rebuildable = false;
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
        public void updateTile(){
            if(Mathf.equal(efficiency, 1f)){
                if(toDestroy != null){
                    effect.at(this);
                    for(var other : Vars.state.teams.active){
                        if(team != other.team){
                            for(var block : toDestroy){
                                other.getBuildings(block).copy().each(b -> {
                                    breakEffect.at(b);
                                    b.kill();
                                });
                            }
                        }
                    }
                    selfKillEffect.at(this);
                    kill();
                }
            }
        }

        @Override
        public void draw() {
            super.draw();
            teamRegion = Core.atlas.find(name + "-team");
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());
            Draw.z(Layer.blockOver);
            Draw.color(team.color);
            Draw.rect(teamRegion, tile.drawx(), tile.drawy());
            Draw.reset();
        }
    }
}
