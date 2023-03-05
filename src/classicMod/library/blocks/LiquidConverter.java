package classicMod.library.blocks;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import classicMod.library.blocks.legacyBlocks.LegacyUnitFactory;
import mindustry.Vars;
import mindustry.content.Liquids;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.world.blocks.production.*;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.meta.Stat;

public class LiquidConverter extends GenericCrafter { //TODO fix this old converter
    public float ConvertTime = 10f;
    public Liquid ConvertLiquid = Liquids.water;
    public float ConvertLiquidAmount = 5f;
    ConsumeLiquid cl;
    public LiquidConverter(String name) {
        super(name);
        hasLiquids = true;
    }

    @Override
    public void init() {
        if(!hasLiquids){throw new RuntimeException("LiquidConverters must have: hasLiquids = true");}
        if(cl != null){throw new RuntimeException("Unable to convert Null to consumeLiquid");}
        cl = consumeLiquid(ConvertLiquid, ConvertLiquidAmount);
        super.init();
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.remove(Stat.output);
        stats.add(Stat.output, outputLiquid.liquid, outputLiquid.amount * 60f, true);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (LiquidConverterBuild e) -> new Bar("bar.progress", Pal.ammo, e::fractionTime));
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{Core.atlas.find(name), Core.atlas.find(name + "-bottom")};
    }

    public class LiquidConverterBuild extends GenericCrafterBuild{
        public float speedScl;
        public float fractionTime(){ return progress / ConvertTime; }
        @Override
        public void drawLight(){
            if(hasLiquids && drawLiquidLight && outputLiquid.liquid.lightColor.a > 0.001f){
                drawLiquidLight(outputLiquid.liquid, liquids.get(outputLiquid.liquid));
            }
        }
        @Override
        public void draw() {
            super.draw();
            TextureRegion region = Core.atlas.find(name);
            TextureRegion bottomRegion = Core.atlas.find(name + "-bottom");
            TextureRegion liquidRegion = Core.atlas.find(name + "-liquid");

            Draw.rect(region, x, y);
            Draw.rect(bottomRegion, x, y);

        }

        @Override
        public void updateTile() {
            float use = Math.min(cl.amount * edelta(), liquidCapacity - liquids.get(outputLiquid.liquid));
            float ratio = outputLiquid.amount / cl.amount;

            if(efficiency > 0){
                if(Mathf.chanceDelta(updateEffectChance)){
                    updateEffect.at(getX() + Mathf.range(size * 4f), getY() + Mathf.range(size * 4));
                }
                progress += use / cl.amount;
                warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);
            }else {
                warmup = Mathf.lerp(warmup, consumesLiquid(ConvertLiquid) ? 1f : 0f, 0.02f);
            }
            if(progress >= ConvertTime) {
                progress %= 1f;
                liquids.add(outputLiquid.liquid, use * ratio);

                consume();
            }
            if(outputLiquid != null){
                dumpLiquid(outputLiquid.liquid);
            }
        }
    }
}
