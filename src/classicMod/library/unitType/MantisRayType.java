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
    public float padding = 5f;
    public float offsetX = 0f;

    public MantisRayType(String name) {
        super(name);
    }


    @Override
    public void draw(Unit unit) {
        TailBegin = Core.atlas.find(name + "-tail-0");
        TailMiddle = Core.atlas.find(name + "-tail-1");

        super.draw(unit);

        Tmp.v1.trns(unit.rotation - 90, TailOffsetBegin.x, TailOffsetBegin.y);
        Draw.rect(TailBegin, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation - 90);

        Tmp.v1.trns(unit.rotation + AngleOffset[0] - 90, offsetX, padding);
        Draw.rect(TailMiddle, unit.x - Tmp.v1.x, unit.y - Tmp.v1.y, unit.rotation - 90);

        //Draw.rect(TailEnd, unit.x + TailOffset[2].x, unit.y + TailOffset[2].y);
    }
}
