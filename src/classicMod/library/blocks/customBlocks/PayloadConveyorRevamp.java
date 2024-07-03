package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.payloads.PayloadConveyor;

import static mindustry.Vars.tilesize;

public class PayloadConveyorRevamp extends PayloadConveyor {
    public PayloadConveyorRevamp(String name) {
        super(name);
    }

    public class PayloadConveyorRevampBuild extends PayloadConveyorBuild {
        @Override
        public void draw() {
            //super.draw();

            float dst = 0.8f;

            float glow = Math.max((dst - (Math.abs(fract() - 0.5f) * 2)) / dst, 0);
            Draw.mixcol(team.color, glow);

            float s = tilesize * size;
            float trnext = s * fract(), trprev = s * (fract() - 1), rot = rotdeg();

            //next
            TextureRegion clipped = clipRegion(tile.getHitbox(Tmp.r1), tile.getHitbox(Tmp.r2).move(trnext, 0), topRegion);
            float widthNext = (s - clipped.width * clipped.scl()) * 0.5f;
            float heightNext = (s - clipped.height * clipped.scl()) * 0.5f;
            Tmp.v1.set(widthNext, heightNext).rotate(rot);
            Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, rot);

            //prev
            clipped = clipRegion(tile.getHitbox(Tmp.r1), tile.getHitbox(Tmp.r2).move(trprev, 0), topRegion);
            float widthPrev = (clipped.width * clipped.scl() - s) * 0.5f;
            float heightPrev = (clipped.height * clipped.scl() - s) * 0.5f;
            Tmp.v1.set(widthPrev, heightPrev).rotate(rot);
            Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, rot);

            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.alpha(1f - Interp.pow5In.apply(fract()));
                    //prev from back
                    Tmp.v1.set(widthPrev, heightPrev).rotate(i * 90 + 180);
                    Draw.rect(clipped, x + Tmp.v1.x, y + Tmp.v1.y, i * 90 + 180);
                }
            }

            Draw.reset();

            for (int i = 0; i < 4; i++) {
                if (!blends(i)) {
                    Draw.rect(edgeRegion, x, y, i * 90);
                }
            }

            Draw.z(Layer.blockOver);

            if (item != null) {
                if(item.content() instanceof UnitType unitType){
                    Draw.rect(unitType.region, item.x(), item.y(), rotdeg());
                    Draw.rect(unitType.cellRegion, item.x(), item.y(), rotdeg());
                    for(int i = 0; i < unitType.weapons.size; i++){
                        Weapon mount = unitType.weapons.get(i);
                        Draw.z(Layer.blockOver + 0.05f);
                        Vec2 yes = new Vec2(mount.x - item.x(), mount.y - item.y()).rotate(rotdeg());
                        Log.info(new Vec2(mount.x - item.x(), mount.y - item.y()));
                        Draw.rect(mount.region, yes.x, yes.y, item.rotation());
                    }
                } else {
                    Draw.rect(item.content().fullIcon, item.x(), item.y());
                }
            }
        }
    }
}
