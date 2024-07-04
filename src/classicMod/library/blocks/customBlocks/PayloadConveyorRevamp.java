package classicMod.library.blocks.customBlocks;

import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
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
                var offsetDegrees = item.rotation() - 90f;
                if(item.content() instanceof UnitType unitType){
                    float z = Draw.z();
                    Draw.z(z - 0.02f);

                    if(unitType.treadRegion.found()){
                        Draw.rect(unitType.treadRegion, item.x(), item.y(), offsetDegrees);
                    }

                    for(int i = 0; i < unitType.weapons.size; i++){
                        Weapon mount = unitType.weapons.get(i);

                        if(mount.outlineRegion.found()) {
                            if (!mount.top) {
                                Draw.z(z + mount.layerOffset);

                                var wx = item.x() + Angles.trnsx(offsetDegrees, mount.x, mount.y);
                                var wy = item.y() + Angles.trnsy(offsetDegrees, mount.x, mount.y);

                                Draw.xscl = -Mathf.sign(mount.flipSprite);
                                Draw.rect(mount.outlineRegion, wx, wy, offsetDegrees + mount.mountType.get(mount).rotation);
                                Draw.xscl = 1f;

                                Draw.z(z);

                            }
                        }
                    }
                    if(unitType.outlineRegion.found()) Draw.rect(unitType.outlineRegion, item.x(), item.y(), offsetDegrees);

                    Draw.z(z);
                    Draw.rect(unitType.region, item.x(), item.y(), offsetDegrees);

                    Draw.color(this.team.color);
                    Draw.rect(unitType.cellRegion, item.x(), item.y(), offsetDegrees);
                    Draw.color();

                    for(int i = 0; i < unitType.weapons.size; i++){
                        Weapon mount = unitType.weapons.get(i);

                        if(mount.region.found()) {

                            var wx = item.x() + Angles.trnsx(offsetDegrees, mount.x, mount.y);
                            var wy = item.y() + Angles.trnsy(offsetDegrees, mount.x, mount.y);

                            Draw.z(z + mount.layerOffset);

                            if(mount.top && mount.outlineRegion.found()) Draw.rect(mount.outlineRegion, wx, wy, offsetDegrees + mount.mountType.get(mount).rotation);

                            Draw.xscl = -Mathf.sign(mount.flipSprite);
                            Draw.rect(mount.region, wx, wy, offsetDegrees + mount.mountType.get(mount).rotation);

                            if(mount.cellRegion.found()){
                                Draw.color(this.team.color);
                                Draw.rect(mount.cellRegion, wx, wy, offsetDegrees + mount.mountType.get(mount).rotation);
                                Draw.color();
                            }

                            Draw.xscl = 1f;

                            Draw.z(z);
                        }
                    }

                    if(unitType.parts.size > 0) {
                        for (int i = 0; i < unitType.parts.size; i++) {
                            var part = unitType.parts.get(i);
                            DrawPart.params.set(0f, 0f, 0f, 0f, 0f, 0f, item.x(), item.y(), item.rotation());

                            part.draw(DrawPart.params);
                        }
                    }

                } else {
                    Draw.rect(item.content().fullIcon, item.x(), item.y());
                }
            }
        }
    }
}
