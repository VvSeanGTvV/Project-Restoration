package classicMod.library.advanceContent;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;

import static mindustry.Vars.world;

public class TentacleUnitType extends UnitType {

    /** Tentacle End Texture **/
    public TextureRegion TentacleEnd;

    /** Tentacle Middle Texture **/
    public TextureRegion Tentacle;

    /** Tentacle Length **/
    public int TentacleLength = 21;

    /** Tentacle Offset Position **/
    public Vec3[] TentaclesOffset = new Vec3[]{new Vec3(45, 0, 175), new Vec3(23, -43, 175), new Vec3(-23, -43, 185), new Vec3(-43, 0, 185)};

    public TentacleUnitType(String name) {
        super(name);

    }

    @Override
    public void draw(Unit unit) {
        TentacleTiming.timing += 0.07f * Time.delta;
        Tentacle = Core.atlas.find(name + "-tentacle");
        TentacleEnd = Core.atlas.find(name + "-tentacle-end");
        Draw.z(Layer.flyingUnit);
        super.draw(unit);
        Draw.z(Layer.flyingUnit - 1f);
        drawMultiTentacles(unit, 5, TentacleLength, 7, 1, 1, 1, 1, 1);
    }

    @Override
    public void setStats() {
        super.setStats();
        if(health >= Float.MAX_VALUE){
            stats.remove(Stat.health);
            stats.add(Stat.health, "NaN");
            stats.remove(Stat.speed);
            stats.add(Stat.speed, "NaN");
        }
    }

    private final Vec2 vec1 = new Vec2();
    private final Vec2 vec2 = new Vec2();
    private final Vec2 tPos = new Vec2();

    public void drawMultiTentacles(Unit unit, int startingLength, int segmentLength, int segmentCount, float segTimeOffset, float timeOffset, float moveMag, float moveMagOffset, float moveScale) {
        for (Vec3 buildTentacle : TentaclesOffset) {
            drawTentacle(unit, buildTentacle.z, buildTentacle.x, buildTentacle.y, startingLength, segmentLength, segmentCount, segTimeOffset, timeOffset, moveMag, moveMagOffset, moveScale);
        }
    }

    public void drawTentacle(Unit unit, float offsetAngle, float offsetX, float offsetY, int startingLength, int segmentLength, int segmentCount, float segTimeOffset, float timeOffset, float moveMag, float moveMagOffset, float moveScale) {

        //const vec1 = new Vec2();
        //const vec2 = new Vec2();
        //const tPos = new Vec2();
        tPos.trns(unit.rotation + offsetAngle - 90, offsetX, startingLength + offsetY);
        //var angleCorrection = -lineageCalculate(vec1, tPos);
        vec2.trns(unit.rotation + offsetAngle - 90, offsetX, startingLength + offsetY);
        for (var i = 0; i < segmentCount; i++) {
            var sine = Mathf.sin(TentacleTiming.timing + (i * segTimeOffset) + timeOffset, moveScale, moveMag + (i * moveMagOffset));

            vec1.set(tPos.x, tPos.y);

            var region = i != segmentCount - 1 ? Tentacle : TentacleEnd;

            Draw.rect(region, unit.x + vec1.x, unit.y + vec1.y, unit.rotation + offsetAngle + sine - 90);
            drawTentacleShadow(unit, region, unit.x + vec1.x, unit.y + vec1.y, unit.rotation + offsetAngle + sine - 90);

            tPos.set(vec2.trns(unit.rotation + offsetAngle + sine, segmentLength)).add(vec1.x, vec1.y);
        }
    }

    public void drawTentacleShadow(Unit unit, TextureRegion region , float x1, float y1, float rot1) {
        float e = Mathf.clamp(unit.elevation, shadowElevation, 1f) * shadowElevationScl * (1f - unit.drownTime);
        float x = x1 + shadowTX * e, y = y1 + shadowTY * e;
        Floor floor = world.floorWorld(x, y);

        float dest = floor.canShadow ? 1f : 0f;
        //yes, this updates state in draw()... which isn't a problem, because I don't want it to be obvious anyway
        unit.shadowAlpha = unit.shadowAlpha < 0 ? dest : Mathf.approachDelta(unit.shadowAlpha, dest, 0.11f);
        Draw.color(Pal.shadow, Pal.shadow.a * unit.shadowAlpha);

        Draw.rect(region, x1 + shadowTX * e, y1 + shadowTY * e, rot1);
        Draw.color();
    }

    public static class TentacleTiming {
        public static float timing = 0f;
    }

}