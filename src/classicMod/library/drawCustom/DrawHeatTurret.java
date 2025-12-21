package classicMod.library.drawCustom;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Rand;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Tmp;
import classicMod.library.blocks.customBlocks.HeatedItemTurret;
import mindustry.entities.part.DrawPart;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawBlock;

public class DrawHeatTurret extends DrawBlock {
    protected static final Rand rand = new Rand();

    public Seq<DrawPart> parts = new Seq<>();
    /** Prefix to use when loading base region. */
    public String basePrefix = "";
    /** Overrides the liquid to draw in the liquid region. */
    public @Nullable Liquid liquidDraw;
    public float turretLayer = Layer.turret, shadowLayer = Layer.turret - 0.5f, heatLayer = Layer.turretHeat;
    public TextureRegion base, liquid, top, heat, preview, outline;

    public DrawHeatTurret(String basePrefix){
        this.basePrefix = basePrefix;
    }

    public DrawHeatTurret(){
    }

    @Override
    public void getRegionsToOutline(Block block, Seq<TextureRegion> out){
        for(var part : parts){
            part.getOutlines(out);
        }

        if(block.region.found() && !(block.outlinedIcon > 0 && block.outlinedIcon < block.getGeneratedIcons().length && block.getGeneratedIcons()[block.outlinedIcon].equals(block.region))){
            out.add(block.region);
        }

        block.resetGeneratedIcons();
    }

    @Override
    public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list){
        Draw.rect(base, plan.drawx(), plan.drawy());
        Draw.rect(preview, plan.drawx(), plan.drawy(), block.rotate ? plan.rotation * 90f - 90f : 0f);
        if(top.found()){
            Draw.rect(top, plan.drawx(), plan.drawy(), block.rotate ? plan.rotation * 90f - 90f : 0f);
        }
    }

    @Override
    public void draw(Building build){
        HeatedItemTurret turret = (HeatedItemTurret)build.block;
        HeatedItemTurret.HeatedItemTurretBuild tb = (HeatedItemTurret.HeatedItemTurretBuild)build;

        Draw.rect(base, build.x, build.y);
        Draw.color();

        Draw.z(shadowLayer);

        Drawf.shadow(preview, build.x + tb.recoilOffset.x - turret.elevation, build.y + tb.recoilOffset.y - turret.elevation, tb.drawrot());

        Draw.z(turretLayer);

        drawTurret(turret, tb);
        drawHeat(turret, tb);
        drawBarrelHeat(turret, tb);

        if(parts.size > 0){
            if(outline.found()){
                //draw outline under everything when parts are involved
                Draw.z(turretLayer - 0.01f);
                Draw.rect(outline, build.x + tb.recoilOffset.x, build.y + tb.recoilOffset.y, tb.drawrot());
                Draw.z(turretLayer);
            }

            float progress = tb.progress();

            //TODO no smooth reload
            var params = DrawPart.params.set(build.warmup(), 1f - progress, 1f - progress, tb.heat, tb.curRecoil, tb.charge, tb.x + tb.recoilOffset.x, tb.y + tb.recoilOffset.y, tb.rotation);

            for(var part : parts){
                params.setRecoil(part.recoilIndex >= 0 && tb.curRecoils != null ? tb.curRecoils[part.recoilIndex] : tb.curRecoil);
                part.draw(params);
            }
        }
    }

    public void drawTurret(Turret block, Turret.TurretBuild build){
        if(block.region.found()){
            Draw.rect(block.region, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }

        if(liquid.found()){
            Liquid toDraw = liquidDraw == null ? build.liquids.current() : liquidDraw;
            Drawf.liquid(liquid, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.liquids.get(toDraw) / block.liquidCapacity, toDraw.color.write(Tmp.c1).a(1f), build.drawrot());
        }

        if(top.found()){
            Draw.rect(top, build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot());
        }
    }

    public void drawHeat(Turret block, Turret.TurretBuild build){
        if(build.heat <= 0.00001f || !heat.found()) return;

        Drawf.additive(heat, block.heatColor.write(Tmp.c1).a(build.heat), build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), heatLayer);
    }

    public void drawBarrelHeat(HeatedItemTurret block, HeatedItemTurret.HeatedItemTurretBuild build){
        if(build.warmth <= 0.00001f || !heat.found()) return;

        Drawf.additive(heat, block.heatColor.write(Tmp.c1).a(build.heatFrac()), build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), heatLayer);
        Drawf.additive(heat, block.heatColor.write(Tmp.c1).a(build.heatFrac()), build.x + build.recoilOffset.x, build.y + build.recoilOffset.y, build.drawrot(), heatLayer);
    }

    /** Load any relevant texture regions. */
    @Override
    public void load(Block block){
        if(!(block instanceof Turret)) throw new ClassCastException("This drawer can only be used on turrets.");

        preview = Core.atlas.find(block.name + "-preview", block.region);
        outline = Core.atlas.find(block.name + "-outline");
        liquid = Core.atlas.find(block.name + "-liquid");
        top = Core.atlas.find(block.name + "-top");
        heat = Core.atlas.find(block.name + "-heat");
        base = Core.atlas.find(block.name + "-base");

        for(var part : parts){
            part.turretShading = true;
            part.load(block.name);
        }

        //TODO test this for mods, e.g. exotic
        if(!base.found() && block.minfo.mod != null) base = Core.atlas.find(block.minfo.mod.name + "-" + basePrefix + "block-" + block.size);
        if(!base.found()) base = Core.atlas.find(basePrefix + "block-" + block.size);
    }

    /** @return the generated icons to be used for this block. */
    @Override
    public TextureRegion[] icons(Block block){
        return top.found() ? new TextureRegion[]{base, preview, top} : new TextureRegion[]{base, preview};
    }
}
