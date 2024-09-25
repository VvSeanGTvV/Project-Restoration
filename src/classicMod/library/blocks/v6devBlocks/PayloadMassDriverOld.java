package classicMod.library.blocks.v6devBlocks;


import arc.Core;
import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.Queue;
import arc.util.*;
import arc.util.io.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.world.blocks.payloads.*;
import mindustry.world.meta.*;

import static classicMod.library.blocks.v6devBlocks.PayloadMassDriverOld.PayloadDriverState.*;
import static mindustry.Vars.*;

public class PayloadMassDriverOld extends PayloadBlock {
    public float range = 100f;
    public float rotateSpeed = 2f;
    public float length = 89 / 8f;
    public float knockback = 5f;
    public float reloadTime = 30f;
    public float chargeTime = 100f;
    public float maxPayloadSize = 3;
    public float grabWidth = 8f, grabHeight = 11 / 4f;
    public Effect shootEffect = Fx.shootBig2;
    public Effect smokeEffect = Fx.shootPayloadDriver;
    public Effect receiveEffect = Fx.payloadReceive;
    public Sound shootSound = Sounds.shootBig;
    public float shake = 3f;
    public Effect transferEffect = new Effect(11f, 300f, e -> {
        if (!(e.data instanceof PayloadMassDriverData data)) return;
        Tmp.v1.set(data.x, data.y).lerp(data.ox, data.oy, Interp.sineIn.apply(e.fin()));
        data.payload.set(Tmp.v1.x, Tmp.v1.y, e.rotation);
        data.payload.draw();
    }).layer(Layer.flyingUnitLow - 1);
    public TextureRegion baseRegion;
    public TextureRegion capRegion;
    public TextureRegion leftRegion;
    public TextureRegion rightRegion;
    public TextureRegion capOutlineRegion;
    public TextureRegion leftOutlineRegion;
    public TextureRegion rightOutlineRegion;
    public TextureRegion arrow;

