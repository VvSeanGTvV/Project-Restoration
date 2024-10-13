package classicMod.library.unitType;

import arc.Core;
import arc.func.Func;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import classicMod.library.drawCustom.BlendingCustom;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class MantisRayType extends UnitType {


    public TextureRegion eye, eyeGlow;

    public MantisRayType(String name) {
        super(name);

        weapons.add(new MantisTail(){{
            layerOffset = -0.0001f;
        }});
    }

    @Override
    public void drawShadow(Unit unit) {
        drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);
    }

    @Override
    public void load() {
        super.load();
        eye = Core.atlas.find(name + "-eye");
        eyeGlow = Core.atlas.find(name + "-eye-glow");
    }

    @Override
    public void draw(Unit unit) {
        //super.draw(unit);

        Draw.z(Layer.flyingUnit - 1f);
        drawShadow(unit);

        Draw.z(Layer.flyingUnit);
        drawOutline(unit);
        drawWeaponOutlines(unit);
        
        drawBody(unit);
        drawWeapons(unit);
        Draw.rect(eye, unit.x, unit.y, unit.rotation - 90);
    }


    public void drawBody(Unit unit) {
        applyColor(unit);

        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        //drawShadowTexture(unit, region, unit.x, unit.y, unit.rotation - 90);
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasA = Core.atlas.find(name + "-tail-mid").asAtlas();
        if (atlasA != null) {
            String regionName = atlasA.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasA), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }

        var atlasB = Core.atlas.find(name + "-tail-end").asAtlas();
        if (atlasB != null) {
            String regionName = atlasB.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlasB), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }

    public void drawShadowTexture(Unit unit, TextureRegion region, float x1, float y1, float rot1) {
        Draw.z(Layer.flyingUnit - 1f);
        float e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime);
        float x = x1 + shadowTX * e, y = y1 + shadowTY * e;
        Floor floor = world.floorWorld(x, y);

        float dest = floor.canShadow ? 1f : 0f;
        //yes, this updates state in draw()... which isn't a problem, because I don't want it to be obvious anyway
        unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

        Draw.rect(region, x1 + shadowTX * e, y1 + shadowTY * e, rot1);
        Draw.color();
        Draw.z(Layer.flyingUnit);
    }
}
