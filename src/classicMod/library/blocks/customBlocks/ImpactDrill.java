package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.production.Drill;

public class ImpactDrill extends Drill{
    public int outputAmount = 5;

    public ImpactDrill(String name){
        super(name);

        itemCapacity = 20;
        //does not drill in the traditional sense, so this is not even used
        hardnessDrillMultiplier = 0f;
        //generally at center
        drillEffectRnd = 0f;
    }

    public class ImpactDrilllBuild extends Drill.DrillBuild {

        @Override
        public void updateTile(){
            if(dominantItem == null){
                return;
            }

            if(timer(timerDump, dumpTime)){
                dump(items.has(dominantItem) ? dominantItem : null);
            }

            if(items.total() <= itemCapacity - outputAmount && dominantItems > 0 && efficiency > 0){

                float speed = efficiency();

                timeDrilled += speed;

                lastDrillSpeed = dominantItems / drillTime * speed;
                progress += delta() * dominantItems * speed;
            }else{
                lastDrillSpeed = 0f;
                return;
            }

            if(dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity){
                for(int i = 0; i < outputAmount; i++){
                    offload(dominantItem);
                }

                progress %= drillTime;
                drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            drawDefaultCracks();

            //TODO charge

            if(dominantItem != null && drawMineItem){
                Draw.color(dominantItem.color);
                Draw.rect(itemRegion, x, y);
                Draw.color();
            }

            Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed);
            Draw.rect(topRegion, x, y);
        }
    }
}