    public PayloadMassDriverOld(String name) {
        super(name);
        update = true;
        solid = true;
        configurable = true;
        hasPower = true;
        outlineIcon = true;
        sync = true;
        rotate = true;
        outputsPayload = true;
        //point2 is relative
        config(Point2.class, (PayloadDriverBuild tile, Point2 point) -> tile.link = Point2.pack(point.x + tile.tileX(), point.y + tile.tileY()));
        config(Integer.class, (PayloadDriverBuild tile, Integer point) -> tile.link = point);
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.payloadCapacity, maxPayloadSize, StatUnit.blocksSquared);
        stats.add(Stat.reload, 60f / (chargeTime + reloadTime), StatUnit.seconds);
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find(name + "-base");
        capRegion = Core.atlas.find(name + "-cap");
        leftRegion = Core.atlas.find(name + "-left");
        rightRegion = Core.atlas.find(name + "-right");
        arrow = Core.atlas.find("bridge-arrow");
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{baseRegion, outRegion, region};
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        super.drawPlace(x, y, rotation, valid);
        Drawf.dashCircle(x * tilesize, y * tilesize, range, Pal.accent);
        //check if a mass driver is selected while placing this driver
        if (!control.input.config.isShown()) return;
        Building selected = control.input.config.getSelected();
        if (selected == null || selected.block != this || !selected.within(x * tilesize, y * tilesize, range)) return;
        //if so, draw a dotted line towards it while it is in range
        float sin = Mathf.absin(Time.time, 6f, 1f);
        Tmp.v1.set(x * tilesize + offset, y * tilesize + offset).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
        float x2 = x * tilesize - Tmp.v1.x, y2 = y * tilesize - Tmp.v1.y,
                x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
        int segs = (int) (selected.dst(x * tilesize, y * tilesize) / tilesize);
        Lines.stroke(4f, Pal.gray);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Lines.stroke(2f, Pal.placing);
        Lines.dashLine(x1, y1, x2, y2, segs);
        Draw.reset();
    }

    @Override
    public TextureRegion[] makeIconRegions() {
        return new TextureRegion[]{leftRegion, rightRegion, capRegion};
    }

    @Override
    public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list) {
        Draw.rect(baseRegion, req.drawx(), req.drawy());
        Draw.rect(topRegion, req.drawx(), req.drawy());
        Draw.rect(outRegion, req.drawx(), req.drawy(), req.rotation * 90);
        Draw.rect(region, req.drawx(), req.drawy());
    }

    @Override
    public void createIcons(MultiPacker packer) {
        super.createIcons(packer);

        createOutline(packer, Core.atlas.find(name + "-cap"));
        createOutline(packer, Core.atlas.find(name + "-left"));
        createOutline(packer, Core.atlas.find(name + "-right"));
    }

    void createOutline(MultiPacker packer, TextureRegion textureRegion) {
        var atlas = textureRegion.asAtlas();
        if (atlas != null) {
            String regionName = atlas.name;
            Pixmap outlined = Pixmaps.outline(Core.atlas.getPixmap(atlas), outlineColor, outlineRadius);

            Drawf.checkBleed(outlined);

            packer.add(MultiPacker.PageType.main, regionName + "-outline", outlined);
        }
    }


    public class PayloadDriverBuild extends PayloadBlockBuild<Payload> {
        public int link = -1;
        public float turretRotation = 90;
        public float reload = 0f, charge = 0f;
        public float targetSize = grabWidth * 2f, curSize = targetSize;
        public float payLength = 0f;
        public boolean loaded;
        public boolean charging;
        public PayloadDriverState state = idle;
        public Queue<Building> waitingShooters = new Queue<>();
        public Payload recPayload;

        public Building currentShooter() {
            return waitingShooters.isEmpty() ? null : waitingShooters.first();
        }

        @Override
        public void updateTile() {
            Building link = world.build(this.link);
            boolean hasLink = linkValid();
            //discharge when charging isn't happening
            if (!charging) {
                charge -= Time.delta * 10f;
                if (charge < 0) charge = 0f;
            }
            curSize = Mathf.lerpDelta(curSize, targetSize, 0.05f);
            targetSize = grabWidth * 2f;
            if (payload != null) {
                targetSize = payload.size();
            }
            charging = false;
            if (hasLink) {
                this.link = link.pos();
            }
            //reload regardless of state
            reload -= edelta() / reloadTime;
            if (reload < 0) reload = 0f;
            var current = currentShooter();
            //cleanup waiting shooters that are not valid
            if(current != null &&
                    !(
                            current instanceof PayloadDriverBuild entity &&
                                    current.isValid() &&
                                    entity.efficiency > 0 && entity.block == block &&
                                    entity.link == pos() && within(current, range)
                    )){
                waitingShooters.removeFirst();
            }

            //switch states
            if (state == idle) {
                //start accepting when idle and there's space
                if (!waitingShooters.isEmpty() && payload == null) {
                    state = accepting;
                } else if (hasLink) { //switch to shooting if there's a valid link.
                    state = shooting;
                }
            }
            //dump when idle or accepting
            if ((state == idle || state == accepting) && payload != null) {
                if (loaded) {
                    payLength -= payloadSpeed * delta();
                    if (payLength <= 0f) {
                        loaded = false;
                        payVector.setZero();
                        payRotation = Angles.moveToward(payRotation, turretRotation + 180f, payloadRotateSpeed * delta());
                    }
                } else {
                    moveOutPayload();
                }
            }
            //skip when there's no power
            if (efficiency <= 0f) {
                return;
            }
            if (state == accepting) {
                //if there's nothing shooting at this or items are full, bail out
                if (currentShooter() == null || payload != null) {
                    state = idle;
                    return;
                }
                if (currentShooter().getPayload() != null) {
                    targetSize = recPayload == null ? currentShooter().getPayload().size() : recPayload.size();
                }
                //align to shooter rotation
                turretRotation = Angles.moveToward(turretRotation, tile.angleTo(currentShooter()), rotateSpeed * efficiency());
            } else if (state == shooting) {
                //if there's nothing to shoot at OR someone wants to shoot at this thing, bail
                if (!hasLink || (!waitingShooters.isEmpty() && payload == null)) {
                    state = idle;
                    return;
                }
                float targetRotation = tile.angleTo(link);
                boolean movedOut = false;
                payRotation = Angles.moveToward(payRotation, turretRotation, payloadRotateSpeed * delta());
                if (loaded) {
                    float loadLength = length - reload * knockback;
                    payLength += payloadSpeed * delta();
                    if (payLength >= loadLength) {
                        payLength = loadLength;
                        movedOut = true;
                    }
                } else if (moveInPayload()) {
                    payLength = 0f;
                    loaded = true;
                }
                //make sure payload firing can happen
                if (movedOut && payload != null && link.getPayload() == null) {
                    var other = (PayloadDriverBuild) link;
                    if (!other.waitingShooters.contains(this)) {
                        other.waitingShooters.addLast(this);
                    }
                    if (reload <= 0) {
                        //align to target location
                        turretRotation = Angles.moveToward(turretRotation, targetRotation, rotateSpeed * efficiency());
                        //fire when it's the first in the queue and angles are ready.
                        if (other.currentShooter() == this &&
                                other.state == accepting &&
                                other.reload <= 0f &&
                                Angles.within(turretRotation, targetRotation, 1f) && Angles.within(other.turretRotation, targetRotation + 180f, 1f)) {
                            charge += edelta();
                            charging = true;
                            if (charge >= chargeTime) {
                                float cx = Angles.trnsx(turretRotation, length), cy = Angles.trnsy(turretRotation, length);
                                //effects
                                shootEffect.at(x + cx, y + cy, turretRotation);
                                smokeEffect.at(x, y, turretRotation);
                                Effect.shake(shake, shake, this);
                                shootSound.at(this, Mathf.random(0.9f, 1.1f));
                                transferEffect.at(x + cx, y + cy, turretRotation, new PayloadMassDriverData(x + cx, y + cy, other.x - cx, other.y - cy, payload));
                                Payload pay = payload;
                                other.recPayload = payload;
                                Time.run(transferEffect.lifetime, () -> {
                                    receiveEffect.at(other.x - cx / 2f, other.y - cy / 2f, other.turretRotation);
                                    Effect.shake(shake, shake, this);
                                    //transfer payload
                                    other.reload = 1f;
                                    other.handlePayload(this, pay);
                                    other.payVector.set(-cx, -cy);
                                    other.payRotation = turretRotation;
                                    other.payLength = length;
                                    other.loaded = true;
                                    other.updatePayload();
                                    other.recPayload = null;
                                    if (other.waitingShooters.size != 0 && other.waitingShooters.first() == this) {
                                        other.waitingShooters.removeFirst();
                                    }
                                    other.state = idle;
                                });
                                //reset state after shooting immediately
                                payload = null;
                                payLength = 0f;
                                loaded = false;
                                state = idle;
                                reload = 1f;
                            }
                        }
                    }
                }
            }
        }

        @Override
        public double sense(LAccess sensor) {
            if (sensor == LAccess.progress) return Mathf.clamp(1f - reload / reloadTime);
            return super.sense(sensor);
        }

        @Override
        public void updatePayload() {
            if (payload != null) {
                if (loaded) {
                    payload.set(x + Angles.trnsx(turretRotation, payLength), y + Angles.trnsy(turretRotation, payLength), payRotation);
                } else {
                    payload.set(x + payVector.x, y + payVector.y, payRotation);
                }
            }
        }

        @Override
        public void draw() {
            if (capOutlineRegion == null) capOutlineRegion = Core.atlas.find(name + "-cap-outline");
            if (leftOutlineRegion == null) leftOutlineRegion = Core.atlas.find(name + "-left-outline");
            if (rightOutlineRegion == null) rightOutlineRegion = Core.atlas.find(name + "-right-outline");
            float
                    tx = x + Angles.trnsx(turretRotation + 180f, reload * knockback),
                    ty = y + Angles.trnsy(turretRotation + 180f, reload * knockback), r = turretRotation - 90;
            Draw.rect(baseRegion, x, y);
            //draw input
            for (int i = 0; i < 4; i++) {
                if (blends(i) && i != rotation) {
                    Draw.rect(inRegion, x, y, (i * 90) - 180);
                }
            }
            Draw.rect(outRegion, x, y, rotdeg());
            if (payload != null) {
                updatePayload();
                Draw.z(loaded ? Layer.blockOver + 0.2f : Layer.blockOver);
                payload.draw();
            }
            Draw.z(Layer.blockOver + 0.1f);
            Draw.rect(topRegion, x, y);
            Draw.z(Layer.turret);
            //TODO
            Drawf.shadow(region, tx - (size / 2f), ty - (size / 2f), r);
            Tmp.v1.trns(turretRotation, 0, -(curSize / 2f - grabWidth));
            Tmp.v2.trns(rotation, -Math.max(curSize / 2f - grabHeight - length, 0f), 0f);
            float rx = tx + Tmp.v1.x + Tmp.v2.x, ry = ty + Tmp.v1.y + Tmp.v2.y;
            float lx = tx - Tmp.v1.x + Tmp.v2.x, ly = ty - Tmp.v1.y + Tmp.v2.y;
            //Draw.rect(capOutlineRegion, tx, ty, r);
            Draw.rect(leftOutlineRegion, lx, ly, r);
            Draw.rect(rightOutlineRegion, rx, ry, r);
            Draw.rect(leftRegion, lx, ly, r);
            Draw.rect(rightRegion, rx, ry, r);
            Draw.rect(capRegion, tx, ty, r);
            Draw.z(Layer.effect);
            if (charge > 0 && linkValid()) {
                Building link = world.build(this.link);
                float fin = Interp.pow2Out.apply(charge / chargeTime), fout = 1f - fin, len = length * 1.8f, w = curSize / 2f + 7f * fout;
                Vec2 right = Tmp.v1.trns(turretRotation, len, w);
                Vec2 left = Tmp.v2.trns(turretRotation, len, -w);
                Lines.stroke(fin * 1.2f, Pal.accent);
                Lines.line(x + left.x, y + left.y, link.x - right.x, link.y - right.y);
                Lines.line(x + right.x, y + right.y, link.x - left.x, link.y - left.y);
                for (int i = 0; i < 4; i++) {
                    Tmp.v3.set(x, y).lerp(link.x, link.y, 0.5f + (i - 2) * 0.1f);
                    Draw.scl(fin * 1.1f);
                    Draw.rect(arrow, Tmp.v3.x, Tmp.v3.y, turretRotation);
                    Draw.scl();
                }
                Draw.reset();
            }
        }

        @Override
        public void drawConfigure() {
            float sin = Mathf.absin(Time.time, 6f, 1f);
            Draw.color(Pal.accent);
            Lines.stroke(1f);
            Drawf.circles(x, y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.accent);
            for (var shooter : waitingShooters) {
                Drawf.circles(shooter.x, shooter.y, (tile.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(shooter.x, shooter.y, x, y, size * tilesize + sin, 4f + sin, Pal.place);
            }
            if (linkValid()) {
                Building target = world.build(link);
                Drawf.circles(target.x, target.y, (target.block().size / 2f + 1) * tilesize + sin - 2f, Pal.place);
                Drawf.arrow(x, y, target.x, target.y, size * tilesize + sin, 4f + sin);
            }
            Drawf.dashCircle(x, y, range, Pal.accent);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other) {
            if (this == other) {
                configure(-1);
                return false;
            }
            if (link == other.pos()) {
                configure(-1);
                return false;
            } else if (other.block instanceof PayloadMassDriver && other.dst(tile) <= range && other.team == team) {
                configure(other.pos());
                return false;
            }
            return true;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return super.acceptPayload(source, payload) && payload.size() <= maxPayloadSize * tilesize;
        }

        protected boolean linkValid() {
            return link != -1 && world.build(this.link) instanceof PayloadDriverBuild other && other.block == block && other.team == team && within(other, range);
        }

        @Override
        public Point2 config() {
            if(tile == null) return null;
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(link);
            write.f(turretRotation);
            write.b((byte) state.ordinal());
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            link = read.i();
            turretRotation = read.f();
            state = PayloadDriverState.all[read.b()];
        }
    }

    public static class PayloadMassDriverData {
        public float x, y, ox, oy;
        public Payload payload;

        public PayloadMassDriverData(float x, float y, float ox, float oy, Payload payload) {
            this.x = x;
            this.y = y;
            this.ox = ox;
            this.oy = oy;
            this.payload = payload;
        }
    }

    public enum PayloadDriverState {
        idle, accepting, shooting;
        public static final PayloadDriverState[] all = values();
    }
}