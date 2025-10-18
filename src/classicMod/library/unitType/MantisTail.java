package classicMod.library.unitType;


import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.gen.Unit;

public class MantisTail implements Cloneable{
    public float layerOffset;
    public float tailRotationSpeed;
    public String spriteName;
    public TextureRegion TailBegin, TailMiddle, TailEnd;
    public TextureRegion TailBody, TailBodyEnd;
    public TextureRegion TailBodyOutline, TailBodyEndOutline;
    public Func<MantisTail, MantisMountTail> mountType = MantisMountTail::new;

    public float shadowElevation = -1f;
    public float shadowElevationScl = 1f;

    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;

    public MantisTail(String name){
        spriteName = name;
    }

    public void load() {
        TailBegin = Core.atlas.find(spriteName + "-0");
        TailMiddle = Core.atlas.find(spriteName + "-1");
        TailEnd = Core.atlas.find(spriteName + "-2");

        TailBody = Core.atlas.find(spriteName + "-mid");
        TailBodyEnd = Core.atlas.find(spriteName + "-end");

        TailBodyOutline = Core.atlas.find(spriteName + "-mid-outline");
        TailBodyEndOutline = Core.atlas.find(spriteName + "-end-outline");
    }

    public void update(Unit unit, MantisMountTail mountTail){
        mountTail.timer += Time.delta / 20f;
        mountTail.rot = Mathf.slerpDelta(mountTail.rot, unit.rotation, 0.35f + tailRotationSpeed);
        mountTail.rotEnd = Mathf.slerpDelta(mountTail.rotEnd, unit.rotation, 0.15f + tailRotationSpeed);

        mountTail.lastRot = (unit.rotation >= 180f) ? mountTail.rot - 360f : mountTail.rot;
        mountTail.lastRotEnd = (unit.rotation >= 180f) ? mountTail.rotEnd - 360f : mountTail.rotEnd;
    }

    @Override
    public MantisTail clone() {
        try {
            MantisTail clone = (MantisTail) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class MantisMountTail {
        /** Float variable associated with this mount */
        public MantisTail tail;
        public float timer, lastRot, lastRotEnd, rot, rotEnd;

        public MantisMountTail(MantisTail tail) {
            this.tail = tail;
        }
    }
}
