package classicMod.library.unitType;

import arc.Core;
import arc.func.Func;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.io.JsonIO;
import mindustry.type.Weapon;

public class WingBlade extends Weapon {
    public Func<WingBlade, WingBladeMount> mountType;
    public final String spriteName;
    public TextureRegion bladeRegion, blurRegion, bladeOutlineRegion, shadeRegion;

    public float x = 0f, y = 0f;

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
        mountType = WingBlade.WingBladeMount::new;
    }

    public static class WingBladeMount extends WeaponMount{
        public WingBlade blade;
        public float bladeRotation;


        public WingBladeMount(WingBlade blade){
            super(blade);
            this.blade = blade;
        }
    }

    @Override
    public void update(Unit unit, WeaponMount mount) {
        WingBladeMount wingMount = (WingBladeMount)mount;
        OrnitopterUnitType type = (OrnitopterUnitType)unit.type;
        wingMount.bladeRotation += ((bladeMaxMoveAngle * type.bladeMoveSpeedScl) + bladeMinMoveAngle) * Time.delta;
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
