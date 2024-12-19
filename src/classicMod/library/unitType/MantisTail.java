package classicMod.library.unitType;


import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.*;
import mindustry.type.Weapon;
import mindustry.world.blocks.environment.Floor;

import static classicMod.content.ClassicVars.empty;
import static mindustry.Vars.world;

public class MantisTail implements Cloneable {
    public float layerOffset;
    public float tailRotationSpeed;
    public String spriteName;
    public TextureRegion TailBegin, TailMiddle, TailEnd;
    public TextureRegion TailBody, TailBodyEnd;
    public TextureRegion TailBodyOutline, TailBodyEndOutline;
    public Func<MantisTail, MantisMountTail> mountType;
    public float timer, lastRot, lastRotEnd, rot, rotEnd;

    public float shadowElevation = -1f;
    public float shadowElevationScl = 1f;


    public Vec2 TailOffsetBegin = new Vec2(0f, -7.25f);
    public float[] AngleOffset = new float[]{0f, 0f};
    public float padding = 0f;
    public float offsetX = 0f;

    public MantisTail(String name){
        spriteName = name;
        mountType = MantisMountTail::new;
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

        public MantisMountTail(MantisTail tail) {
            this.tail = tail;
        }
    }
}
