package classicMod.library.blocks;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;

public class ShieldBreaker extends Block{
    public TextureRegion notify;
    public Block[] toDestroy = {};
    public Effect effect = Fx.shockwave, breakEffect = Fx.reactorExplosion, selfKillEffect = Fx.massiveExplosion;

    public ShieldBreaker(String name){
        super(name);

        solid = update = true;
        rebuildable = false;
    }

    @Override
    public boolean canBreak(Tile tile){
        return Vars.state.isEditor();
    }

    public class ShieldBreakerBuild extends Building{
        public boolean NoBlock;
        public Class<? extends Block> BlockClassIndication;
        @Override
        public void updateTile(){
            if(toDestroy != null){
                for(var other : Vars.state.teams.active){
                    if(team != other.team){
                        for(var blockC : toDestroy){
                            other.getBuildings(block).copy().each(b -> {
                                BlockClassIndication = b.block.getClass();
                                if (BlockClassIndication == blockC.getClass()) {
                                    NoBlock = false;
                                }else {
                                    NoBlock = true;
                                }
                            });
                        }
                    }else{
                        NoBlock = true;
                    }
                }
            }else{
                NoBlock = true;
            }
            if(Mathf.equal(efficiency, 1f)){
                if(toDestroy != null){
                    effect.at(this);
                    for(var other : Vars.state.teams.active){
                        if(team != other.team && !NoBlock){
                            for(var blockC : toDestroy){
                                other.getBuildings(blockC).copy().each(b -> {
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
            notify = Core.atlas.find(name + "-notify");
            Draw.z(Layer.block);
            Draw.rect(region, tile.drawx(), tile.drawy());
            Draw.z(Layer.blockOver);
            if(NoBlock){
                Draw.color(new Color(221,146/2,126/2,255));
            }else{
                Draw.color(new Color(146/2,221,126/2,255));
            }
            Draw.rect(notify, tile.drawx(), tile.drawy());
            Draw.reset();
        }
    }
}
