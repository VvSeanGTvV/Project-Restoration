package classicMod.library.unitType;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class MantisRayType extends UnitType {

    public TextureRegion TailBegin;
    public TextureRegion TailMiddle;
    public TextureRegion TailEnd;
    public Vec2 TailOffsetBegin = new Vec2(0f, -10f);
    public float[] AngleOffset = new float[]{10f, 20f};
    public float padding = 10f;
    public float offsetX = 0f;

    public MantisRayType(String name) {
        super(name);
    }


    @Override
    public void draw(Unit unit) {
        TailBegin = Core.atlas.find(name + "-tail-0");
        TailMiddle = Core.atlas.find(name + "-tail-1");

        Draw.rect(TailBegin, unit.x + TailOffsetBegin.x, unit.y + TailOffsetBegin.y);
        float padY = (TailBegin.height / 2f);
        Tmp.v1.trns(unit.rotation + AngleOffset[0], unit.x + offsetX, unit.y + padY + padding);
        Draw.rect(TailMiddle, unit.x + Tmp.v1.x, unit.y + Tmp.v1.y);
        //Draw.rect(TailEnd, unit.x + TailOffset[2].x, unit.y + TailOffset[2].y);
    }
}
