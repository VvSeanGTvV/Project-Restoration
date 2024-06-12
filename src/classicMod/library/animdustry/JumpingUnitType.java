package classicMod.library.animdustry;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import classicMod.library.ai.JumpingAI;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;

public class JumpingUnitType extends UnitType {

    public TextureRegion ouch;

    public JumpingUnitType(String name) {
        super(name);
        controller = u -> new JumpingAI();
    }

    @Override
    public void update(Unit unit) {
        if(unit.controller() instanceof JumpingAI ai) { ai.timing += 0.1f * Time.delta; }
    }

    @Override
    public void draw(Unit unit) {
        if(unit.controller() instanceof JumpingAI ai) {
            ouch = Core.atlas.find(name + "-hit"); region = Core.atlas.find(name);

            if(unit.rotation > 180){
                Draw.xscl = -Mathf.sign(true);
            } else {
                Draw.xscl = 1f;
            }

            var sine = Mathf.sin(ai.timing);
            Draw.z(Layer.groundUnit);
            if (sine < -0.85f) ai.timing = 2f;
            if (sine > 0f && !ai.stopMoving) {
                Draw.rect(region, unit.x, unit.y + 2 - sine * 2, ((float) region.width / 2) + sine * 5, ((float) region.height / 2) - sine * 10);
                if(ai.hit) Draw.rect(ouch, unit.x, unit.y + 2 - sine * 2, ((float) ouch.width / 2) + sine * 5, ((float) ouch.height / 2) - sine * 10);
            } else {
                Draw.rect(region, unit.x, unit.y + 2, (float) region.width / 2, (float) region.height / 2);
                if(ai.hit) Draw.rect(ouch, unit.x, unit.y + 2, ((float) ouch.width / 2), ((float) ouch.height / 2));
            }
        }

    }

    public float getTimingSine(JumpingAI ai){
        return Mathf.sin(ai.timing);
    }
}
