package classicMod.library.unitType;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.UnitType;

public class MantisRayType extends UnitType {

    public TextureRegion TailBegin;
    public TextureRegion TailMiddle;
    public TextureRegion TailEnd;
    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;
    private float timer;

    public MantisRayType(String name) {
        super(name);
    }


    @Override
    public void draw(Unit unit) {
        this.timer += Time.delta / 20f;
        TailBegin = Core.atlas.find(name + "-tail-0");
        TailMiddle = Core.atlas.find(name + "-tail-1");
        TailEnd = Core.atlas.find(name + "-tail-2");

        super.draw(unit);

        var sine0 = Mathf.sin(this.timer) * 10f;
        var sine1 = sine0 + 5f;
        Tmp.v1.trns(unit.rotation - 90, TailOffsetBegin.x, TailOffsetBegin.y);
        Draw.rect(TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation - 90);

        Tmp.v1.trns(unit.rotation + sine0 + AngleOffset[0] - 90, offsetX, (TailBegin.height / 8f) + 6.6f + padding);
        Draw.rect(TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine0 + AngleOffset[0] - 90);

        Tmp.v1.trns(unit.rotation + sine1 + AngleOffset[1] - 90, offsetX, (TailMiddle.height / 4f) + 0.15f + padding);
        Draw.rect(TailEnd, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation + sine1 + AngleOffset[1] - 90);

        //Draw.rect(TailEnd, unit.x + TailOffset[2].x, unit.y + TailOffset[2].y);
    }

    /*@Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        var atlasB = Core.atlas.find(name + "-tail-2").asAtlas();
        if (atlasB != null) {
            String regionName = atlasB.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(Core.atlas.find(name)), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }*/
}
