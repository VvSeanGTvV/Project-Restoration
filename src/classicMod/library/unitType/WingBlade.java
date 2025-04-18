package classicMod.library.unitType;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.io.JsonIO;

public class WingBlade implements Cloneable {
    public long drawSeed = 0;
    public Func<WingBlade, WingBladeMount> mountType;
    public final String spriteName;
    public TextureRegion bladeRegion, blurRegion, bladeOutlineRegion, shadeRegion;

    public float x = 0f, y = 0f;

    public float bladeRotation;
    public float bladeSizeScl = 1, shadeSizeScl = 1;
    /**
     * Blade max moving distance
     */
    public float bladeMaxMoveAngle = 12;
    /**
     * Blade min moving distance
     */
    public float bladeMinMoveAngle = 0f;

    public float layerOffset = 0.001f;

    public float blurAlpha = 0.9f;

    public WingBlade(String name) {
        this.spriteName = name;
        mountType = WingBladeMount::new;
    }

    @Override
    public WingBlade clone() {
        try {
            WingBlade clone = (WingBlade) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class WingBladeMount{
        public WingBlade blade;


        public WingBladeMount(WingBlade blade){
            this.blade = blade;
        }
    }

    public void update(Unit unit) {
        OrnitopterUnitType type = (OrnitopterUnitType)unit.type;
        bladeRotation += ((bladeMaxMoveAngle * type.bladeMoveSpeedScl) + bladeMinMoveAngle) * Time.delta;
        drawSeed++;
    }

    public void load() {
        bladeRegion = Core.atlas.find(spriteName);
        blurRegion = Core.atlas.find(spriteName + "-blur");
        bladeOutlineRegion = Core.atlas.find(spriteName + "-outline");
        shadeRegion = Core.atlas.find(spriteName + "-blur-shade");
    }

    // For mirroring
    public WingBlade copy() {
        return JsonIO.copy(this, new WingBlade(spriteName));
    }

}
