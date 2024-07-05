package classicMod.library.drawCustom;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.part.DrawPart;
import mindustry.graphics.Pal;

import static arc.math.Mathf.rand;

public class CircleForceDraw extends DrawPart{
    public float x = 0, y = 0;
    public float orbRadius = 4.1f, orbMidScl = 0.33f, orbSinScl = 8f, orbSinMag = 1f, layerOffset = 0f;
    public Color color = Pal.suppress;
    public float layer = -1;
    public boolean under;

    public int particles = 15;
    public float particleSize = 4f;
    public float particleLen = 7f;
    public float rotateScl = 3f;
    public float particleLife = 110f;
    public boolean active = true;
    public Interp particleInterp = f -> Interp.circleOut.apply(Interp.slope.apply(f));
    public Color particleColor = Pal.sap.cpy();
    public int id;
    public Blending blending = Blending.normal;
    @Override
    public void draw(PartParams params) {
        float xC = params.x;
        float yC = params.y;
        float rotation = params.rotation;

        float z = Draw.z();
        if(layer > 0) Draw.z(layer);
        if(under) Draw.z(z - 0.0001f);
        Draw.z(Draw.z() + layerOffset);

        float rad = orbRadius + Mathf.absin(orbSinScl, orbSinMag);
        Tmp.v1.set(x, y).rotate(rotation);
        float rx = xC + Tmp.v1.x, ry = yC + Tmp.v1.y;

        float base = (Time.time / particleLife);
        rand.setSeed(id + hashCode());
        Draw.color(particleColor);
        for(int i = 0; i < particles; i++){
            float fin = (rand.random(1f) + base) % 1f, fout = 1f - fin;
            float angle = rand.random(360f) + (Time.time / rotateScl + rotation) % 360f;
            float len = particleLen * particleInterp.apply(fout);
            Fill.circle(
                    rx + Angles.trnsx(angle, len),
                    ry + Angles.trnsy(angle, len),
                    particleSize * Mathf.slope(fin)
            );
        }

        Lines.stroke(2f);

        Draw.color(color);
        Lines.circle(rx, ry, rad);

        Draw.color(color);
        Fill.circle(rx, ry, rad * orbMidScl);

        Draw.blend(Blending.additive);
        Fill.circle(rx, ry, rad + Lines.getStroke() * orbMidScl);
        Draw.blend();

        if(active){
            //TODO draw range when selected?
        }

        Draw.reset();
    }

    @Override
    public void load(String name) {

    }

    public CircleForceDraw(){
    }
}
